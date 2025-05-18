package com.umc.footprint.core

import androidx.compose.ui.geometry.Offset
import com.umc.design.CategoryColor

data class MapMarker(
    val latitude: Double,
    val longitude: Double,
    val zIndex: Int,
    val color: CategoryColor? = null,
    val onClicked: ((Offset) -> (() -> Unit)?)? = null,
)