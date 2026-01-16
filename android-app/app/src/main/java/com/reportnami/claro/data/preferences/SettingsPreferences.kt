package com.reportnami.claro.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Use the same extension function as DeviceIdRepository
private val Context.dataStore by preferencesDataStore(name = "claro_prefs")

@Singleton
class SettingsPreferences @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore
    
    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val FONT_SIZE_KEY = stringPreferencesKey("font_size")
    }
    
    val darkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }
    
    val fontSize: Flow<String> = dataStore.data.map { preferences ->
        preferences[FONT_SIZE_KEY] ?: "Medium"
    }
    
    suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
        }
    }
    
    suspend fun setFontSize(fontSize: String) {
        dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = fontSize
        }
    }
}
