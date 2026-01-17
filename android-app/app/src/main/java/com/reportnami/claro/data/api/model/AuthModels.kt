package com.reportnami.claro.data.api.model

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val email: String,
    val password: String,
    val fullName: String
)

data class LoginResponseDto(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val roles: List<String>,
    val userId: Long? = null,
    val email: String? = null,
    val fullName: String? = null,
    val role: String? = null
)

data class AuthResponseDto(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val roles: List<String>
)

data class UserDto(
    val id: Long,
    val email: String,
    val fullName: String,
    val role: String,
    val enabled: Boolean
)

data class RefreshTokenRequestDto(
    val refreshToken: String
)

data class RefreshTokenResponseDto(
    val token: String,
    val refreshToken: String,
    val expiresIn: Long
)
