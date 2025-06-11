package com.umc.footprint.model.event

import androidx.compose.ui.geometry.Offset
import com.umc.design.CategoryColor

data class MapMarkerClickedEvent(
    val offset: Offset,
    val latitude: Double,
    val longitude: Double,
    val clusterId: Long,
    val color: CategoryColor?,
)