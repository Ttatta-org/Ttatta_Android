package com.umc.footprint.model.event

import androidx.compose.ui.geometry.Offset
import com.umc.design.CategoryColor

data class ModifiedFootprintMarkerClickedEvent(
    val offset: Offset,
    val clusterId: Long,
    val color: CategoryColor?
)
