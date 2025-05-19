package com.umc.footprint.model.prop

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.time.LocalDate

class DiaryCardFrameProp(
    val date: LocalDate?,
    val borderColor: Color,
    val backgroundColor: Color,
    val contentContainerColor: Color,
    val onModifyButtonClicked: (() -> Unit)?,
    val content: @Composable () -> Unit,
)