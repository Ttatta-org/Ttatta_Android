package com.umc.core.model

import com.umc.design.CategoryColor
import java.time.LocalDate

data class DiaryForCard(
    val id: Long,
    val date: LocalDate,
    val color: CategoryColor?,
    val content: String,
    val imageUrl: String,
)