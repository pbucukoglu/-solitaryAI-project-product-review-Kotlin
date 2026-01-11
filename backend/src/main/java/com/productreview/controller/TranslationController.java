package com.productreview.controller;

import com.productreview.dto.TranslateRequestDTO;
import com.productreview.dto.TranslateResponseDTO;
import com.productreview.service.GroqTranslationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translate")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TranslationController {

    private final GroqTranslationService groqTranslationService;

    @PostMapping
    public ResponseEntity<TranslateResponseDTO> translate(@Valid @RequestBody TranslateRequestDTO request) {
        TranslateResponseDTO res = groqTranslationService.translateBatch(request.getTexts(), request.getLang());
        return ResponseEntity.ok(res);
    }
}
