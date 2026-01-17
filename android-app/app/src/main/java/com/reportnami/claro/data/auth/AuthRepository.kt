package com.reportnami.claro.data.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthResult>
    suspend fun register(email: String, password: String, name: String): Result<AuthResult>
    suspend fun refreshToken(): Result<String>
    suspend fun logout()
    fun getCurrentUser(): Flow<User?>
    fun getAuthToken(): Flow<String?>
    fun isLoggedIn(): Flow<Boolean>
}

data class AuthResult(
    val token: String,
    val refreshToken: String,
    val user: User,
    val expiresIn: Long
)

data class User(
    val id: Long,
    val email: String,
    val name: String,
    val role: String,
    val avatar: String? = null
)
