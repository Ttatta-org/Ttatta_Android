package com.umc.challenge.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.round

@Composable
fun ShadowBoxScope(
    radius: Dp,
    color: Color,
    offset: DpOffset,
    content: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current

    Box {
        Box(
            modifier = Modifier
                .toShadow(
                    color = color,
                    inflate = radius
                )
                .offset {
                    Offset(
                        x = with(density) { offset.x.toPx() },
                        y = with(density) { offset.y.toPx() }
                    ).round()
                }
                .blur(radius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        ) {
            content()
        }
        content()
    }
}

private fun Modifier.toShadow(
    color: Color,
    inflate: Dp,
): Modifier = this.then(
    Modifier.drawWithContent {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                colorFilter = ColorFilter.tint(color = color)
            }

            canvas.saveLayer(size.toRect().inflate(delta = inflate.toPx()), paint)
            drawContent()
            canvas.restore()
        }
    }
)