package com.reportnami.claro.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Singleton
class AuthPreferences @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.authDataStore
    
    companion object {
        val TOKEN_KEY = stringPreferencesKey("auth_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_ROLE_KEY = stringPreferencesKey("user_role")
        val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
    }
    
    suspend fun saveAuthData(authResult: AuthResult) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = authResult.token
            preferences[REFRESH_TOKEN_KEY] = authResult.refreshToken
            preferences[USER_ID_KEY] = authResult.user.id.toString()
            preferences[USER_EMAIL_KEY] = authResult.user.email
            preferences[USER_NAME_KEY] = authResult.user.name
            preferences[USER_ROLE_KEY] = authResult.user.role
            preferences[USER_AVATAR_KEY] = authResult.user.avatar ?: ""
        }
    }
    
    suspend fun clearAuthData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    fun getAuthToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }
    
    fun getRefreshToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }
    
    fun getCurrentUser(): Flow<User?> = dataStore.data.map { preferences ->
        val userId = preferences[USER_ID_KEY]?.toLongOrNull()
        val email = preferences[USER_EMAIL_KEY]
        val name = preferences[USER_NAME_KEY]
        val role = preferences[USER_ROLE_KEY]
        val avatar = preferences[USER_AVATAR_KEY]?.takeIf { it.isNotEmpty() }
        
        if (userId != null && email != null && name != null && role != null) {
            User(userId, email, name, role, avatar)
        } else {
            null
        }
    }
    
    fun isLoggedIn(): Flow<Boolean> = dataStore.data.map { preferences ->
        !preferences[TOKEN_KEY].isNullOrBlank()
    }
    
    suspend fun getTokenSync(): String? {
        return dataStore.data.first()[TOKEN_KEY]
    }
    
    suspend fun getRefreshTokenSync(): String? {
        return dataStore.data.first()[REFRESH_TOKEN_KEY]
    }
}
