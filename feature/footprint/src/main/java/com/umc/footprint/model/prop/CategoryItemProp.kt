package com.umc.footprint.model.prop

import com.umc.design.CategoryColor

data class CategoryItemProp(
    val name: String,
    val color: CategoryColor?,
    val count: Int,
    val onClicked: () -> Unit,
)