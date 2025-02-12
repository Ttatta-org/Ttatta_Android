package com.umc.design.character

import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.umc.design.R

enum class CharacterType(
    @DrawableRes val bodyRes: Int,
    @DrawableRes val handRes: Int,
    val size: Size,
    val offset: Offset,
    val handSize: Size,
    val handOffset: Offset
) {
    TTOTTO(
        bodyRes = R.drawable.img_ttotto,
        handRes = R.drawable.img_ttotto_hand,
        size = Size(184f, 190f),
        offset = Offset(0f, 41f),
        handSize = Size(32f, 48f),
        handOffset = Offset(121f, 73f),
    ),
    TTUTTU(
        bodyRes = R.drawable.img_ttuttu,
        handRes = R.drawable.img_ttuttu_hand,
        size = Size(218f, 217f),
        offset = Offset(108f, 0f),
        handSize = Size(39f, 57f),
        handOffset = Offset(144f, 88f),
    )
}