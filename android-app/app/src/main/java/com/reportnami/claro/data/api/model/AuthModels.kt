package com.reportnami.claro.data.api.model

data class AuthRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val email: String,
    val password: String,
    val name: String
)

data class AuthResponseDto(
    val token: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserDto
)

data class UserDto(
    val id: Long,
    val email: String,
    val name: String,
    val role: String,
    val avatar: String? = null
)

data class RefreshTokenRequestDto(
    val refreshToken: String
)

data class RefreshTokenResponseDto(
    val token: String,
    val refreshToken: String,
    val expiresIn: Long
)
