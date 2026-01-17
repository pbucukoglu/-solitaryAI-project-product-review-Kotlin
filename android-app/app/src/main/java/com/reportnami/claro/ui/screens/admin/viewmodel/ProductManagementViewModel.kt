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
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val response = apiService.getProducts()
                
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        products = products,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load products: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network Error: ${e.message}"
                )
            }
        }
    }
    
    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val token = authPreferences.getTokenSync()
                if (token.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Authentication required"
                    )
                    return@launch
                }
                
                val response = apiService.deleteProduct("Bearer $token", productId)
                
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
}

data class ProductManagementUiState(
    val isLoading: Boolean = false,
    val products: List<ProductDto> = emptyList(),
    val error: String? = null,
    val deleteSuccess: Boolean = false
)
