package com.umc.core.model

import com.umc.design.CategoryColor

data class Footprint(
    val diaryId: Long,
    val categoryId: Long,
    val color: CategoryColor,
    val latitude: Double,
    val longitude: Double,
)