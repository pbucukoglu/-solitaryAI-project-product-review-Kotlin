package com.productreview.controller;

import com.productreview.dto.CreateReviewDTO;
import com.productreview.dto.HelpfulVoteResponseDTO;
import com.productreview.dto.ReviewDTO;
import com.productreview.dto.UpdateReviewDTO;
import com.productreview.service.ReviewService;
import com.productreview.validation.ValidateCreateReview;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ValidateCreateReview
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody CreateReviewDTO createReviewDTO, Authentication authentication) {
        ReviewDTO review = reviewService.createReview(createReviewDTO, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewDTO updateReviewDTO,
            Authentication authentication
    ) {
        ReviewDTO updated = reviewService.updateReview(reviewId, updateReviewDTO, authentication.getName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        reviewService.deleteReview(reviewId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) Integer minRating
    ) {
        Sort primary = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Sort sort = "helpfulCount".equals(sortBy)
                ? primary.and(Sort.by("createdAt").descending())
                : primary;
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId, pageable, minRating);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{reviewId}/helpful")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<HelpfulVoteResponseDTO> toggleHelpful(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        HelpfulVoteResponseDTO response = reviewService.toggleHelpful(reviewId, authentication.getName());
        return ResponseEntity.ok(response);
    }
}
