package com.umc.footprint.model.event

import androidx.compose.ui.geometry.Offset
import com.umc.design.CategoryColor

/**
 * 발자국 마커가 클릭되고 화면 이동 등의 조치가 끝났을 때 발생하는 이벤트
 */
data class ModifiedMapMarkerClickedEvent(
    val offset: Offset,
    val clusterId: Long,
    val color: CategoryColor?
)
