package com.umc.core.model

import java.time.LocalTime

// 화면 초기화용 도메인 모델
data class AlarmSummary(
    val writingActive: Boolean,
    val writingTime: LocalTime?,
    val memoryActive: Boolean,
    val challengeActive: Boolean,
    val challengeHoursAgo: Int?,
    val dailyActive: Boolean,
    val dailyTime: LocalTime?
)