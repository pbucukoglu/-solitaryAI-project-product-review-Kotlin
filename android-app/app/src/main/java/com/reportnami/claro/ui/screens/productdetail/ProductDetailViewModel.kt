package com.reportnami.claro.ui.screens.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reportnami.claro.data.api.model.CreateReviewRequestDto
import com.reportnami.claro.data.api.model.ProductDetailDto
import com.reportnami.claro.data.api.model.ReviewDto
import com.reportnami.claro.data.api.model.ReviewSummaryResponseDto
import com.reportnami.claro.data.repository.DeviceIdRepository
import com.reportnami.claro.data.repository.ProductRepository
import com.reportnami.claro.data.repository.ReviewRepository
import com.reportnami.claro.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository,
    private val deviceIdRepository: DeviceIdRepository,
    private val wishlistRepository: WishlistRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val product: ProductDetailDto? = null,
        val reviewSummary: ReviewSummaryResponseDto? = null,
        val reviewSummarySource: String? = null,
        val isFavorite: Boolean = false,
        val reviews: List<ReviewDto> = emptyList(),
        val reviewsError: String? = null,
        val reviewsPage: Int = 0,
        val reviewsHasMore: Boolean = true,
        val isLoadingReviews: Boolean = false,
        val translatedDescription: String? = null,
        val translatedCommentsById: Map<Long, String> = emptyMap(),
        val isSubmittingReview: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var currentProductId: Long? = null
    private var currentLang: String = "en"

    fun load(productId: Long) {
        currentProductId = productId
        _uiState.value = UiState(isLoading = true)
        viewModelScope.launch {
            runCatching {
                val productDeferred = async { productRepository.getProductById(productId) }
                val summaryDeferred = async { productRepository.getReviewSummary(productId, limit = 30, lang = currentLang) }
                val firstReviewsDeferred = async {
                    reviewRepository.getReviewsByProductId(
                        productId = productId,
                        page = 0,
                        size = 10,
                        sortBy = "helpfulCount",
                        sortDir = "DESC",
                        minRating = null,
                    )
                }

                val product = productDeferred.await()
                val summary = summaryDeferred.await()
                val firstReviews = firstReviewsDeferred.await()

                val isFavorite = wishlistRepository.favoriteIds.first().contains(productId)

                LoadedBundle(
                    product = product,
                    summary = summary,
                    reviews = firstReviews.content,
                    reviewsPage = firstReviews.number ?: 0,
                    reviewsHasMore = (firstReviews.last == false),
                    isFavorite = isFavorite,
                )
            }.onSuccess { bundle ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null,
                    product = bundle.product,
                    reviewSummary = bundle.summary,
                    reviewSummarySource = bundle.summary?.source,
                    isFavorite = bundle.isFavorite,
                    reviews = bundle.reviews,
                    reviewsPage = bundle.reviewsPage,
                    reviewsHasMore = bundle.reviewsHasMore,
                    reviewsError = null,
                    isLoadingReviews = false,
                )

                translateIfNeeded(
                    lang = currentLang,
                    description = bundle.product.description,
                    reviews = bundle.reviews,
                )
            }.onFailure { e ->
                _uiState.value = UiState(isLoading = false, error = e.message ?: "Unknown error")
            }
        }
    }

    fun setLang(lang: String) {
        currentLang = lang
        val current = _uiState.value
        translateIfNeeded(lang = lang, description = current.product?.description, reviews = current.reviews)
    }

    fun toggleFavorite(productId: Long) {
        viewModelScope.launch {
            runCatching {
                wishlistRepository.toggle(productId)
            }.onSuccess { next ->
                _uiState.value = _uiState.value.copy(isFavorite = next.contains(productId))
            }
        }
    }

    fun toggleHelpful(reviewId: Long) {
        viewModelScope.launch {
            runCatching {
                val deviceId = deviceIdRepository.getOrCreate()
                reviewRepository.toggleHelpful(reviewId, deviceId)
            }.onSuccess {
                // Refresh reviews to update helpful counts
                currentProductId?.let { load(it) }
            }
        }
    }

    fun addReview(productId: Long, reviewerName: String, rating: Int, comment: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingReview = true)
            runCatching {
                val deviceId = deviceIdRepository.getOrCreate()
                println("🔍 [ProductDetail] Creating review: productId=$productId, reviewerName=$reviewerName, rating=$rating, comment=$comment, deviceId=$deviceId")
                
                val request = CreateReviewRequestDto(
                    productId = productId,
                    comment = comment,
                    rating = rating,
                    reviewerName = reviewerName,
                    deviceId = deviceId
                )
                println("🔍 [ProductDetail] Request DTO: $request")
                
                val result = reviewRepository.createReview(request)
                println("🔍 [ProductDetail] Review created successfully: $result")
                result
            }.onSuccess { newReview ->
                // Add the new review to the current list immediately (optimistic update)
                val currentProduct = _uiState.value.product
                val currentReviews = _uiState.value.reviews
                
                if (currentProduct != null) {
                    // Update product review count and average rating
                    val updatedProduct = currentProduct.copy(
                        reviewCount = (currentProduct.reviewCount ?: 0) + 1,
                        averageRating = calculateNewAverageRating(
                            currentReviews + newReview,
                            currentProduct.averageRating ?: 0.0
                        )
                    )
                    
                    // Add new review to the list (at the beginning like React Native)
                    val updatedReviews = listOf(newReview) + currentReviews
                    
                    _uiState.value = _uiState.value.copy(
                        product = updatedProduct,
                        reviews = updatedReviews
                    )
                }
                
                // Reset pagination and refresh from server to ensure consistency
                _uiState.value = _uiState.value.copy(
                    reviewsPage = 0,
                    reviewsHasMore = true
                )
                
                // Brief delay to show optimistic update, then refresh
                delay(500)
                loadReviews(productId, page = 0, append = false)
            }.onFailure { e ->
                println("🔍 [ProductDetail] Error creating review: ${e.message}")
                e.printStackTrace()
                // Handle error
                _uiState.value = _uiState.value.copy(
                    error = "Failed to add review: ${e.message}"
                )
            }.also {
                _uiState.value = _uiState.value.copy(isSubmittingReview = false)
            }
        }
    }
    
    private fun calculateNewAverageRating(reviews: List<ReviewDto>, currentAverage: Double): Double {
        if (reviews.isEmpty()) return currentAverage
        return reviews.mapNotNull { it.rating }.average()
    }
    
    private fun loadReviews(productId: Long, page: Int = 0, append: Boolean = false) {
        viewModelScope.launch {
            try {
                if (append) {
                    _uiState.value = _uiState.value.copy(isLoadingReviews = true)
                } else {
                    _uiState.value = _uiState.value.copy(isLoadingReviews = true, reviewsError = null)
                }
                
                val response = reviewRepository.getReviewsByProductId(
                    productId = productId,
                    page = page,
                    size = 10,
                    sortBy = "helpfulCount",
                    sortDir = "DESC",
                    minRating = null,
                )
                
                val newReviews = response.content
                val hasMore = response.last == false
                
                if (append) {
                    // Append to existing reviews (like React Native)
                    val mergedReviews = _uiState.value.reviews + newReviews
                    // Remove duplicates
                    val dedupedReviews = mergedReviews.distinctBy { it.id }
                    _uiState.value = _uiState.value.copy(
                        reviews = dedupedReviews,
                        reviewsPage = page,
                        reviewsHasMore = hasMore,
                        isLoadingReviews = false
                    )
                } else {
                    // Replace all reviews (like React Native)
                    _uiState.value = _uiState.value.copy(
                        reviews = newReviews,
                        reviewsPage = page,
                        reviewsHasMore = hasMore,
                        isLoadingReviews = false,
                        reviewsError = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingReviews = false,
                    reviewsError = "Failed to load reviews: ${e.message}"
                )
            }
        }
    }
    
    fun loadMoreReviews() {
        val currentState = _uiState.value
        if (currentState.reviewsHasMore && !currentState.isLoadingReviews) {
            currentProductId?.let { productId ->
                loadReviews(productId, page = currentState.reviewsPage + 1, append = true)
            }
        }
    }

        private fun translateIfNeeded(lang: String, description: String?, reviews: List<ReviewDto>) {
        val target = lang.trim().lowercase()
        if (target.isBlank() || target == "en") {
            _uiState.value = _uiState.value.copy(
                translatedDescription = null,
                translatedCommentsById = emptyMap(),
            )
            return
        }

        val desc = (description ?: "").trim()
        val commentPairs = reviews.mapNotNull { r ->
            val c = (r.comment ?: "").trim()
            if (c.isBlank()) null else (r.id to c)
        }

        val texts = buildList {
            if (desc.isNotBlank()) add(desc)
            addAll(commentPairs.map { it.second })
        }
        if (texts.isEmpty()) return

        viewModelScope.launch {
            runCatching {
                productRepository.translateBatch(target, texts)
            }.onSuccess { translations ->
                if (translations.size != texts.size) return@onSuccess

                var idx = 0
                val translatedDesc = if (desc.isNotBlank()) translations[idx++].ifBlank { desc } else null
                val map = mutableMapOf<Long, String>()
                commentPairs.forEach { (id, original) ->
                    map[id] = translations[idx++].ifBlank { original }
                }
                _uiState.value = _uiState.value.copy(
                    translatedDescription = translatedDesc,
                    translatedCommentsById = map,
                )
            }
        }
    }

    private data class LoadedBundle(
        val product: ProductDetailDto,
        val summary: ReviewSummaryResponseDto?,
        val reviews: List<ReviewDto>,
        val reviewsPage: Int,
        val reviewsHasMore: Boolean,
        val isFavorite: Boolean,
    )
}
