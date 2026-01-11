package com.productreview.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productreview.dto.TranslateResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GroqTranslationService {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(8);
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private static final int MAX_TEXTS = 30;
    private static final int MAX_TOTAL_CHARS = 10_000;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public TranslateResponseDTO translateBatch(List<String> texts, String lang) {
        String safeLang = normalizeLang(lang);
        List<String> safeTexts = normalizeTexts(texts);

        if (safeTexts.isEmpty()) {
            return new TranslateResponseDTO(safeLang, "LOCAL", List.of());
        }

        // If target language is English, just return original texts.
        if (Objects.equals(safeLang, "en")) {
            return new TranslateResponseDTO(safeLang, "LOCAL", safeTexts);
        }

        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return new TranslateResponseDTO(safeLang, "LOCAL", safeTexts);
        }

        // Cache per text
        List<String> out = new ArrayList<>(safeTexts.size());
        List<Integer> missIdx = new ArrayList<>();
        List<String> misses = new ArrayList<>();

        for (int i = 0; i < safeTexts.size(); i++) {
            String s = safeTexts.get(i);
            String key = cacheKey(safeLang, s);
            CacheEntry cached = cache.get(key);
            if (cached != null && !cached.isExpired()) {
                out.add(cached.value);
            } else {
                out.add(null);
                missIdx.add(i);
                misses.add(s);
            }
        }

        if (misses.isEmpty()) {
            return new TranslateResponseDTO(safeLang, "AI", out);
        }

        List<String> translatedMisses;
        try {
            translatedMisses = callGroqTranslate(apiKey, misses, safeLang);
        } catch (Exception e) {
            // Fallback: fill misses with original
            for (int k = 0; k < missIdx.size(); k++) {
                out.set(missIdx.get(k), misses.get(k));
            }
            return new TranslateResponseDTO(safeLang, "LOCAL", out);
        }

        // Align lengths; if mismatch fallback to originals
        if (translatedMisses == null || translatedMisses.size() != misses.size()) {
            for (int k = 0; k < missIdx.size(); k++) {
                out.set(missIdx.get(k), misses.get(k));
            }
            return new TranslateResponseDTO(safeLang, "LOCAL", out);
        }

        for (int k = 0; k < missIdx.size(); k++) {
            int idx = missIdx.get(k);
            String translated = safeText(translatedMisses.get(k));
            if (translated == null) translated = misses.get(k);
            out.set(idx, translated);
            cache.put(cacheKey(safeLang, misses.get(k)), new CacheEntry(translated));
        }

        return new TranslateResponseDTO(safeLang, "AI", out);
    }

    private List<String> callGroqTranslate(String apiKey, List<String> texts, String lang) throws Exception {
        String model = System.getenv("GROQ_MODEL");
        if (model == null || model.trim().isEmpty()) {
            model = "llama-3.3-70b-versatile";
        }

        String prompt = buildPrompt(texts, lang);

        Map<String, Object> req = new HashMap<>();
        req.put("model", model);
        req.put("temperature", 0.1);
        req.put("messages", List.of(
                Map.of(
                        "role", "system",
                        "content", "You translate text for an e-commerce app. Return STRICT JSON ONLY with key: translations (array of strings). Preserve brand/model terms. Do not add commentary. No markdown. Output language: " + lang + "."
                ),
                Map.of(
                        "role", "user",
                        "content", prompt
                )
        ));

        String body = objectMapper.writeValueAsString(req);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Groq translate failed with status " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
        if (contentNode.isMissingNode() || contentNode.asText().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Groq returned empty response");
        }

        String raw = contentNode.asText();
        String json = extractJsonObject(raw);
        JsonNode parsed = objectMapper.readTree(json);
        JsonNode arr = parsed.path("translations");
        if (!arr.isArray()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Groq response missing translations");
        }

        List<String> out = new ArrayList<>();
        for (JsonNode n : arr) {
            out.add(n.asText(""));
        }
        return out;
    }

    private String buildPrompt(List<String> texts, String lang) {
        StringBuilder sb = new StringBuilder();
        sb.append("Translate each item in the following JSON array to language: ").append(lang).append(". ");
        sb.append("Return STRICT JSON ONLY: {\"translations\": [..]} with same length/order. ");
        sb.append("Preserve product/brand/model terms exactly. Keep punctuation natural.\n\n");
        sb.append("Input texts:\n");
        sb.append("[");
        for (int i = 0; i < texts.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("\"").append(escape(texts.get(i))).append("\"");
        }
        sb.append("]\n");
        return sb.toString();
    }

    private String normalizeLang(String lang) {
        String l = (lang == null ? "" : lang).trim().toLowerCase(Locale.ROOT);
        if (l.contains("-")) l = l.substring(0, l.indexOf('-'));
        if (Objects.equals(l, "tr") || Objects.equals(l, "es") || Objects.equals(l, "en")) return l;
        return "en";
    }

    private List<String> normalizeTexts(List<String> texts) {
        if (texts == null) return List.of();

        List<String> out = new ArrayList<>();
        int total = 0;
        for (String t : texts) {
            if (t == null) {
                out.add("");
                continue;
            }
            String s = t.trim();
            if (s.length() > 2000) s = s.substring(0, 2000);
            total += s.length();
            out.add(s);
            if (out.size() >= MAX_TEXTS) break;
            if (total >= MAX_TOTAL_CHARS) break;
        }
        return out;
    }

    private String safeText(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        if (t.length() > 2000) t = t.substring(0, 2000);
        return t;
    }

    private String extractJsonObject(String raw) {
        String s = raw == null ? "" : raw.trim();
        int start = s.indexOf('{');
        int end = s.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Groq response was not valid JSON");
        }
        return s.substring(start, end + 1);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }

    private String cacheKey(String lang, String text) {
        return lang + "|" + sha(text);
    }

    private String sha(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest((text == null ? "" : text).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            return String.valueOf(Objects.hash(text));
        }
    }

    private static final class CacheEntry {
        private final String value;
        private final Instant createdAt;

        private CacheEntry(String value) {
            this.value = value;
            this.createdAt = Instant.now();
        }

        private boolean isExpired() {
            return Instant.now().isAfter(createdAt.plus(CACHE_TTL));
        }
    }
}
