package com.umc.data.api.dto.server

data class CheckVerificationCodeRequestDTO(
    val email: String,
    val code: String
) 