package com.umc.design.theme.color

import androidx.compose.ui.graphics.Color

data object Basic: ThemeColorScheme(
    isDark = false,
    primary = ColorMap(
        200 to Color(0xFFFCAD98),
        300 to Color(0xFFFF9681),
        400 to Color(0xFFFF8072),
        500 to Color(0xFFFF7162),
    ),
    secondary = ColorMap(
        50 to Color(0xFFFFF7F4),
        100 to Color(0xFFFEF6F2),
        200 to Color(0xFFFFEFE4),
        300 to Color(0xFFFDDDC1),
    ),
    grey = ColorMap(
        100 to Color(0xFFF5F5F5),
        200 to Color(0xFFE1E1E1),
        300 to Color(0xFFCACACA),
        400 to Color(0xFF8E8E8E),
        500 to Color(0xFF4B4B4B),
    )
)