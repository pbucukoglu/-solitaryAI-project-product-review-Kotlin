package com.reportnami.claro.ui.screens.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reportnami.claro.data.api.model.ProductDto
import com.reportnami.claro.data.repository.ProductRepository
import com.reportnami.claro.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val wishlistRepository: WishlistRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val items: List<ProductDto> = emptyList(),
        val favoriteIds: Set<Long> = emptySet(),
        val showFavorites: Boolean = false,
        val page: Int = 0,
        val hasMore: Boolean = true,
        val search: String = "",
        val selectedCategory: String? = null,
        val sortBy: String = "reviewCount",
        val sortDir: String = "DESC",
        val minRating: Int? = null,
        val minPrice: String = "",
        val maxPrice: String = "",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    private var lastAllItems: List<ProductDto> = emptyList()

    init {
        viewModelScope.launch {
            wishlistRepository.favoriteIds.collect { ids ->
                _uiState.update { it.copy(favoriteIds = ids) }
                if (_uiState.value.showFavorites) {
                    refresh()
                }
            }
        }
        refresh()
    }

    fun refresh() {
        val current = _uiState.value
        _uiState.value = current.copy(isLoading = true, error = null, page = 0, hasMore = !current.showFavorites)
        viewModelScope.launch {
            runCatching {
                if (current.showFavorites) {
                    loadFavoriteProducts(
                        ids = current.favoriteIds,
                        selectedCategory = current.selectedCategory,
                        search = current.search,
                        minRating = current.minRating,
                        minPrice = current.minPrice,
                        maxPrice = current.maxPrice,
                        sortBy = current.sortBy,
                        sortDir = current.sortDir,
                    )
                } else {
                    productRepository.getProducts(
                        page = 0,
                        size = 20,
                        sortBy = current.sortBy,
                        sortDir = current.sortDir,
                        category = normalizeCategoryForApi(current.selectedCategory),
                        search = current.search.takeIf { it.isNotBlank() },
                        minRating = current.minRating,
                        minPrice = current.minPrice.takeIf { it.isNotBlank() },
                        maxPrice = current.maxPrice.takeIf { it.isNotBlank() },
                    ).also { lastAllItems = it.content }
                }
            }.onSuccess { res ->
                if (current.showFavorites) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        items = res,
                        page = 0,
                        hasMore = false,
                        error = null,
                    )
                } else {
                    val items = res.content
                    val hasMore = (res.last == false)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        items = items,
                        page = res.number ?: 0,
                        hasMore = hasMore,
                        error = null,
                    )
                }
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Unknown error")
            }
        }
    }

    fun loadMore() {
        val current = _uiState.value
        if (current.showFavorites) return
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
                    category = normalizeCategoryForApi(current.selectedCategory),
                    search = current.search.takeIf { it.isNotBlank() },
                    minRating = current.minRating,
                    minPrice = current.minPrice.takeIf { it.isNotBlank() },
                    maxPrice = current.maxPrice.takeIf { it.isNotBlank() },
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
        _uiState.update { it.copy(search = text) }
        val delayMs = if (text.isNotBlank()) 500L else 0L
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (delayMs > 0) delay(delayMs)
            refresh()
        }
    }

    fun setShowFavorites(show: Boolean) {
        _uiState.update { it.copy(showFavorites = show, page = 0, hasMore = !show) }
        refresh()
    }

    fun applyFilters(
        selectedCategory: String?,
        sortBy: String,
        sortDir: String,
        minRating: Int?,
        minPrice: String,
        maxPrice: String,
    ) {
        _uiState.update {
            it.copy(
                selectedCategory = selectedCategory,
                sortBy = sortBy,
                sortDir = sortDir,
                minRating = minRating,
                minPrice = minPrice,
                maxPrice = maxPrice,
                page = 0,
                hasMore = !it.showFavorites,
            )
        }
        refresh()
    }

    fun toggleFavorite(productId: Long) {
        viewModelScope.launch {
            runCatching { wishlistRepository.toggle(productId) }
        }
    }

    private suspend fun loadFavoriteProducts(
        ids: Set<Long>,
        selectedCategory: String?,
        search: String,
        minRating: Int?,
        minPrice: String,
        maxPrice: String,
        sortBy: String,
        sortDir: String,
    ): List<ProductDto> {
        if (ids.isEmpty()) return emptyList()

        val items = ids.mapNotNull { id ->
            runCatching { productRepository.getProductById(id) }.getOrNull()
        }.map {
            ProductDto(
                id = it.id,
                name = it.name,
                description = it.description,
                category = it.category,
                price = it.price,
                imageUrls = it.imageUrls,
                averageRating = it.averageRating,
                reviewCount = it.reviewCount,
            )
        }

        val q = search.trim().lowercase()
        val minPriceNum = minPrice.trim().takeIf { it.isNotBlank() }?.toDoubleOrNull()
        val maxPriceNum = maxPrice.trim().takeIf { it.isNotBlank() }?.toDoubleOrNull()

        var filtered = items

        if (!selectedCategory.isNullOrBlank()) {
            val cat = normalizeCategoryForApi(selectedCategory)
            filtered = filtered.filter { it.category == cat }
        }

        if (q.isNotBlank()) {
            filtered = filtered.filter {
                it.name.lowercase().contains(q) || (it.description ?: "").lowercase().contains(q)
            }
        }

        if (minRating != null) {
            filtered = filtered.filter { (it.averageRating ?: 0.0) >= minRating.toDouble() }
        }

        if (minPriceNum != null) {
            filtered = filtered.filter { (it.price?.toDoubleOrNull() ?: 0.0) >= minPriceNum }
        }

        if (maxPriceNum != null) {
            filtered = filtered.filter { (it.price?.toDoubleOrNull() ?: 0.0) <= maxPriceNum }
        }

        fun cmp(a: ProductDto, b: ProductDto): Int {
            val asc = sortDir.equals("ASC", ignoreCase = true)
            val result = when (sortBy) {
                "price" -> (a.price?.toDoubleOrNull() ?: 0.0).compareTo(b.price?.toDoubleOrNull() ?: 0.0)
                "averageRating" -> (a.averageRating ?: 0.0).compareTo(b.averageRating ?: 0.0)
                "name" -> a.name.compareTo(b.name)
                else -> (a.reviewCount ?: 0L).compareTo(b.reviewCount ?: 0L)
            }
            return if (asc) result else -result
        }

        return filtered.sortedWith(::cmp)
    }

    private fun normalizeCategoryForApi(category: String?): String? {
        val raw = category?.trim().orEmpty()
        if (raw.isBlank()) return null
        val norm = raw.lowercase()

        if (norm == "electronics") return "ELECTRONICS"
        if (norm == "clothing") return "CLOTHING"
        if (norm == "books") return "BOOKS"

        if (norm == "home & kitchen" || norm == "home and kitchen" || norm == "homekitchen") return "HOME & KITCHEN"
        if (norm == "sports & outdoors" || norm == "sports and outdoors" || norm == "sportsoutdoors") return "SPORTS & OUTDOORS"

        if (raw == raw.uppercase()) return raw
        return raw.uppercase()
    }
}
