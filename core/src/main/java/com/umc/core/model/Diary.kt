package com.umc.core.model

import java.time.LocalDateTime

data class Diary(
    val id: Long,
    val date: LocalDateTime,
    val content: String,
    val imageUrl: String,
    val locationName: String,
)