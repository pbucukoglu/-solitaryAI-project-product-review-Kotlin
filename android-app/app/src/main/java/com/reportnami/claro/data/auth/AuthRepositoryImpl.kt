package com.reportnami.claro.data.auth

import com.reportnami.claro.data.api.model.AuthRequestDto
import com.reportnami.claro.data.api.model.AuthResponseDto
import com.reportnami.claro.data.api.model.RefreshTokenRequestDto
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
            val request = AuthRequestDto(email, password)
            val response = apiService.login(request)
            
            val authResult = AuthResult(
                token = response.accessToken,
                tokenType = response.tokenType,
                expiresIn = response.expiresIn,
                roles = response.roles
            )
            
            // Save auth data locally
            authPreferences.saveAuthData(authResult)
            authPreferences.saveUserInfo(email, email) // Use email as fullName for now
            
            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun register(email: String, password: String, fullName: String): Result<AuthResult> {
        return try {
            val request = RegisterRequestDto(email, password, fullName)
            val response = apiService.register(request)
            
            val authResult = AuthResult(
                token = response.accessToken,
                tokenType = response.tokenType,
                expiresIn = response.expiresIn,
                roles = response.roles
            )
            
            // Save auth data locally
            authPreferences.saveAuthData(authResult)
            authPreferences.saveUserInfo(email, fullName)
            
            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refreshToken(): Result<String> {
        return try {
            // For now, we don't implement refresh token since backend doesn't support it
            // In a real app, you would implement this with refresh tokens
            Result.failure(Exception("Refresh token not implemented"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout() {
        try {
            // Call logout endpoint if available
            authPreferences.getTokenSync()?.let { token ->
                apiService.logout(token)
            }
        } catch (e: Exception) {
            // Continue with local logout even if server call fails
        } finally {
            // Clear local auth data
            authPreferences.clearAuthData()
        }
    }
    
    override fun getCurrentUser(): Flow<User?> = authPreferences.getCurrentUser()
    
    override fun getAuthToken(): Flow<String?> = authPreferences.getAuthToken()
    
    override fun isLoggedIn(): Flow<Boolean> = authPreferences.isLoggedIn()
}
