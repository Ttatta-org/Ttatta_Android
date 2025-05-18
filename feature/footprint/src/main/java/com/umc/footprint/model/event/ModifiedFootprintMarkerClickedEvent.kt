package com.umc.footprint.model.event

import androidx.compose.ui.geometry.Offset

data class ModifiedFootprintMarkerClickedEvent(
    val offset: Offset,
    val clusterId: Long,
)
