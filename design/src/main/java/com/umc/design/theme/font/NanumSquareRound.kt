package com.umc.design.theme.font

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.umc.design.R

data object NanumSquareRound: ThemeFontScheme(
    font = FontFamily(
        Font(R.font.nanum_square_round_eb, FontWeight.Black),
        Font(R.font.nanum_square_round_eb, FontWeight.ExtraBold),
        Font(R.font.nanum_square_round_b, FontWeight.Bold),
        Font(R.font.nanum_square_round_b, FontWeight.SemiBold),
        Font(R.font.nanum_square_round_r, FontWeight.Medium),
        Font(R.font.nanum_square_round_r, FontWeight.Normal),
        Font(R.font.nanum_square_round_l, FontWeight.Light),
        Font(R.font.nanum_square_round_l, FontWeight.ExtraLight),
        Font(R.font.nanum_square_round_l, FontWeight.Thin),
    )
)