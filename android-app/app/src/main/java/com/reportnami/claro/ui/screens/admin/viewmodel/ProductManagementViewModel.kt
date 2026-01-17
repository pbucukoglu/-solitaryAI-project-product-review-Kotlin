package com.reportnami.claro.ui.screens.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reportnami.claro.data.api.ApiService
import com.reportnami.claro.data.api.model.ProductDto
import com.reportnami.claro.data.auth.AuthPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductManagementViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductManagementUiState())
    val uiState: StateFlow<ProductManagementUiState> = _uiState.asStateFlow()
    
    init {
        loadProducts()
    }
    
    fun loadProducts() {
        viewModelScope.launch {
            try {
                println("DEBUG: ProductManagementViewModel - Loading products...")
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // Get all products with large page size
                val products = apiService.getProductsPaginated(
                    page = 0,
                    size = 1000, // Large number to get all products
                    sortBy = "id",
                    sortDir = "ASC"
                )
                println("DEBUG: ProductManagementViewModel - API call completed, products count: ${products.content.size}")
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    products = products.content,
                    error = null
                )
                println("DEBUG: ProductManagementViewModel - UI state updated with ${products.content.size} products")
            } catch (e: Exception) {
                println("DEBUG: ProductManagementViewModel - Error loading products: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network Error: ${e.message}"
                )
            }
        }
    }
    
    fun deleteProduct(productId: Long) {
        println("DEBUG: deleteProduct called with productId: $productId")
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val token = authPreferences.getTokenSync()
                println("DEBUG: Token retrieved: ${if (token.isNullOrEmpty()) "null/empty" else "present"}")
                
                if (token.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Authentication required"
                    )
                    return@launch
                }
                
                println("DEBUG: Making API call to delete product $productId")
                val response = apiService.deleteProduct("Bearer $token", productId)
                println("DEBUG: API response - successful: ${response.isSuccessful}, code: ${response.code()}")
                
                if (response.isSuccessful) {
                    // Remove product from list
                    val updatedProducts = _uiState.value.products.filter { it.id != productId }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        products = updatedProducts,
                        error = null,
                        deleteSuccess = true
                    )
                    
                    // Reset delete success after a delay
                    kotlinx.coroutines.delay(2000)
                    _uiState.value = _uiState.value.copy(deleteSuccess = false)
                } else {
                    println("DEBUG: Delete failed - response code: ${response.code()}, message: ${response.message()}")
                    val errorMessage = when (response.code()) {
                        401 -> "Unauthorized: Admin access required"
                        403 -> "Forbidden: Insufficient permissions"
                        404 -> "Product not found"
                        500 -> "Server Error: Please try again later"
                        else -> "Error: ${response.message()}"
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            } catch (e: Exception) {
                println("DEBUG: Exception in deleteProduct: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network Error: ${e.message}"
                )
            }
        }
    }
    
    fun resetState() {
        _uiState.value = ProductManagementUiState()
    }
    
    fun refresh() {
        loadProducts()
    }
}

data class ProductManagementUiState(
    val isLoading: Boolean = false,
    val products: List<ProductDto> = emptyList(),
    val error: String? = null,
    val deleteSuccess: Boolean = false
)
