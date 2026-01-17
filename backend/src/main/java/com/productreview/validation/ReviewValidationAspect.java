package com.productreview.validation;

import com.productreview.dto.CreateReviewDTO;
import com.productreview.entity.Product;
import com.productreview.entity.Review;
import com.productreview.entity.User;
import com.productreview.repository.ProductRepository;
import com.productreview.repository.ReviewRepository;
import com.productreview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class ReviewValidationAspect {
    
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    
    @Before("@annotation(com.productreview.validation.ValidateCreateReview)")
    public void validateCreateReview(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        
        for (Object arg : args) {
            if (arg instanceof CreateReviewDTO) {
                CreateReviewDTO reviewDTO = (CreateReviewDTO) arg;
                validateReviewDTO(reviewDTO);
            } else if (arg instanceof Long && args.length >= 2) {
                Long productId = (Long) arg;
                validateProductExists(productId);
            }
        }
    }
    
    private void validateReviewDTO(CreateReviewDTO reviewDTO) {
        if (reviewDTO.getRating() == null || reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Rating must be between 1 and 5");
        }
        
        if (reviewDTO.getComment() == null || reviewDTO.getComment().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Comment cannot be empty");
        }
        
        if (reviewDTO.getComment().length() < 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Comment must be at least 10 characters long");
        }
        
        if (reviewDTO.getComment().length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Comment must not exceed 2000 characters");
        }
    }
    
    private void validateProductExists(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Product not found with id: " + productId);
        }
    }
    
    public void validateDuplicateReview(Long productId, Long userId) {
        Optional<Review> existingReview = reviewRepository.findByProductIdAndUserId(productId, userId);
        if (existingReview.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "You have already reviewed this product");
        }
    }
    
    public void validateUserExists(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "User not found with id: " + userId);
        }
    }
}
