package com.umc.data.api.dto.server

import com.squareup.moshi.Json
// import com.squareup.moshi.JsonClass
// @JsonClass(generateAdapter = true)
data class WritingDiaryAlarm(
    @Json(name = "isActive")  val isActive: String? = null,   // "ON" | "OFF"
    @Json(name = "alarmTime") val alarmTime: java.time.LocalTime? = null
)