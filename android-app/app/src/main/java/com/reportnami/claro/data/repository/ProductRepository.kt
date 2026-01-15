package com.reportnami.claro.data.repository

import com.reportnami.claro.data.api.ApiService
import com.reportnami.claro.data.api.model.PageResponse
import com.reportnami.claro.data.api.model.ProductDetailDto
import com.reportnami.claro.data.api.model.ProductDto
import com.reportnami.claro.data.api.model.ReviewSummaryResponseDto
import com.reportnami.claro.data.api.model.TranslateRequestDto
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getProducts(
        page: Int,
        size: Int,
        sortBy: String,
        sortDir: String,
        category: String?,
        search: String?,
        minRating: Int?,
        minPrice: String?,
        maxPrice: String?,
    ): PageResponse<ProductDto> {
        return apiService.getProducts(
            page = page,
            size = size,
            sortBy = sortBy,
            sortDir = sortDir,
            category = category,
            search = search,
            minRating = minRating,
            minPrice = minPrice,
            maxPrice = maxPrice,
        )
    }

    suspend fun getProductById(id: Long): ProductDetailDto {
        return apiService.getProductById(id)
    }

    suspend fun getReviewSummary(productId: Long, limit: Int, lang: String): ReviewSummaryResponseDto {
        return apiService.getReviewSummary(productId = productId, limit = limit, lang = lang)
    }

    suspend fun translateBatch(lang: String, texts: List<String>): List<String> {
        if (texts.isEmpty()) return emptyList()
        return apiService.translate(
            body = TranslateRequestDto(
                lang = lang,
                texts = texts,
            ),
        ).translations.orEmpty()
    }
}
