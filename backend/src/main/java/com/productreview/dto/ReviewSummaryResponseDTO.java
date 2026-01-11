package com.productreview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryResponseDTO {
    private Long productId;
    private String lang;
    private String source;
    private Double averageRating;
    private Long reviewCount;
    private Long reviewCountUsed;
    private String takeaway;
    private java.util.List<String> pros;
    private java.util.List<String> cons;
    private java.util.List<String> topTopics;
    private String generatedAt;
}
