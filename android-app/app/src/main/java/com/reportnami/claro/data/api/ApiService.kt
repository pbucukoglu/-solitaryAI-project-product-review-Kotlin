package com.reportnami.claro.data.api

import com.reportnami.claro.data.api.model.LoginRequestDto
import com.reportnami.claro.data.api.model.LoginResponseDto
import com.reportnami.claro.data.api.model.CreateReviewRequestDto
import com.reportnami.claro.data.api.model.HelpfulVoteResponseDto
import com.reportnami.claro.data.api.model.PageResponse
import com.reportnami.claro.data.api.model.ProductDetailDto
import com.reportnami.claro.data.api.model.ProductDto
import com.reportnami.claro.data.api.model.RegisterRequestDto
import com.reportnami.claro.data.api.model.ReviewDto
import com.reportnami.claro.data.api.model.ReviewSummaryResponseDto
import com.reportnami.claro.data.api.model.TranslateRequestDto
import com.reportnami.claro.data.api.model.TranslateResponseDto
import com.reportnami.claro.data.api.model.UpdateReviewRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Map

interface ApiService {

    // Auth endpoints
    @POST("/api/auth/login")
    suspend fun login(
        @Body body: LoginRequestDto,
    ): LoginResponseDto

    @POST("/api/auth/register")
    suspend fun register(
        @Body body: RegisterRequestDto,
    ): String // Returns success message

    @GET("/api/products")
    suspend fun getProducts(
    ): Response<List<ProductDto>>

    @GET("/api/products")
    suspend fun getProductsPaginated(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "reviewCount",
        @Query("sortDir") sortDir: String = "DESC",
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("minRating") minRating: Int? = null,
        @Query("minPrice") minPrice: String? = null,
        @Query("maxPrice") maxPrice: String? = null,
    ): PageResponse<ProductDto>

    @POST("/api/products")
    suspend fun createProduct(
        @Header("Authorization") authorization: String,
        @Body body: Map<String, Any>,
    ): Response<ProductDto>

    @DELETE("/api/products/{id}")
    suspend fun deleteProduct(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long,
    ): Response<Void>

    @GET("/api/products/{id}")
    suspend fun getProductById(
        @Path("id") id: Long,
    ): ProductDetailDto

    @GET("/api/products/{productId}/review-summary")
    suspend fun getReviewSummary(
        @Path("productId") productId: Long,
        @Query("limit") limit: Int = 30,
        @Query("lang") lang: String = "en",
    ): ReviewSummaryResponseDto

    @GET("/api/reviews/product/{productId}")
    suspend fun getReviewsByProductId(
        @Path("productId") productId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "createdAt",
        @Query("sortDir") sortDir: String = "DESC",
        @Query("minRating") minRating: Int? = null,
    ): PageResponse<ReviewDto>

    @POST("/api/reviews")
    suspend fun createReview(
        @Body body: CreateReviewRequestDto,
    ): ReviewDto

    @PUT("/api/reviews/{reviewId}")
    suspend fun updateReview(
        @Path("reviewId") reviewId: Long,
        @Body body: UpdateReviewRequestDto,
    ): ReviewDto

    @DELETE("/api/reviews/{reviewId}")
    suspend fun deleteReview(
        @Path("reviewId") reviewId: Long,
        @Query("deviceId") deviceId: String,
    )

    @POST("/api/reviews/{reviewId}/helpful")
    suspend fun toggleHelpful(
        @Path("reviewId") reviewId: Long,
        @Query("deviceId") deviceId: String,
    ): HelpfulVoteResponseDto

    @POST("/api/translate")
    suspend fun translate(
        @Body body: TranslateRequestDto,
    ): TranslateResponseDto
}
