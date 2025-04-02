package com.umc.design.theme.color

import androidx.compose.ui.graphics.Color

data object Basic: ThemeColorScheme(
    isDark = false,
    primary = ColorMap(
        100 to Color(0xFFFFE6E1),
        200 to Color(0xFFFFD0C8),
        300 to Color(0xFFFFB1A5),
        400 to Color(0xFFFF9888),
        500 to Color(0xFFFF8072),
        600 to Color(0xFFFF7162),
    ),
    secondary = ColorMap(
        100 to Color(0xFFFEF6F2),
        200 to Color(0xFFFFEFE4),
        300 to Color(0xFFFDDDC1),
        400 to Color(0xFFFFD2AC),
    ),
)