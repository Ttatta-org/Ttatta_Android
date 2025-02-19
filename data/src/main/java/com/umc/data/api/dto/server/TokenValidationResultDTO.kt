package com.umc.data.api.dto.server

data class TokenValidationResultDTO(
    val isRegistered: Boolean,
    val accessToken: String?,
    val refreshToken: String?
) 