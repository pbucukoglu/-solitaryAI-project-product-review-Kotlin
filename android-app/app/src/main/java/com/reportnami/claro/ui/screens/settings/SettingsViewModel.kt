package com.reportnami.claro.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reportnami.claro.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    val currentUser = authRepository.getCurrentUser()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            // Load user preferences
            _uiState.value = _uiState.value.copy(
                isDarkMode = false, // TODO: Load from preferences
                fontSize = "Medium", // TODO: Load from preferences
                language = "English" // TODO: Load from preferences
            )
        }
    }
    
    fun toggleDarkMode() {
        _uiState.value = _uiState.value.copy(
            isDarkMode = !_uiState.value.isDarkMode
        )
        // TODO: Save to preferences
    }
    
    fun updateFontSize(fontSize: String) {
        _uiState.value = _uiState.value.copy(fontSize = fontSize)
        // TODO: Save to preferences
    }
    
    fun updateLanguage(language: String) {
        _uiState.value = _uiState.value.copy(language = language)
        // TODO: Save to preferences
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val fontSize: String = "Medium",
    val language: String = "English",
    val isLoading: Boolean = false,
    val error: String? = null
)
