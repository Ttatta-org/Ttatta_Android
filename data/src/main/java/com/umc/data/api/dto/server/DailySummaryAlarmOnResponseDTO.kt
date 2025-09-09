package com.umc.data.api.dto.server

import com.squareup.moshi.Json

/**
 * 하루 요약 알림 ON 응답 DTO
 */
data class DailySummaryAlarmOnResponseDTO(
    @Json(name = "alarmTime")
    val alarmTime: java.time.LocalTime? = null
)
