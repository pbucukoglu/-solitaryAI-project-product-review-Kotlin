package com.reportnami.claro.data.auth

import com.reportnami.claro.data.api.model.LoginRequestDto
import com.reportnami.claro.data.api.model.LoginResponseDto
import com.reportnami.claro.data.api.model.RegisterRequestDto
import com.reportnami.claro.data.api.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) : AuthRepository {
    
    override suspend fun login(email: String, password: String): Result<AuthResult> {
        return try {
            val request = LoginRequestDto(email, password)
            val response = apiService.login(request)
            
            val authResult = AuthResult(
                accessToken = response.accessToken,
                tokenType = response.tokenType,
                expiresIn = response.expiresIn,
                roles = response.roles
            )
            
            // Save auth data locally
            authPreferences.saveAuthData(authResult, email)
            
            // Save user info from response
            val user = com.reportnami.claro.data.auth.User(
                id = response.userId ?: 1L,
                email = response.email ?: email,
                fullName = response.fullName ?: "User",
                role = response.role ?: "User",
                enabled = true
            )
            authPreferences.saveUserInfo(user)
            
            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun register(email: String, password: String, fullName: String): Result<String> {
        return try {
            val request = RegisterRequestDto(email, password, fullName)
            val response = apiService.register(request)
            
            // Return success message, user must login explicitly after registration
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout() {
        try {
            // Clear local auth data (no server logout endpoint in our implementation)
            authPreferences.clearAuthData()
        } catch (e: Exception) {
            // Continue with local logout even if something fails
        }
    }
    
    override fun getCurrentUser(): Flow<User?> = authPreferences.getCurrentUser()
    
    override fun getAuthToken(): Flow<String?> = authPreferences.getAuthToken()
    
    override fun isLoggedIn(): Flow<Boolean> = authPreferences.isLoggedIn()
}
