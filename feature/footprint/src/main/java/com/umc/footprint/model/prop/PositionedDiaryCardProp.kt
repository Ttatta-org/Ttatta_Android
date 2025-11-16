package com.umc.footprint.model.prop

import androidx.compose.ui.geometry.Offset

data class PositionedDiaryCardProp(
    val offset: Offset,
    val prop: DiaryCardProp,
    val showMarker: Boolean,
    val isMarkerBook: Boolean,
)