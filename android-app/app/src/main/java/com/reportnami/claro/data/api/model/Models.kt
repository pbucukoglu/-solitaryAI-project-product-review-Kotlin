package com.reportnami.claro.data.api.model

import com.google.gson.annotations.SerializedName

data class ProductDto(
    val id: Long,
    val name: String,
    val description: String?,
    val category: String?,
    val price: String?,
    val imageUrls: List<String>?,
    val averageRating: Double?,
    val reviewCount: Long?,
)

data class ProductDetailDto(
    val id: Long,
    val name: String,
    val description: String?,
    val category: String?,
    val price: String?,
    val imageUrls: List<String>?,
    val averageRating: Double?,
    val reviewCount: Long?,
    val reviews: List<ReviewDto>?,
)

data class ReviewDto(
    val id: Long,
    val productId: Long,
    val comment: String?,
    val rating: Int?,
    val reviewerName: String?,
    val deviceId: String?,
    val helpfulCount: Long?,
    val createdAt: String?,
)

data class CreateReviewRequestDto(
    val productId: Long,
    val comment: String?,
    val rating: Int,
    val reviewerName: String?,
    val deviceId: String,
)

data class UpdateReviewRequestDto(
    val comment: String?,
    val rating: Int,
    val reviewerName: String?,
    val deviceId: String,
)

data class HelpfulVoteResponseDto(
    val reviewId: Long,
    val helpfulCount: Long,
    val helpfulByMe: Boolean,
)

data class TranslateRequestDto(
    val lang: String,
    val texts: List<String>,
)

data class TranslateResponseDto(
    @SerializedName("translations")
    val translations: List<String>?,
)

data class ReviewSummaryResponseDto(
    val productId: Long,
    val lang: String?,
    val source: String?,
    val averageRating: Double?,
    val reviewCount: Long?,
    val reviewCountUsed: Long?,
    val takeaway: String?,
    val pros: List<String>?,
    val cons: List<String>?,
    val topTopics: List<String>?,
    val generatedAt: String?,
)

data class PageResponse<T>(
    val content: List<T> = emptyList(),
    val number: Int? = null,
    val size: Int? = null,
    val totalElements: Long? = null,
    val totalPages: Int? = null,
    val last: Boolean? = null,
    val first: Boolean? = null,
)
