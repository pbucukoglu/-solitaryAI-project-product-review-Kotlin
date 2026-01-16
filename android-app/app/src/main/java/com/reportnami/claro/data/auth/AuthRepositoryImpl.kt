package com.reportnami.claro.data.auth

import com.reportnami.claro.data.api.model.AuthRequestDto
import com.reportnami.claro.data.api.model.AuthResponseDto
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
                token = response.token,
                refreshToken = response.refreshToken,
                user = User(
                    id = response.user.id,
                    email = response.user.email,
                    name = response.user.name,
                    role = response.user.role,
                    avatar = response.user.avatar
                ),
                expiresIn = response.expiresIn
            )
            
            // Save auth data locally
            authPreferences.saveAuthData(authResult)
            
            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun register(email: String, password: String, name: String): Result<AuthResult> {
        return try {
            val request = RegisterRequestDto(email, password, name)
            val response = apiService.register(request)
            
            val authResult = AuthResult(
                token = response.token,
                refreshToken = response.refreshToken,
                user = User(
                    id = response.user.id,
                    email = response.user.email,
                    name = response.user.name,
                    role = response.user.role,
                    avatar = response.user.avatar
                ),
                expiresIn = response.expiresIn
            )
            
            // Save auth data locally
            authPreferences.saveAuthData(authResult)
            
            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refreshToken(): Result<String> {
        return try {
            val refreshToken = authPreferences.getRefreshTokenSync()
            if (refreshToken == null) {
                return Result.failure(Exception("No refresh token available"))
            }
            
            val response = apiService.refreshToken(refreshToken)
            
            // Update token in preferences
            authPreferences.saveAuthData(
                AuthResult(
                    token = response.token,
                    refreshToken = response.refreshToken,
                    user = authPreferences.getCurrentUser().first() ?: return Result.failure(Exception("No user data")),
                    expiresIn = response.expiresIn
                )
            )
            
            Result.success(response.token)
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
