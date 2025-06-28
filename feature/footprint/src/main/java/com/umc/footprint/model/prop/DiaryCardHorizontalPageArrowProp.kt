package com.umc.footprint.model.prop

import androidx.compose.ui.graphics.Color

enum class DiaryCardHorizontalPageArrowDirection {
    LEFT,
    RIGHT
}

data class DiaryCardHorizontalPageArrowProp(
    val direction: DiaryCardHorizontalPageArrowDirection,
    val outerColor: Color,
    val innerColor: Color,
    val colorAnimationDuration: Int,
    val onClicked: () -> Unit,
)