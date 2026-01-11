package com.reportnami.claro.ui.screens.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reportnami.claro.data.api.model.ProductDetailDto
import com.reportnami.claro.data.api.model.ReviewSummaryResponseDto
import com.reportnami.claro.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val product: ProductDetailDto? = null,
        val summary: ReviewSummaryResponseDto? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(productId: Long) {
        _uiState.value = UiState(isLoading = true)
        viewModelScope.launch {
            runCatching {
                val productDeferred = async { productRepository.getProductById(productId) }
                val summaryDeferred = async { productRepository.getReviewSummary(productId, limit = 30, lang = "en") }
                Pair(productDeferred.await(), summaryDeferred.await())
            }.onSuccess { (product, summary) ->
                _uiState.value = UiState(isLoading = false, product = product, summary = summary)
            }.onFailure { e ->
                _uiState.value = UiState(isLoading = false, error = e.message ?: "Unknown error")
            }
        }
    }
}
