package com.umc.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
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

    CompositionLocalProvider(
        LocalColorTheme provides colorScheme,
        LocalFontTheme provides fontScheme,
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
