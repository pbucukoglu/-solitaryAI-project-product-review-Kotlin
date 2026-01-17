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
        val TOKEN_KEY = stringPreferencesKey("access_token")
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_ROLE_KEY = stringPreferencesKey("user_role")
    }
    
    suspend fun saveAuthData(authResult: AuthResult, email: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = authResult.accessToken
            preferences[USER_EMAIL_KEY] = email
            // Extract user info from token or roles if needed
            preferences[USER_ROLE_KEY] = authResult.roles.firstOrNull()?.replace("ROLE_", "") ?: "USER"
        }
    }
    
    suspend fun saveUserInfo(user: User) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.id.toString()
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_NAME_KEY] = user.fullName
            preferences[USER_ROLE_KEY] = user.role
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
    
    fun getCurrentUser(): Flow<User?> = dataStore.data.map { preferences ->
        val userId = preferences[USER_ID_KEY]?.toLongOrNull()
        val email = preferences[USER_EMAIL_KEY]
        val name = preferences[USER_NAME_KEY]
        val role = preferences[USER_ROLE_KEY]
        
        if (userId != null && email != null && name != null && role != null) {
            User(userId, email, name, role, true)
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
}
