package com.umc.core.model

import java.time.LocalDateTime

data class DailySummary(
    val summary: String,
    val createdTime: LocalDateTime,
)
