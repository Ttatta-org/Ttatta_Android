package com.umc.core.model

import com.umc.design.CategoryColor
import java.time.LocalDate

data class DiaryForRemind(
    val id: Long,
    val isClustered: Boolean,
    val date: LocalDate,
    val color: CategoryColor?,
    val content: String,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
)

