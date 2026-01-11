package com.productreview.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDTO {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @Size(max = 2000, message = "Comment must be at most 2000 characters")
    private String comment;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    private String reviewerName;

    @NotBlank(message = "Device ID is required")
    private String deviceId;
}


