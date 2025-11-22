package com.umc.core.model

import java.time.LocalTime

// 개별 동작 결과(ON/변경 공통)
data class AlarmResult(
    val active: Boolean,
    val time: LocalTime? = null,
    val hoursAgo: Int? = null
)