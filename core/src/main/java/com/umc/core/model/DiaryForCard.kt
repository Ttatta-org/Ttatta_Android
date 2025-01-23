package com.umc.core.model

import java.time.LocalDate

data class DiaryForCard(
    val id: Long,
    val date: LocalDate,
    val content: String,
    val imageUrl: String,
)