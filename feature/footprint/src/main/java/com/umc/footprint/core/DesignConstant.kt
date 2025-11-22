package com.umc.footprint.core

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.umc.design.CategoryColor
import com.umc.footprint.R

object DesignConstant {
    val LocatorSize = DpSize(48.dp, 48.dp)
    val ClusterMarkerMaxSize = DpSize(128.dp, 128.dp)
    val FootprintMarkerSize = DpSize(64.dp, 69.12.dp)
    val BookMarkerSize = DpSize(72.dp, 72.dp)

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

    val FootprintMarkerResourceMap = mapOf(
        CategoryColor.RED to R.raw.ic_foot_red,
        CategoryColor.ORANGE to R.raw.ic_foot_orange,
        CategoryColor.YELLOW to R.raw.ic_foot_yellow,
        CategoryColor.GREEN to R.raw.ic_foot_green,
        CategoryColor.TURQUOISE to R.raw.ic_foot_turquoise,
        CategoryColor.BLUE to R.raw.ic_foot_blue,
        CategoryColor.NAVY to R.raw.ic_foot_navy,
        CategoryColor.PURPLE to R.raw.ic_foot_purple,
        CategoryColor.BROWN to R.raw.ic_foot_brown,
        CategoryColor.PINK to R.raw.ic_foot_pink,
        CategoryColor.WHITE to R.raw.ic_foot_white,
        CategoryColor.BLACK to R.raw.ic_foot_black,
    )

    val BookMarkerResourceMap = mapOf(
        CategoryColor.RED to R.raw.ic_book_red,
        CategoryColor.ORANGE to R.raw.ic_book_orange,
        CategoryColor.YELLOW to R.raw.ic_book_yellow,
        CategoryColor.GREEN to R.raw.ic_book_green,
        CategoryColor.TURQUOISE to R.raw.ic_book_turquoise,
        CategoryColor.BLUE to R.raw.ic_book_blue,
        CategoryColor.NAVY to R.raw.ic_book_navy,
        CategoryColor.PURPLE to R.raw.ic_book_purple,
        CategoryColor.BROWN to R.raw.ic_book_brown,
        CategoryColor.PINK to R.raw.ic_book_pink,
        CategoryColor.WHITE to R.raw.ic_book_white,
        CategoryColor.BLACK to R.raw.ic_book_black,
    )
}