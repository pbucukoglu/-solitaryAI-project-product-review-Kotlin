package com.productreview.service;

import com.productreview.dto.CreateReviewDTO;
import com.productreview.dto.HelpfulVoteResponseDTO;
import com.productreview.dto.ReviewDTO;
import com.productreview.dto.UpdateReviewDTO;
import com.productreview.entity.Product;
import com.productreview.entity.Review;
import com.productreview.entity.ReviewHelpfulVote;
import com.productreview.entity.User;
import com.productreview.repository.ProductRepository;
import com.productreview.repository.ReviewHelpfulVoteRepository;
import com.productreview.repository.ReviewRepository;
import com.productreview.repository.UserRepository;
import com.productreview.validation.ReviewValidationAspect;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ReviewHelpfulVoteRepository reviewHelpfulVoteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewValidationAspect reviewValidationAspect;
    
    public ReviewDTO createReview(CreateReviewDTO createReviewDTO, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
        
        Product product = productRepository.findById(createReviewDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + createReviewDTO.getProductId()));
        
        // Check for duplicate review
        Optional<Review> existingReview = reviewRepository.findByProductIdAndUserId(product.getId(), user.getId());
        if (existingReview.isPresent()) {
            throw new RuntimeException("You have already reviewed this product");
        }
        
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setComment(createReviewDTO.getComment().trim());
        review.setRating(createReviewDTO.getRating());
        
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

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only edit your own review");
        }

        review.setComment(updateReviewDTO.getComment().trim());
        review.setRating(updateReviewDTO.getRating());

        Review saved = reviewRepository.save(review);
        recalculateAggregates(review.getProduct().getId());
        return convertToDTO(saved);
    }

    public void deleteReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own review");
        }

        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);
        recalculateAggregates(productId);
    }
    
    public Page<ReviewDTO> getReviewsByProductId(Long productId, Pageable pageable, Integer minRating) {
        return reviewRepository.findByProductIdFiltered(productId, minRating, pageable)
                .map(this::convertToDTO);
    }

    public HelpfulVoteResponseDTO toggleHelpful(Long reviewId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        var existing = reviewHelpfulVoteRepository.findByReviewIdAndUserId(reviewId, user.getId());
        boolean helpfulByMe;
        if (existing.isPresent()) {
            reviewHelpfulVoteRepository.delete(existing.get());
            long next = Math.max(0L, (review.getHelpfulCount() == null ? 0L : review.getHelpfulCount()) - 1L);
            review.setHelpfulCount(next);
            helpfulByMe = false;
        } else {
            ReviewHelpfulVote vote = new ReviewHelpfulVote();
            vote.setReview(review);
            vote.setUser(user);
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
                review.getUser().getFirstName() + " " + review.getUser().getLastName(),
                review.getUser().getEmail(),
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


