package com.umc.data.api.dto.server

data class SendVerificationMailFindPwRequestDTO(
    val username: String,
    val name: String,
    val email: String
) 