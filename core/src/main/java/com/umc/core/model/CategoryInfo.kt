package com.umc.core.model

import com.umc.design.CategoryColor

data class CategoryInfo(
    val id: Long,
    val name: String,
    val color: CategoryColor,
    val count: Int,
)