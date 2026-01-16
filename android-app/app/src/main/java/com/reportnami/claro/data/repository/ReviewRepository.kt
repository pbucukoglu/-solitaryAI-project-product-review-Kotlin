package com.reportnami.claro.data.repository

import com.reportnami.claro.data.api.ApiService
import com.reportnami.claro.data.api.model.CreateReviewRequestDto
import com.reportnami.claro.data.api.model.HelpfulVoteResponseDto
import com.reportnami.claro.data.api.model.PageResponse
import com.reportnami.claro.data.api.model.ReviewDto
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getReviewsByProductId(
        productId: Long,
        page: Int,
        size: Int,
        sortBy: String,
        sortDir: String,
        minRating: Int?,
    ): PageResponse<ReviewDto> {
        return apiService.getReviewsByProductId(
            productId = productId,
            page = page,
            size = size,
            sortBy = sortBy,
            sortDir = sortDir,
            minRating = minRating,
        )
    }

    suspend fun createReview(body: CreateReviewRequestDto): ReviewDto {
        println("🔍 [ReviewRepository] Creating review with body: $body")
        return try {
            val result = apiService.createReview(body)
            println("🔍 [ReviewRepository] Review created successfully: $result")
            result
        } catch (e: Exception) {
            println("🔍 [ReviewRepository] Error creating review: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun toggleHelpful(reviewId: Long, deviceId: String): HelpfulVoteResponseDto {
        return apiService.toggleHelpful(reviewId = reviewId, deviceId = deviceId)
    }
}
