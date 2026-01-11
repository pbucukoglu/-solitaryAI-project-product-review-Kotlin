package com.productreview.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productreview.dto.ReviewSummaryDTO;
import com.productreview.dto.ReviewSummaryResponseDTO;
import com.productreview.entity.Product;
import com.productreview.entity.Review;
import com.productreview.repository.ProductRepository;
import com.productreview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GroqReviewSummaryService {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(6);
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public ReviewSummaryResponseDTO getReviewSummary(Long productId, int limit, String lang) {
        String safeLang = normalizeLang(lang);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + productId));

        Long reviewCount = product.getReviewCount() == null ? 0L : product.getReviewCount();
        Double averageRating = product.getAverageRating() == null ? 0.0 : product.getAverageRating();

        LocalDateTime latestCreatedAt = reviewRepository.findLatestCreatedAtByProductId(productId);
        String versionKey = String.valueOf(latestCreatedAt) + "|" + reviewCount;
        String cacheKey = productId + "|" + safeLang + "|" + versionKey;

        CacheEntry cached = cache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.value;
        }

        int safeLimit = Math.max(1, Math.min(100, limit));

        List<Review> latest = reviewRepository.findLatestByProductId(productId, PageRequest.of(0, safeLimit));
        List<Review> usable = new ArrayList<>();
        for (Review r : latest) {
            if (r == null) continue;
            String c = r.getComment();
            if (c == null) continue;
            if (c.trim().isEmpty()) continue;
            usable.add(r);
        }

        if (usable.isEmpty()) {
            ReviewSummaryDTO local = buildLocalSummary(product, usable, safeLang);
            ReviewSummaryResponseDTO response = new ReviewSummaryResponseDTO(
                    productId,
                    safeLang,
                    "LOCAL",
                    averageRating,
                    reviewCount,
                    0L,
                    local.getTakeaway(),
                    local.getPros(),
                    local.getCons(),
                    local.getTopTopics(),
                    Instant.now().toString()
            );
            cache.put(cacheKey, new CacheEntry(response));
            return response;
        }

        ReviewSummaryDTO local = buildLocalSummary(product, usable, safeLang);
        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            ReviewSummaryResponseDTO response = new ReviewSummaryResponseDTO(
                    productId,
                    safeLang,
                    "LOCAL",
                    averageRating,
                    reviewCount,
                    (long) usable.size(),
                    local.getTakeaway(),
                    local.getPros(),
                    local.getCons(),
                    local.getTopTopics(),
                    Instant.now().toString()
            );
            cache.put(cacheKey, new CacheEntry(response));
            return response;
        }

        ReviewSummaryDTO ai = null;
        try {
            String prompt = buildPrompt(product, usable, safeLang);
            ai = callGroq(apiKey, prompt, safeLang);
        } catch (Exception ignored) {
            ai = null;
        }

        ReviewSummaryDTO result = ai != null && ai.getTakeaway() != null ? clamp(ai) : local;
        String source = (ai != null && ai.getTakeaway() != null) ? "AI" : "LOCAL";

        ReviewSummaryResponseDTO response = new ReviewSummaryResponseDTO(
                productId,
                safeLang,
                source,
                averageRating,
                reviewCount,
                (long) usable.size(),
                result.getTakeaway(),
                result.getPros(),
                result.getCons(),
                result.getTopTopics(),
                Instant.now().toString()
        );

        cache.put(cacheKey, new CacheEntry(response));
        return response;
    }

    private String buildPrompt(Product product, List<Review> reviews, String lang) {
        StringBuilder sb = new StringBuilder();
        sb.append("You summarize product reviews in a conservative, e-commerce style. ");
        sb.append("Return STRICT JSON ONLY with keys: takeaway (string), pros (array of strings), cons (array of strings), topTopics (array of strings). ");
        sb.append("Use neutral language (no marketing). Avoid absolute claims. Avoid emojis. ");
        sb.append("Do not mention AI/models. Preserve brand/model terms. ");
        sb.append("Max 3 pros/cons, max 5 topics. No markdown, no code fences, no extra text. ");
        sb.append("Output language: ").append(lang).append(".\n\n");

        // Category-specific topic guidance
        String category = product.getCategory() != null ? product.getCategory().toLowerCase() : "";
        if (category.contains("book") || category.contains("kitap")) {
            sb.append("For books, focus on topics like: Writing style, Plot, Characters, Length, Translation, Cover quality. ");
            sb.append("Do NOT include hardware topics like Battery, Camera, Performance.\n\n");
        } else if (category.contains("electronics") || category.contains("camera") || category.contains("phone") || category.contains("laptop") || category.contains("tablet")) {
            sb.append("For electronics, focus on topics like: Battery, Performance, Build quality, Screen, Camera, Price, Delivery. ");
            sb.append("Do NOT include irrelevant topics like Plot, Characters.\n\n");
        } else if (category.contains("clothing") || category.contains("giyim") || category.contains("shirt") || category.contains("pants") || category.contains("dress")) {
            sb.append("For clothing, focus on topics like: Fit, Fabric quality, Size, Color, Style, Durability, Price. ");
            sb.append("Do NOT include hardware topics like Battery, Screen, Performance.\n\n");
        } else if (category.contains("home") || category.contains("kitchen") || category.contains("furniture") || category.contains("decoration")) {
            sb.append("For home & kitchen, focus on topics like: Quality, Assembly, Size, Material, Price, Delivery, Packaging. ");
            sb.append("Do NOT include irrelevant topics like Battery, Screen, Camera.\n\n");
        } else if (category.contains("sports") || category.contains("outdoor") || category.contains("shoes") || category.contains("exercise")) {
            sb.append("For sports & outdoors, focus on topics like: Durability, Comfort, Fit, Material, Performance, Price, Weather resistance. ");
            sb.append("Do NOT include irrelevant topics like Battery, Screen, Camera.\n\n");
        } else {
            sb.append("For general products, focus on topics like: Quality, Price, Delivery, Packaging, Usability, Durability. ");
            sb.append("Only include topics actually mentioned in reviews.\n\n");
        }

        sb.append("Product:\n");
        sb.append("name: ").append(product.getName()).append("\n");
        sb.append("category: ").append(product.getCategory()).append("\n");
        sb.append("price: ").append(product.getPrice()).append("\n");
        sb.append("averageRating: ").append(product.getAverageRating()).append("\n");
        sb.append("reviewCount: ").append(product.getReviewCount()).append("\n\n");

        sb.append("Latest reviews (rating + comment):\n");
        for (Review r : reviews) {
            sb.append("- rating: ").append(r.getRating()).append("\n");
            sb.append("  comment: ").append(sanitize(r.getComment())).append("\n");
        }

        sb.append("\nGuidelines:\n");
        sb.append("- Write 1 neutral takeaway sentence.\n");
        sb.append("- Extract up to 3 pros and 3 cons as short phrases.\n");
        sb.append("- Extract up to 5 topics relevant to the product category.\n");
        sb.append("- Only include topics actually mentioned in reviews.\n");
        sb.append("- Use conservative, factual language.\n");

        return sb.toString();
    }

    private ReviewSummaryDTO callGroq(String apiKey, String prompt, String lang) {
        try {
            String model = System.getenv("GROQ_MODEL");
            if (model == null || model.trim().isEmpty()) {
                model = "llama-3.3-70b-versatile";
            }

            Map<String, Object> req = new HashMap<>();
            req.put("model", model);
            req.put("temperature", 0.2);
            req.put("messages", List.of(
                    Map.of(
                            "role", "system",
                            "content", "Return STRICT JSON ONLY with keys: takeaway (string), pros (array of strings), cons (array of strings), topTopics (array of strings). Neutral language only; no marketing; no absolute claims; no emojis; do not mention AI. Max 3 pros/cons, max 5 topics. Output language: " + lang + "."
                    ),
                    Map.of(
                            "role", "user",
                            "content", prompt
                    )
            ));

            String body = objectMapper.writeValueAsString(req);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String snippet = safeSnippet(response.body());
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Groq call failed with status " + response.statusCode() + (snippet.isEmpty() ? "" : (": " + snippet))
                );
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode contentNode = root
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content");

            if (contentNode.isMissingNode() || contentNode.asText().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Groq returned empty response");
            }

            String raw = contentNode.asText();
            String json = extractJsonObject(raw);
            JsonNode parsed = objectMapper.readTree(json);

            String takeaway = parsed.path("takeaway").asText(null);
            List<String> pros = asStringList(parsed.path("pros"));
            List<String> cons = asStringList(parsed.path("cons"));
            List<String> topTopics = asStringList(parsed.path("topTopics"));
            if (takeaway == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Groq response missing takeaway");
            }
            return clamp(new ReviewSummaryDTO(takeaway, pros, cons, topTopics));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Groq call failed");
        }
    }

    private ReviewSummaryDTO clamp(ReviewSummaryDTO in) {
        if (in == null) return new ReviewSummaryDTO(null, List.of(), List.of(), List.of());
        List<String> pros = clampList(in.getPros(), 3);
        List<String> cons = clampList(in.getCons(), 3);
        List<String> topics = clampList(in.getTopTopics(), 5);
        return new ReviewSummaryDTO(
                safeText(in.getTakeaway()),
                pros,
                cons,
                topics
        );
    }

    private List<String> clampList(List<String> in, int max) {
        if (in == null || in.isEmpty()) return List.of();
        List<String> out = new ArrayList<>();
        for (String s : in) {
            String t = safeText(s);
            if (t == null) continue;
            if (out.contains(t)) continue;
            out.add(t);
            if (out.size() >= max) break;
        }
        return out;
    }

    private String safeText(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        if (t.length() > 220) t = t.substring(0, 220);
        return t;
    }

    private String normalizeLang(String lang) {
        String l = (lang == null ? "" : lang).trim().toLowerCase();
        if (l.contains("-")) l = l.substring(0, l.indexOf('-'));
        if (Objects.equals(l, "tr") || Objects.equals(l, "es") || Objects.equals(l, "en")) return l;
        return "en";
    }

    private ReviewSummaryDTO buildLocalSummary(Product product, List<Review> reviews, String lang) {
        List<Review> usable = reviews == null ? List.of() : reviews;
        Map<String, Integer> topicCounts = new LinkedHashMap<>();
        for (String k : topicLabels().keySet()) {
            topicCounts.put(k, 0);
        }

        List<String> pros = new ArrayList<>();
        List<String> cons = new ArrayList<>();
        int used = 0;

        for (Review r : usable) {
            if (r == null) continue;
            String comment = r.getComment();
            if (comment == null) continue;
            String text = comment.toLowerCase();
            used++;

            String snippet = firstSentence(comment);

            if (r.getRating() != null && r.getRating() >= 4 && pros.size() < 3) {
                if (snippet != null && !pros.contains(snippet)) pros.add(snippet);
            }
            if (r.getRating() != null && r.getRating() <= 2 && cons.size() < 3) {
                if (snippet != null && !cons.contains(snippet)) cons.add(snippet);
            }

            for (Map.Entry<String, List<String>> e : topicKeywords(product.getCategory()).entrySet()) {
                String topicKey = e.getKey();
                for (String kw : e.getValue()) {
                    if (kw == null || kw.isEmpty()) continue;
                    if (text.contains(kw)) {
                        topicCounts.put(topicKey, (topicCounts.getOrDefault(topicKey, 0) + 1));
                        break;
                    }
                }
            }
        }

        List<String> topTopics = new ArrayList<>();
        topicCounts.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .filter(e -> e.getValue() > 0)
                .limit(5)
                .forEach(e -> topTopics.add(topicLabel(lang, e.getKey())));

        String posTopic = topTopics.size() > 0 ? topTopics.get(0) : null;
        String negTopic = topTopics.size() > 1 ? topTopics.get(1) : null;

        String takeaway = buildLocalTakeaway(lang, posTopic, negTopic);
        if (takeaway == null) takeaway = defaultNoInsight(lang);

        return new ReviewSummaryDTO(takeaway, clampList(pros, 3), clampList(cons, 3), clampList(topTopics, 5));
    }

    private String firstSentence(String text) {
        if (text == null) return null;
        String t = text.replace("\r", " ").replace("\n", " ").trim();
        if (t.isEmpty()) return null;
        String[] parts = t.split("[.!?]");
        String s = parts.length > 0 ? parts[0].trim() : t;
        if (s.length() > 90) s = s.substring(0, 90) + "…";
        return s.isEmpty() ? null : s;
    }

    private String buildLocalTakeaway(String lang, String pos, String neg) {
        if (pos == null && neg == null) return defaultNoInsight(lang);
        if (pos != null && neg != null) {
            if ("tr".equals(lang)) return "Kullanıcıların çoğu " + pos + " konusunu olumlu belirtirken, " + neg + " sıkça eleştiriliyor.";
            if ("es".equals(lang)) return "La mayoría destaca " + pos + " de forma positiva, mientras que " + neg + " es una queja frecuente.";
            return "Most users mention " + pos + " positively, while " + neg + " is a common complaint.";
        }
        if (pos != null) {
            if ("tr".equals(lang)) return "Kullanıcıların çoğu " + pos + " konusunu olumlu belirtiyor.";
            if ("es".equals(lang)) return "La mayoría menciona " + pos + " de forma positiva.";
            return "Most users mention " + pos + " positively.";
        }
        if ("tr".equals(lang)) return neg + " konusu sıkça eleştiriliyor.";
        if ("es".equals(lang)) return neg + " es una queja frecuente.";
        return neg + " is a common complaint.";
    }

    private String defaultNoInsight(String lang) {
        if ("tr".equals(lang)) return "Yorumlar farklı deneyimler içeriyor.";
        if ("es".equals(lang)) return "Las reseñas muestran experiencias variadas.";
        return "Reviews mention mixed experiences.";
    }

    private Map<String, Map<String, String>> topicLabels() {
        Map<String, Map<String, String>> labels = new LinkedHashMap<>();
        labels.put("battery", Map.of("en", "Battery", "tr", "Batarya", "es", "Batería"));
        labels.put("performance", Map.of("en", "Performance", "tr", "Performans", "es", "Rendimiento"));
        labels.put("price", Map.of("en", "Price", "tr", "Fiyat", "es", "Precio"));
        labels.put("build", Map.of("en", "Build quality", "tr", "Malzeme kalitesi", "es", "Calidad de construcción"));
        labels.put("camera", Map.of("en", "Camera", "tr", "Kamera", "es", "Cámara"));
        labels.put("delivery", Map.of("en", "Delivery", "tr", "Kargo", "es", "Envío"));
        labels.put("packaging", Map.of("en", "Packaging", "tr", "Paketleme", "es", "Embalaje"));
        labels.put("comfort", Map.of("en", "Comfort", "tr", "Konfor", "es", "Comodidad"));
        labels.put("usability", Map.of("en", "Usability", "tr", "Kullanım", "es", "Usabilidad"));
        return labels;
    }

    private String topicLabel(String lang, String topicKey) {
        Map<String, String> m = topicLabels().get(topicKey);
        if (m == null) return topicKey;
        return m.getOrDefault(lang, m.get("en"));
    }

    private Map<String, List<String>> topicKeywords(String category) {
        String c = category == null ? "" : category.toLowerCase();
        Map<String, List<String>> kws = new LinkedHashMap<>();
        if (c.contains("electronics")) {
            kws.put("battery", List.of("battery", "batarya", "pil", "charge", "şarj", "bateria", "carga"));
            kws.put("performance", List.of("performance", "speed", "fast", "performans", "hız", "rapido", "rendimiento"));
            kws.put("camera", List.of("camera", "kamera", "cámara", "photo", "foto"));
            kws.put("build", List.of("build", "quality", "malzeme", "kalite", "construction", "construcción"));
            kws.put("price", List.of("price", "fiyat", "precio", "expensive", "pahalı", "caro"));
            kws.put("delivery", List.of("delivery", "shipping", "kargo", "envío"));
            kws.put("packaging", List.of("package", "packaging", "paket", "embalaje"));
            kws.put("usability", List.of("usability", "easy", "kullanım", "kolay", "usabilidad"));
            return kws;
        }
        if (c.contains("clothing")) {
            kws.put("comfort", List.of("comfortable", "comfort", "konfor", "rahat", "comodidad"));
            kws.put("build", List.of("fabric", "quality", "kumaş", "kalite", "tela", "calidad"));
            kws.put("price", List.of("price", "fiyat", "precio", "expensive", "pahalı", "caro"));
            kws.put("delivery", List.of("delivery", "shipping", "kargo", "envío"));
            kws.put("packaging", List.of("package", "packaging", "paket", "embalaje"));
            kws.put("usability", List.of("fit", "size", "beden", "uyum", "talla", "ajuste"));
            return kws;
        }
        if (c.contains("books")) {
            kws.put("usability", List.of("translation", "çeviri", "traducción", "writing", "yazım", "prose", "estilo"));
            kws.put("build", List.of("cover", "kapak", "paper", "kağıt", "portada", "papel"));
            kws.put("price", List.of("price", "fiyat", "precio", "expensive", "pahalı", "caro"));
            kws.put("delivery", List.of("delivery", "shipping", "kargo", "envío"));
            kws.put("packaging", List.of("package", "packaging", "paket", "embalaje"));
            return kws;
        }
        kws.put("build", List.of("quality", "kalite", "calidad", "material", "malzeme"));
        kws.put("price", List.of("price", "fiyat", "precio", "expensive", "pahalı", "caro"));
        kws.put("delivery", List.of("delivery", "shipping", "kargo", "envío"));
        kws.put("packaging", List.of("package", "packaging", "paket", "embalaje"));
        kws.put("usability", List.of("easy", "kolay", "usabilidad", "usable"));
        return kws;
    }

    private String safeSnippet(String body) {
        if (body == null) return "";
        String t = body.replace("\r", " ").replace("\n", " ").trim();
        if (t.isEmpty()) return "";
        return t.length() > 500 ? t.substring(0, 500) : t;
    }

    private List<String> asStringList(JsonNode node) {
        List<String> out = new ArrayList<>();
        if (node == null || !node.isArray()) return out;
        for (JsonNode n : node) {
            if (n == null) continue;
            String v = n.asText(null);
            if (v == null) continue;
            String t = v.trim();
            if (t.isEmpty()) continue;
            out.add(t);
        }
        return out;
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

    private String sanitize(String text) {
        if (text == null) return "";
        String t = text.replace("\r", " ").replace("\n", " ").trim();
        if (t.length() > 500) {
            return t.substring(0, 500);
        }
        return t;
    }

    private static final class CacheEntry {
        private final ReviewSummaryResponseDTO value;
        private final Instant createdAt;

        private CacheEntry(ReviewSummaryResponseDTO value) {
            this.value = value;
            this.createdAt = Instant.now();
        }

        private boolean isExpired() {
            return Instant.now().isAfter(createdAt.plus(CACHE_TTL));
        }
    }
}
