package com.umc.footprint.design

import androidx.annotation.DrawableRes
import com.umc.footprint.R

enum class CategoryColor(
    @DrawableRes val flowerIconId: Int,
    @DrawableRes val footIconId: Int,
) {
    RED(R.drawable.ic_flower_red, R.drawable.ic_foot_red),
    ORANGE(R.drawable.ic_flower_orange, R.drawable.ic_foot_orange),
    YELLOW(R.drawable.ic_flower_yellow, R.drawable.ic_foot_yellow),
    GREEN(R.drawable.ic_flower_green, R.drawable.ic_foot_green),
    TURQUOISE(R.drawable.ic_flower_turquoise, R.drawable.ic_foot_turquoise),
    BLUE(R.drawable.ic_flower_blue, R.drawable.ic_foot_blue),
    NAVY(R.drawable.ic_flower_navy, R.drawable.ic_foot_navy),
    PURPLE(R.drawable.ic_flower_purple, R.drawable.ic_foot_purple),
    BROWN(R.drawable.ic_flower_brown, R.drawable.ic_foot_brown),
    WHITE(R.drawable.ic_flower_white, R.drawable.ic_foot_white),
    PINK(R.drawable.ic_flower_pink, R.drawable.ic_foot_pink),
    BLACK(R.drawable.ic_flower_black, R.drawable.ic_foot_black),
}