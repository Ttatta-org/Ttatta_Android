package com.umc.login.model.event

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class RenderEvent(
    val topLeft: Offset,
    val size: Size,
)