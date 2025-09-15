package com.umc.data.api.dto.server

import com.squareup.moshi.Json
// @JsonClass(generateAdapter = true)
data class MemoryDiaryAlarm(
    @Json(name = "isActive") val isActive: String? = null
)