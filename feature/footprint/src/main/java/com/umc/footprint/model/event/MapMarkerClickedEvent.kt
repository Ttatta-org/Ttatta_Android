package com.umc.footprint.model.event

import androidx.compose.ui.geometry.Offset
import com.umc.design.CategoryColor

/**
 * 발자국 마커가 클릭되었을 때 최초로 발생하는 이벤트
 */
data class MapMarkerClickedEvent(
    val offset: Offset,
    val latitude: Double,
    val longitude: Double,
    val clusterId: Long,
    val isBook: Boolean,
    val color: CategoryColor?,
)