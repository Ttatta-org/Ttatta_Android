package com.umc.data.api.dto.server

import com.squareup.moshi.Json
// @JsonClass(generateAdapter = true)
data class DailySummaryAlarm(
    @Json(name = "isActive")  val isActive: String? = null,
    @Json(name = "alarmTime") val alarmTime: java.time.LocalTime? = null
)