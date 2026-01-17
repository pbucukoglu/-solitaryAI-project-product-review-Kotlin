package com.reportnami.claro.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
        val TOKEN_TYPE_KEY = stringPreferencesKey("token_type")
        val EXPIRES_IN_KEY = stringPreferencesKey("expires_in")
        val USER_ROLES_KEY = stringSetPreferencesKey("user_roles")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_FULL_NAME_KEY = stringPreferencesKey("user_full_name")
    }
    
    suspend fun saveAuthData(authResult: AuthResult) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = authResult.token
            preferences[TOKEN_TYPE_KEY] = authResult.tokenType
            preferences[EXPIRES_IN_KEY] = authResult.expiresIn.toString()
            preferences[USER_ROLES_KEY] = authResult.roles.toSet()
        }
    }
    
    suspend fun saveUserInfo(email: String, fullName: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_FULL_NAME_KEY] = fullName
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
        val email = preferences[USER_EMAIL_KEY]
        val fullName = preferences[USER_FULL_NAME_KEY]
        val roles = preferences[USER_ROLES_KEY]?.toList() ?: emptyList()
        
        if (email != null && fullName != null) {
            User(0L, email, fullName, roles) // ID not needed for client side
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
    
    fun getRoles(): Flow<List<String>> = dataStore.data.map { preferences ->
        preferences[USER_ROLES_KEY]?.toList() ?: emptyList()
    }
}
