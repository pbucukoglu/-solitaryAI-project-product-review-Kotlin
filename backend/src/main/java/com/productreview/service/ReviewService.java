package com.productreview.service;

import com.productreview.aspect.ValidateCreateReview;
import com.productreview.dto.CreateReviewDTO;
import com.productreview.dto.HelpfulVoteResponseDTO;
import com.productreview.dto.ReviewDTO;
import com.productreview.dto.UpdateReviewDTO;
import com.productreview.entity.Product;
import com.productreview.entity.Review;
import com.productreview.entity.ReviewHelpfulVote;
import com.productreview.repository.ProductRepository;
import com.productreview.repository.ReviewHelpfulVoteRepository;
import com.productreview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ReviewHelpfulVoteRepository reviewHelpfulVoteRepository;
    private final ProductRepository productRepository;
    
    @ValidateCreateReview
    public ReviewDTO createReview(CreateReviewDTO createReviewDTO, String userEmail) {
        Product product = productRepository.findById(createReviewDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + createReviewDTO.getProductId()));
        
        String rawComment = createReviewDTO.getComment();
        String trimmedComment = rawComment == null ? "" : rawComment.trim();
        if (rawComment != null && !rawComment.isEmpty() && trimmedComment.isEmpty()) {
            throw new IllegalArgumentException("Comment must be at least 10 characters");
        }
        if (!trimmedComment.isEmpty() && trimmedComment.length() < 10) {
            throw new IllegalArgumentException("Comment must be at least 10 characters");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setComment(trimmedComment);
        review.setRating(createReviewDTO.getRating());
        review.setReviewerName(createReviewDTO.getReviewerName() != null && !createReviewDTO.getReviewerName().isEmpty() 
                ? createReviewDTO.getReviewerName() 
                : userEmail);
        review.setDeviceId(userEmail); // Use email as device identifier for authenticated users
        
        Review savedReview = reviewRepository.save(review);

        Double avgRating = reviewRepository.findAverageRatingByProductId(product.getId());
        Long reviewCount = reviewRepository.countByProductId(product.getId());
        product.setAverageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        product.setReviewCount(reviewCount != null ? reviewCount : 0L);
        productRepository.save(product);
        
        return convertToDTO(savedReview);
    }

    public ReviewDTO updateReview(Long reviewId, UpdateReviewDTO updateReviewDTO, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        // Check if user owns this review (either by device ID or email)
        if (!userEmail.equals(review.getDeviceId())) {
            throw new IllegalStateException("FORBIDDEN");
        }

        String rawComment = updateReviewDTO.getComment();
        String trimmedComment = rawComment == null ? "" : rawComment.trim();
        if (rawComment != null && !rawComment.isEmpty() && trimmedComment.isEmpty()) {
            throw new IllegalArgumentException("Comment must be at least 10 characters");
        }
        if (!trimmedComment.isEmpty() && trimmedComment.length() < 10) {
            throw new IllegalArgumentException("Comment must be at least 10 characters");
        }

        review.setComment(trimmedComment);
        review.setRating(updateReviewDTO.getRating());
        review.setReviewerName(updateReviewDTO.getReviewerName() != null && !updateReviewDTO.getReviewerName().isEmpty()
                ? updateReviewDTO.getReviewerName()
                : userEmail);

        Review saved = reviewRepository.save(review);
        recalculateAggregates(review.getProduct().getId());
        return convertToDTO(saved);
    }

    public void deleteReview(Long reviewId, String deviceId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        // Check if user owns this review (either by device ID or email)
        if (!userEmail.equals(review.getDeviceId())) {
            throw new IllegalStateException("FORBIDDEN");
        }

        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);
        recalculateAggregates(productId);
    }
    
    public Page<ReviewDTO> getReviewsByProductId(Long productId, Pageable pageable, Integer minRating) {
        return reviewRepository.findByProductIdFiltered(productId, minRating, pageable)
                .map(this::convertToDTO);
    }

    public HelpfulVoteResponseDTO toggleHelpful(Long reviewId, String deviceId, String userEmail) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            deviceId = userEmail; // Use email as device identifier for authenticated users
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        var existing = reviewHelpfulVoteRepository.findByReviewIdAndDeviceId(reviewId, deviceId);
        boolean helpfulByMe;
        if (existing.isPresent()) {
            reviewHelpfulVoteRepository.delete(existing.get());
            long next = Math.max(0L, (review.getHelpfulCount() == null ? 0L : review.getHelpfulCount()) - 1L);
            review.setHelpfulCount(next);
            helpfulByMe = false;
        } else {
            ReviewHelpfulVote vote = new ReviewHelpfulVote();
            vote.setReview(review);
            vote.setDeviceId(deviceId);
            reviewHelpfulVoteRepository.save(vote);
            long next = (review.getHelpfulCount() == null ? 0L : review.getHelpfulCount()) + 1L;
            review.setHelpfulCount(next);
            helpfulByMe = true;
        }

        Review saved = reviewRepository.save(review);
        return new HelpfulVoteResponseDTO(saved.getId(), saved.getHelpfulCount() == null ? 0L : saved.getHelpfulCount(), helpfulByMe);
    }
    
    private ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getProduct().getId(),
                review.getComment(),
                review.getRating(),
                review.getReviewerName(),
                review.getDeviceId(),
                review.getHelpfulCount() == null ? 0L : review.getHelpfulCount(),
                review.getCreatedAt()
        );
    }

    private void recalculateAggregates(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Double avgRating = reviewRepository.findAverageRatingByProductId(productId);
        Long reviewCount = reviewRepository.countByProductId(productId);

        product.setAverageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        product.setReviewCount(reviewCount != null ? reviewCount : 0L);
        productRepository.save(product);
    }
}


