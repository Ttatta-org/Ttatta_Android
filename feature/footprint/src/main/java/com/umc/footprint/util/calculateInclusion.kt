package com.umc.footprint.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

fun calculateInclusion(
    innerOffset: Offset,
    innerSize: Size,
    outerOffset: Offset,
    outerSize: Size,
): Boolean {
    val left = outerOffset.x < innerOffset.x
    val top = outerOffset.y < innerOffset.y
    val right = innerOffset.x + innerSize.width < outerOffset.x + outerSize.width
    val bottom = innerOffset.y + innerSize.height < outerOffset.y + outerSize.height

    return top && bottom && left && right
}