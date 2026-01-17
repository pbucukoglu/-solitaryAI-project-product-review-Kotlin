package com.productreview.aspect;

import com.productreview.dto.CreateReviewDTO;
import com.productreview.dto.ProductDTO;
import com.productreview.entity.Product;
import com.productreview.exception.ValidationException;
import com.productreview.repository.ProductRepository;
import com.productreview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ValidationAspect {
    
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    
    @Before("@annotation(ValidateCreateReview) && args(createReviewDTO,userEmail,..)")
    public void validateCreateReview(JoinPoint joinPoint, CreateReviewDTO createReviewDTO, String userEmail) {
        // Validate product exists
        if (!productRepository.existsById(createReviewDTO.getProductId())) {
            throw new ValidationException("Product not found with id: " + createReviewDTO.getProductId());
        }
        
        // Validate rating range
        if (createReviewDTO.getRating() < 1 || createReviewDTO.getRating() > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }
        
        // Validate comment length
        String comment = createReviewDTO.getComment();
        if (comment == null || comment.trim().isEmpty()) {
            throw new ValidationException("Comment cannot be empty");
        }
        if (comment.trim().length() < 3) {
            throw new ValidationException("Comment must be at least 3 characters long");
        }
        if (comment.trim().length() > 500) {
            throw new ValidationException("Comment cannot exceed 500 characters");
        }
        
        // Business rule: Prevent duplicate review by same user for same product
        if (reviewRepository.existsByProductIdAndUserEmail(createReviewDTO.getProductId(), userEmail)) {
            throw new ValidationException("You have already reviewed this product");
        }
    }
    
    @Before("@annotation(ValidateCreateProduct) && args(productDTO)")
    public void validateCreateProduct(JoinPoint joinPoint, ProductDTO productDTO) {
        // Validate product name
        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            throw new ValidationException("Product name cannot be empty");
        }
        if (productDTO.getName().trim().length() < 2) {
            throw new ValidationException("Product name must be at least 2 characters long");
        }
        if (productDTO.getName().trim().length() > 200) {
            throw new ValidationException("Product name cannot exceed 200 characters");
        }
        
        // Validate price
        if (productDTO.getPrice() == null || productDTO.getPrice().doubleValue() <= 0) {
            throw new ValidationException("Price must be a positive value");
        }
        if (productDTO.getPrice().doubleValue() > 999999.99) {
            throw new ValidationException("Price cannot exceed 999999.99");
        }
        
        // Validate category
        if (productDTO.getCategory() == null || productDTO.getCategory().trim().isEmpty()) {
            throw new ValidationException("Category cannot be empty");
        }
        
        // Validate description (optional but if provided, must be reasonable)
        if (productDTO.getDescription() != null && productDTO.getDescription().length() > 2000) {
            throw new ValidationException("Description cannot exceed 2000 characters");
        }
    }
}
