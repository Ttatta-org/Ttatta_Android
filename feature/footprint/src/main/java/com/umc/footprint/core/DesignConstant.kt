package com.umc.footprint.core

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

object DesignConstant {
    val LocatorSize = DpSize(48.dp, 48.dp)
    val ClusterMarkerMaxSize = DpSize(128.dp, 128.dp)
    val MarkerSize = DpSize(64.dp, 64.dp)

    val DiaryCardSize = DpSize(249.dp, 298.dp)
    val DiaryCardHorizontalPageArrowSize = DpSize(34.dp, 86.dp)
    val DiaryCardFrameShadowRadius = 10.dp
    val DiaryCardFrameShadowYOffset = 4.dp

    val ClusteringDistance = 32.dp

    val DiaryCardSizeWithShadowArea get() = DpSize(
        DiaryCardSize.width + DiaryCardFrameShadowRadius * 2,
        DiaryCardSize.height + DiaryCardFrameShadowRadius * 2
    )

    val DiaryCardSizeWithArrowArea get() = DpSize(
        DiaryCardSizeWithShadowArea.width + DiaryCardHorizontalPageArrowSize.width * 2,
        DiaryCardSizeWithShadowArea.height,
    )
}