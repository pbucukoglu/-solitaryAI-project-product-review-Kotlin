package com.reportnami.claro.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reportnami.claro.data.auth.AuthRepository
import com.reportnami.claro.data.preferences.SettingsPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    val currentUser = authRepository.getCurrentUser()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsPreferences.darkMode,
                settingsPreferences.fontSize,
                settingsPreferences.fontScale
            ) { darkMode, fontSize, fontScale ->
                _uiState.value = _uiState.value.copy(
                    isDarkMode = darkMode,
                    fontSize = fontSize,
                    fontScale = fontScale
                )
            }.collect {}
        }
    }
    
    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_uiState.value.isDarkMode
            settingsPreferences.setDarkMode(newValue)
            _uiState.value = _uiState.value.copy(isDarkMode = newValue)
        }
    }
    
    fun updateFontSize(fontSize: String) {
        viewModelScope.launch {
            settingsPreferences.setFontSize(fontSize)
            _uiState.value = _uiState.value.copy(fontSize = fontSize)
        }
    }
    
    fun updateFontScale(fontScale: Float) {
        viewModelScope.launch {
            settingsPreferences.setFontScale(fontScale)
            _uiState.value = _uiState.value.copy(fontScale = fontScale)
        }
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
    val fontScale: Float = 1.0f,
    val isLoading: Boolean = false,
    val error: String? = null
)
