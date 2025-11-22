package com.umc.footprint.model.prop

import androidx.annotation.DrawableRes

data class CategoryItemProp(
    val name: String,
    @field:DrawableRes val icon: Int,
    val count: Int?,
    val onClicked: () -> Unit,
)