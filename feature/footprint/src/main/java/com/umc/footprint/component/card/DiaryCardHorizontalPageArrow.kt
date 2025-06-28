package com.umc.footprint.component.card

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addSvg
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.umc.footprint.core.DesignConstant
import com.umc.footprint.model.prop.DiaryCardHorizontalPageArrowDirection
import com.umc.footprint.model.prop.DiaryCardHorizontalPageArrowProp

const val outerPath = "M19.7977 83.1224C20.2806 84.1151 19.8694 85.3129 18.8795 85.7971C17.8897 86.2814 16.6954 85.869 16.2125 84.8763L3.73452 59.2214C-1.16705 49.1438 -1.24359 37.39 3.50472 27.26L3.73452 26.7785L16.2126 1.12365L16.3119 0.944942C16.8445 0.0833341 17.9515 -0.251229 18.8795 0.202766C19.8075 0.656753 20.2266 1.73776 19.8776 2.68906L19.7977 2.87753L7.3197 28.5324L6.91561 29.3927C2.88225 38.3288 3.01738 48.622 7.3197 57.4675L19.7977 83.1224Z"
const val innerPath = "M33.7977 83.1224C34.2806 84.1151 33.8694 85.3129 32.8795 85.7971C31.8897 86.2814 30.6954 85.869 30.2125 84.8763L17.7345 59.2214C12.8329 49.1438 12.7564 37.39 17.5047 27.26L17.7345 26.7785L30.2126 1.12365L30.3119 0.944942C30.8445 0.0833341 31.9515 -0.251229 32.8795 0.202766C33.8075 0.656753 34.2266 1.73776 33.8776 2.68906L33.7977 2.87753L21.3197 28.5324L20.9156 29.3927C16.8822 38.3288 17.0174 48.622 21.3197 57.4675L33.7977 83.1224Z"

@Composable
fun DiaryCardHorizontalPageArrow(
    prop: DiaryCardHorizontalPageArrowProp,
) {
    val density = LocalDensity.current
    val outerPath = remember { Path().apply { addSvg(pathData = outerPath) } }
    val innerPath = remember { Path().apply { addSvg(pathData = innerPath) } }
    val scaleFactor = remember { with(density) { DesignConstant.DiaryCardHorizontalPageArrowSize.height.toPx() } / outerPath.getBounds().height }

    val outerColor by animateColorAsState(
        targetValue = prop.outerColor,
        animationSpec = tween(durationMillis = prop.colorAnimationDuration.toInt()),
    )

    val innerColor by animateColorAsState(
        targetValue = prop.innerColor,
        animationSpec = tween(durationMillis = prop.colorAnimationDuration.toInt()),
    )

    Canvas(
        modifier = Modifier
            .size(DesignConstant.DiaryCardHorizontalPageArrowSize)
            .let {
                when (prop.direction) {
                    DiaryCardHorizontalPageArrowDirection.LEFT -> it
                    DiaryCardHorizontalPageArrowDirection.RIGHT -> it.rotate(180f)
                }
            }
            .clickable(indication = null, interactionSource = null) { prop.onClicked() },
    ) {
        scale(scale = scaleFactor, pivot = Offset.Zero) {
            drawPath(path = outerPath, color = outerColor, style = Fill)
            drawPath(path = innerPath, color = innerColor, style = Fill)
        }
    }
}

private val previewDiaryCardHorizontalPageArrowProp = DiaryCardHorizontalPageArrowProp(
    direction = DiaryCardHorizontalPageArrowDirection.LEFT,
    outerColor = Color(0xAA000000),
    innerColor = Color(0x77000000),
    colorAnimationDuration = 200,
    onClicked = {},
)

@Preview(showBackground = true)
@Composable
fun DiaryCardHorizontalPageArrowPreview() {
    DiaryCardHorizontalPageArrow(
        prop = previewDiaryCardHorizontalPageArrowProp
    )
}