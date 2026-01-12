package com.reportnami.claro.ui.screens.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reportnami.claro.data.api.model.ReviewDto
import com.reportnami.claro.data.repository.DeviceIdRepository
import com.reportnami.claro.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val deviceIdRepository: DeviceIdRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val items: List<ReviewDto> = emptyList(),
        val page: Int = 0,
        val hasMore: Boolean = true,
        val productId: Long? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(productId: Long) {
        _uiState.value = UiState(isLoading = true, productId = productId)
        viewModelScope.launch {
            runCatching {
                reviewRepository.getReviewsByProductId(
                    productId = productId,
                    page = 0,
                    size = 10,
                    sortBy = "createdAt",
                    sortDir = "DESC",
                    minRating = null,
                )
            }.onSuccess { res ->
                _uiState.value = UiState(
                    isLoading = false,
                    productId = productId,
                    items = res.content,
                    page = res.number ?: 0,
                    hasMore = (res.last == false),
                )
            }.onFailure { e ->
                _uiState.value = UiState(isLoading = false, productId = productId, error = e.message ?: "Failed to load reviews")
            }
        }
    }

    fun loadMore() {
        val current = _uiState.value
        val productId = current.productId ?: return
        if (current.isLoading || !current.hasMore) return

        _uiState.value = current.copy(isLoading = true, error = null)
        val nextPage = current.page + 1

        viewModelScope.launch {
            runCatching {
                reviewRepository.getReviewsByProductId(
                    productId = productId,
                    page = nextPage,
                    size = 10,
                    sortBy = "createdAt",
                    sortDir = "DESC",
                    minRating = null,
                )
            }.onSuccess { res ->
                _uiState.value = current.copy(
                    isLoading = false,
                    items = current.items + res.content,
                    page = res.number ?: nextPage,
                    hasMore = (res.last == false),
                    error = null,
                )
            }.onFailure { e ->
                _uiState.value = current.copy(isLoading = false, error = e.message ?: "Failed to load reviews")
            }
        }
    }

    fun toggleHelpful(reviewId: Long) {
        val current = _uiState.value
        viewModelScope.launch {
            val deviceId = deviceIdRepository.getOrCreate()
            runCatching {
                reviewRepository.toggleHelpful(reviewId = reviewId, deviceId = deviceId)
            }.onSuccess { res ->
                val nextItems = current.items.map { r ->
                    if (r.id == res.reviewId) {
                        r.copy(helpfulCount = res.helpfulCount)
                    } else r
                }
                _uiState.value = current.copy(items = nextItems)
            }.onFailure {
                // keep UI stable; error banner can be added later
            }
        }
    }
}
