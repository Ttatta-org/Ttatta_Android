package com.umc.design

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

enum class CategoryColor(
    @DrawableRes val flowerIconId: Int,
    @DrawableRes val footIconId: Int,
    val a: Color,
    val b: Color,
    val c: Color,
) {
    RED(
        flowerIconId = R.drawable.ic_flower_red,
        footIconId = R.drawable.ic_foot_red,
        a = Color(0xFFFFC0C0),
        b = Color(0xFFFF5252),
        c = Color(0xFFFFEEEE)
    ),
    ORANGE(
        flowerIconId = R.drawable.ic_flower_orange,
        footIconId = R.drawable.ic_foot_orange,
        a = Color(0xFFFFE0D3),
        b = Color(0xFFFF6A2B),
        c = Color(0xFFFFF5F0),
    ),
    YELLOW(
        flowerIconId = R.drawable.ic_flower_yellow,
        footIconId = R.drawable.ic_foot_yellow,
        a = Color(0xFFFFF4D4),
        b = Color(0xFFFFC832),
        c = Color(0xFFFFFAEB)
    ),
    GREEN(
        flowerIconId = R.drawable.ic_flower_green,
        footIconId = R.drawable.ic_foot_green,
        a = Color(0xFFE3FFCC),
        b = Color(0xFF6DD219),
        c = Color(0xFFF4FFEB),
    ),
    TURQUOISE(
        flowerIconId = R.drawable.ic_flower_turquoise,
        footIconId = R.drawable.ic_foot_turquoise,
        a = Color(0xFFD6FAF6),
        b = Color(0xFF51CCBD),
        c = Color(0xFFEDFFFD),
    ),
    BLUE(
        flowerIconId = R.drawable.ic_flower_blue,
        footIconId = R.drawable.ic_foot_blue,
        a = Color(0xFFD4EFFF),
        b = Color(0xFF2AB1F4),
        c = Color(0xFFECF8FF),
    ),
    NAVY(
        flowerIconId = R.drawable.ic_flower_navy,
        footIconId = R.drawable.ic_foot_navy,
        a = Color(0xFFD1DDFF),
        b = Color(0xFF4C7AF8),
        c = Color(0xFFEFF3FF),
    ),
    PURPLE(
        flowerIconId = R.drawable.ic_flower_purple,
        footIconId = R.drawable.ic_foot_purple,
        a = Color(0xFFEFD9FF),
        b = Color(0xFFB767EF),
        c = Color(0xFFF8EFFF),
    ),
    BROWN(
        flowerIconId = R.drawable.ic_flower_brown,
        footIconId = R.drawable.ic_foot_brown,
        a = Color(0xFFEBD9CF),
        b = Color(0xFFA5643F),
        c = Color(0xFFF3EEEC),
    ),
    WHITE(
        flowerIconId = R.drawable.ic_flower_white,
        footIconId = R.drawable.ic_foot_white,
        a = Color(0xFFFFFFFF),
        b = Color(0xFF999999),
        c = Color(0xFFE3E3E3),
    ),
    PINK(
        flowerIconId = R.drawable.ic_flower_pink,
        footIconId = R.drawable.ic_foot_pink,
        a = Color(0xFFFFC5E0),
        b = Color(0xFFFF459C),
        c = Color(0xFFFFE7F2),
    ),
    BLACK(
        flowerIconId = R.drawable.ic_flower_black,
        footIconId = R.drawable.ic_foot_black,
        a = Color(0xFFACACAC),
        b = Color(0xFF606060),
        c = Color(0xFFE3E3E3),
    ),
}