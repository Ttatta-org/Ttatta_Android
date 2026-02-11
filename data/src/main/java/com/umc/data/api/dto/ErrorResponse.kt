package com.umc.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
)
