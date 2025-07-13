package com.umc.design.theme.color

import androidx.compose.ui.graphics.Color

sealed class ThemeColorScheme(
    val isDark: Boolean,

    val primary: ColorMap,
    val secondary: ColorMap,
    val grey: ColorMap = ColorMap(
        100 to Color(0xFFF5F5F5),
        200 to Color(0xFFECECEC),
        300 to Color(0xFFE1E1E1),
        400 to Color(0xFFCACACA),
        500 to Color(0xFFB1B1B1),
        600 to Color(0xFF8E8E8E),
        700 to Color(0xFF4B4B4B),
    ),

    val positive: Color = Color(0xFF4CD955),
    val negative: Color = Color(0xFFFF6060),
)