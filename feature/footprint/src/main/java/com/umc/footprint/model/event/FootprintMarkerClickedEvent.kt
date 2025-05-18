package com.umc.footprint.model.event

import androidx.compose.ui.geometry.Offset

data class FootprintMarkerClickedEvent(
    val offset: Offset,
    val latitude: Double,
    val longitude: Double,
    val clusterId: Long,
)