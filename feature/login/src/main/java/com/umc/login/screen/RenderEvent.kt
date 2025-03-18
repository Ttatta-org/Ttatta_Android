package com.umc.login.screen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class RenderEvent(
    val topLeft: Offset,
    val size: Size,
)