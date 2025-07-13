package com.umc.footprint.core

import androidx.compose.ui.geometry.Offset
import com.umc.design.CategoryColor

data class MapMarker(
    val latitude: Double,
    val longitude: Double,
    val zIndex: Int,
    val color: CategoryColor? = null,
    val isOverlapping: Boolean,  // 여러 개의 일기가 겹쳐져 있는지에 대한 여부
    val onClicked: ((Offset) -> (() -> Unit)?)? = null,
)