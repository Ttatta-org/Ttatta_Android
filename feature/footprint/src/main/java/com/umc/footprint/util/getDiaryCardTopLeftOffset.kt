package com.umc.footprint.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.umc.footprint.core.DesignConstant

fun getDiaryCardTopLeftOffset(
    density: Density,
    markerOffset: Offset,
    includeArrowArea: Boolean = false,
): Offset {
    val diaryCardSize = if (includeArrowArea) DesignConstant.DiaryCardSizeWithArrowArea else DesignConstant.DiaryCardSizeWithShadowArea

    return with(density) {
        Offset(
            x = markerOffset.x - diaryCardSize.width.toPx() / 2,
            y = markerOffset.y - DesignConstant.MarkerSize.height.toPx() / 2 - diaryCardSize.height.toPx(),
        )
    }
}