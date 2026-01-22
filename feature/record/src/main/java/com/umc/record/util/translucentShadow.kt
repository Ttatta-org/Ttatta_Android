package com.umc.record.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.translucentShadow(
    color: Color = Color.Black,
    alpha: Float = 0.1f,
    shadowRadius: Dp = 10.dp,
    borderRadius: Dp = 0.dp,
    offsetY: Dp = 2.dp,
    offsetX: Dp = 0.dp
) = this.drawBehind {

    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()

        frameworkPaint.color = color
            .copy(alpha = 0f)
            .toArgb()

        frameworkPaint.setShadowLayer(
            shadowRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            color
                .copy(alpha = alpha)
                .toArgb()
        )

        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = borderRadius.toPx(),
            radiusY = borderRadius.toPx(),
            paint = paint
        )
    }
}