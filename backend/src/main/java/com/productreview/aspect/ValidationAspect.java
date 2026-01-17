package com.productreview.aspect;

import com.productreview.dto.CreateReviewDTO;
import com.productreview.entity.Product;
import com.productreview.exception.NotFoundException;
import com.productreview.exception.ValidationException;
import com.productreview.repository.ProductRepository;
import com.productreview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class ValidationAspect {
    
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    
    @Before("@annotation(com.productreview.annotation.ValidateCreateReview)")
    public void validateCreateReview(JoinPoint joinPoint) {
        CreateReviewDTO dto = (CreateReviewDTO) joinPoint.getArgs()[0];
        
        // Validate product exists
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + dto.getProductId()));
        
        // Validate rating
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }
        
        // Validate comment length
        String comment = dto.getComment();
        if (comment != null) {
            String trimmedComment = comment.trim();
            if (trimmedComment.length() < 3) {
                throw new ValidationException("Comment must be at least 3 characters long");
            }
            if (trimmedComment.length() > 500) {
                throw new ValidationException("Comment must not exceed 500 characters");
            }
        }
        
        // Business rule: Prevent duplicate review by same user for same product
        // Note: This would require user authentication context, for now using deviceId
        if (dto.getDeviceId() != null) {
            boolean existingReview = reviewRepository.existsByProductIdAndDeviceId(dto.getProductId(), dto.getDeviceId());
            if (existingReview) {
                throw new ValidationException("You have already reviewed this product");
            }
        }
    }
    
    @Before("@annotation(com.productreview.annotation.ValidateCreateProduct)")
    public void validateCreateProduct(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        // Assuming first argument is a ProductDTO or similar
        if (args.length > 0) {
            Object productDTO = args[0];
            
            try {
                // Use reflection to validate product fields
                String name = (String) productDTO.getClass().getMethod("getName").invoke(productDTO);
                Double price = (Double) productDTO.getClass().getMethod("getPrice").invoke(productDTO);
                
                // Validate name not blank
                if (name == null || name.trim().isEmpty()) {
                    throw new ValidationException("Product name is required");
                }
                
                // Validate price > 0
                if (price == null || price <= 0) {
                    throw new ValidationException("Product price must be greater than 0");
                }
                
            } catch (Exception e) {
                throw new ValidationException("Invalid product data format");
            }
        }
    }
}
