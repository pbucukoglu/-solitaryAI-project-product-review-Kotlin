package com.reportnami.claro.data.api.model

data class AuthRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val email: String,
    val password: String,
    val fullName: String
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
    val roles: List<String>
)

data class RefreshTokenRequestDto(
    val refreshToken: String
)

data class RefreshTokenResponseDto(
    val token: String,
    val refreshToken: String,
    val expiresIn: Long
)
