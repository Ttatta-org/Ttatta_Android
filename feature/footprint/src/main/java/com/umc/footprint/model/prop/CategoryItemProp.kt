package com.umc.footprint.model.prop

import androidx.annotation.RawRes

data class CategoryItemProp(
    val name: String,
    @param:RawRes val icon: Int,
    val count: Int?,
    val onClicked: () -> Unit,
)