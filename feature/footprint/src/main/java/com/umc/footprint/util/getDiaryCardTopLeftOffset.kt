package com.umc.footprint.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.umc.footprint.core.DesignConstant

fun getDiaryCardTopLeftOffset(density: Density, footprintOffset: Offset): Offset {
    return with(density) {
        Offset(
            x = footprintOffset.x - DesignConstant.DiaryCardSize.width.toPx() / 2,
            y = footprintOffset.y - DesignConstant.DiaryCardSize.height.toPx() - DesignConstant.MarkerSize.height.toPx() / 2,
        )
    }
}