package com.reportnami.claro.ui.screens.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reportnami.claro.data.api.ApiService
import com.reportnami.claro.data.auth.AuthPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()
    
    fun addProduct(
        name: String,
        description: String,
        category: String,
        price: Double,
        imageUrls: List<String>
    ) {
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
                
                val productRequest = mapOf(
                    "name" to name,
                    "description" to description,
                    "category" to category,
                    "price" to price,
                    "imageUrls" to imageUrls
                )
                
                val response = apiService.createProduct("Bearer $token", productRequest as java.util.Map<String, Any>)
                
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Unauthorized: Admin access required"
                        403 -> "Forbidden: Insufficient permissions"
                        400 -> "Bad Request: Invalid product data"
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
        _uiState.value = AddProductUiState()
    }
}

data class AddProductUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
