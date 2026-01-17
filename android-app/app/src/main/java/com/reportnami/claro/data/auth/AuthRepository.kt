package com.reportnami.claro.data.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthResult>
    suspend fun register(email: String, password: String, fullName: String): Result<String>
    suspend fun logout()
    fun getCurrentUser(): Flow<User?>
    fun getAuthToken(): Flow<String?>
    fun isLoggedIn(): Flow<Boolean>
}

data class AuthResult(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val roles: List<String>
)

data class User(
    val id: Long,
    val email: String,
    val fullName: String,
    val role: String,
    val enabled: Boolean
)
