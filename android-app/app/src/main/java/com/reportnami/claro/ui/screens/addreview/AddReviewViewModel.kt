package com.reportnami.claro.ui.screens.addreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reportnami.claro.data.api.model.CreateReviewRequestDto
import com.reportnami.claro.data.repository.DeviceIdRepository
import com.reportnami.claro.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val deviceIdRepository: DeviceIdRepository,
) : ViewModel() {

    data class UiState(
        val isSubmitting: Boolean = false,
        val error: String? = null,
        val success: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun submit(productId: Long, reviewerName: String?, rating: Int, comment: String?) {
        if (rating !in 1..5) {
            _uiState.value = UiState(error = "Rating must be between 1 and 5")
            return
        }

        _uiState.value = UiState(isSubmitting = true)
        viewModelScope.launch {
            runCatching {
                val deviceId = deviceIdRepository.getOrCreate()
                reviewRepository.createReview(
                    CreateReviewRequestDto(
                        productId = productId,
                        comment = comment?.takeIf { it.isNotBlank() },
                        rating = rating,
                        reviewerName = reviewerName?.takeIf { it.isNotBlank() },
                        deviceId = deviceId,
                    )
                )
            }.onSuccess {
                _uiState.value = UiState(success = true)
            }.onFailure { e ->
                _uiState.value = UiState(error = e.message ?: "Failed to submit review")
            }
        }
    }

    fun consumeSuccess() {
        _uiState.value = UiState(success = false)
    }
}
