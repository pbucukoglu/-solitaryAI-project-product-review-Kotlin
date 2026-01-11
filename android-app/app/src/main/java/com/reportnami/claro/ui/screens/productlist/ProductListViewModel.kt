package com.reportnami.claro.ui.screens.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reportnami.claro.data.api.model.ProductDto
import com.reportnami.claro.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val items: List<ProductDto> = emptyList(),
        val page: Int = 0,
        val hasMore: Boolean = true,
        val search: String = "",
        val sortBy: String = "reviewCount",
        val sortDir: String = "DESC",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, page = 0, hasMore = true)
        viewModelScope.launch {
            runCatching {
                productRepository.getProducts(
                    page = 0,
                    size = 20,
                    sortBy = _uiState.value.sortBy,
                    sortDir = _uiState.value.sortDir,
                    category = null,
                    search = _uiState.value.search.takeIf { it.isNotBlank() },
                    minRating = null,
                    minPrice = null,
                    maxPrice = null,
                )
            }.onSuccess { res ->
                val items = res.content
                val hasMore = (res.last == false)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    items = items,
                    page = res.number ?: 0,
                    hasMore = hasMore,
                    error = null,
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Unknown error")
            }
        }
    }

    fun loadMore() {
        val current = _uiState.value
        if (current.isLoading || !current.hasMore) return

        _uiState.value = current.copy(isLoading = true, error = null)
        val nextPage = (current.page) + 1
        viewModelScope.launch {
            runCatching {
                productRepository.getProducts(
                    page = nextPage,
                    size = 20,
                    sortBy = current.sortBy,
                    sortDir = current.sortDir,
                    category = null,
                    search = current.search.takeIf { it.isNotBlank() },
                    minRating = null,
                    minPrice = null,
                    maxPrice = null,
                )
            }.onSuccess { res ->
                val merged = current.items + res.content
                val hasMore = (res.last == false)
                _uiState.value = current.copy(
                    isLoading = false,
                    items = merged,
                    page = res.number ?: nextPage,
                    hasMore = hasMore,
                    error = null,
                )
            }.onFailure { e ->
                _uiState.value = current.copy(isLoading = false, error = e.message ?: "Unknown error")
            }
        }
    }

    fun setSearch(text: String) {
        _uiState.value = _uiState.value.copy(search = text)
    }
}
