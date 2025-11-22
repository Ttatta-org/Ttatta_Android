package com.umc.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.umc.design.theme.color.Basic
import com.umc.design.theme.color.ThemeColorScheme
import com.umc.design.theme.font.NanumSquareRound
import com.umc.design.theme.font.ThemeFontScheme
import com.umc.design.theme.font.getTypography

val LocalColorTheme = compositionLocalOf<ThemeColorScheme> { Basic }
val LocalFontTheme = compositionLocalOf<ThemeFontScheme> { NanumSquareRound }

@Composable
fun ThemeProvider(
    colorScheme: ThemeColorScheme = Basic,
    fontScheme: ThemeFontScheme = NanumSquareRound,
    content: @Composable () -> Unit,
) {
    val currentTypography = MaterialTheme.typography
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val defaultDensity = LocalDensity.current
    val designWidth = remember { 390.dp }

    val scaledDensity = remember(
        key1 = screenWidth,
        key2 = designWidth,
        key3 = defaultDensity,
    ) {
        if (screenWidth < designWidth) {
            val scaleFactor = screenWidth.value / designWidth.value

            Density(
                density = defaultDensity.density * scaleFactor,
                fontScale = 1.0f * scaleFactor
            )
        } else {
            Density(
                density = defaultDensity.density,
                fontScale = 1.0f
            )
        }
    }

    CompositionLocalProvider(
        LocalColorTheme provides colorScheme,
        LocalFontTheme provides fontScheme,
        LocalDensity provides scaledDensity,
    ) {
        MaterialTheme(
            typography = remember(key1 = fontScheme, key2 = currentTypography) {
                getTypography(
                    currentTypography = currentTypography,
                    font = fontScheme.font,
                )
            },
        ) {
            content.invoke()
        }
    }
}
