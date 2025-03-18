package com.umc.login.logic

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color

data class StateMessage(
    @StringRes val id: Int,
    val color: Color,
)
