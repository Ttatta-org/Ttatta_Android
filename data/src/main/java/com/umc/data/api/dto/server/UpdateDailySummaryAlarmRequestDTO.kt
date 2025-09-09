package com.umc.data.api.dto.server

import com.squareup.moshi.Json

/**
 * 하루 요약 알림 시간 변경 요청 DTO
 * alarmTime 형식: "HH:mm:ss"
 */
data class UpdateDailySummaryAlarmRequestDTO(
    @Json(name = "alarmTime")
    val alarmTime: String? = null
)
