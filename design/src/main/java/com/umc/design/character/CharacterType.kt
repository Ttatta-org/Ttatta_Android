package com.umc.design.character

import androidx.annotation.RawRes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.umc.design.R

enum class CharacterType(
    @get:RawRes val bodyRes: Int,
    @get:RawRes val handRes: Int,
    @get:RawRes val shadowRes: Int,
    val size: Size,
    val offset: Offset,
    val handSize: Size,
    val handOffset: Offset,
    val shadowSize: Size,
    val shadowOffset: Offset,
) {
    TTOTTO(
        bodyRes = R.raw.img_ttotto,
        handRes = R.raw.img_ttotto_hand,
        shadowRes = R.raw.img_ttotto_shadow,
        size = Size(185.6f, 172.89f),
        offset = Offset(0f, 48.93f),
        handSize = Size(25.18f, 47.98f),
        handOffset = Offset(122.92f, 75.72f),
        shadowSize = Size(199.68f, 29.34f),
        shadowOffset = Offset(-6.79f, 158f),
    ),
    TTUTTU(
        bodyRes = R.raw.img_ttuttu,
        handRes = R.raw.img_ttuttu_hand,
        shadowRes = R.raw.img_ttuttu_shadow,
        size = Size(227.61f, 212.02f),
        offset = Offset(103.62f, 0f),
        handSize = Size(45.76f, 61.21f),
        handOffset = Offset(150.74f, 90.5f),
        shadowSize = Size(189.27f, 28.39f),
        shadowOffset = Offset(16.57f, 196.33f),
    )
}