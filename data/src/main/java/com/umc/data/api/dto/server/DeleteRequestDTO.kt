package com.umc.data.api.dto.server

import com.squareup.moshi.Json

data class DeleteRequestDTO(
    @Json(name = "reason")
    val reason: String? = null
)