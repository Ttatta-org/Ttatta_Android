package com.umc.category.model

import com.umc.design.CategoryColor

data class CategoryListItemProp(
    val name: String,
    val color: CategoryColor?,
    val onClicked: (() -> Unit)?,
)