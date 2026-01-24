package com.umc.record.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class SpeechBubbleTopTailShape(
    private val cornerRadius: Dp = 18.dp,
    private val tailWidth: Dp = 22.dp,
    private val tailHeight: Dp = 10.dp,
    private val tailOffsetFromRight: Dp = 12.dp, // 오른쪽에서 얼마나 떨어질지
    private val tailTipRatio: Float = 0.5f,     // 꼬리 꼭짓점 x 비율(0~1)
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = with(density) {
        val r = cornerRadius.toPx()
        val tw = tailWidth.toPx()
        val th = tailHeight.toPx()
        val offR = tailOffsetFromRight.toPx()

        val left = 0f
        val right = size.width
        val bottom = size.height

        val tailStartX = (right - offR - tw).coerceIn(left + r, right - r - tw)
        val tailTipX = tailStartX + (tw * tailTipRatio)

        val capR = min(th, tw / 2f) * 0.55f  // 뭉특함 정도(원하면 0.45~0.7 사이로 조절)
        val capCenter = Offset(tailTipX, capR)

        val startDeg = 210f
        val sweepDeg = 120f

        fun polarPoint(deg: Float): Offset {
            val rad = Math.toRadians(deg.toDouble())
            return Offset(
                x = capCenter.x + cos(rad).toFloat() * capR,
                y = capCenter.y + sin(rad).toFloat() * capR
            )
        }

        val pL = polarPoint(startDeg)

        val capRect = Rect(
            left = capCenter.x - capR,
            top = capCenter.y - capR,
            right = capCenter.x + capR,
            bottom = capCenter.y + capR
        )

        val path = Path().apply {
            moveTo(left + r, th)

            // --- 꼬리 부분(팁만 둥글게) ---
            lineTo(tailStartX, th)    // 베이스 왼쪽
            lineTo(pL.x, pL.y)         // 팁 캡 시작점까지 올라감

            arcTo(
                rect = capRect,
                startAngleDegrees = startDeg,
                sweepAngleDegrees = sweepDeg,
                forceMoveTo = false
            )

            lineTo(tailStartX + tw, th) // 베이스 오른쪽 복귀

            // --- 상단 우측 라운드 ---
            lineTo(right - r, th)
            arcTo(
                rect = Rect(right - 2 * r, th, right, th + 2 * r),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // --- 우측 ---
            lineTo(right, bottom - r)
            arcTo(
                rect = Rect(right - 2 * r, bottom - 2 * r, right, bottom),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // --- 하단 ---
            lineTo(left + r, bottom)
            arcTo(
                rect = Rect(left, bottom - 2 * r, left + 2 * r, bottom),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // --- 좌측 ---
            lineTo(left, th + r)
            arcTo(
                rect = Rect(left, th, left + 2 * r, th + 2 * r),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            close()
        }

        Outline.Generic(path)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSpeechBubbleTopTailShape() {
    val shape = SpeechBubbleTopTailShape(
        cornerRadius = 16.dp,
        tailWidth = 22.dp,
        tailHeight = 10.dp,
        tailOffsetFromRight = 0.dp
    )

    val bg = Color(0xCCDFE8FF)
    val tailHeight = 10.dp

    Box(
        modifier = Modifier
            .size(width = 240.dp, height = 220.dp)
            .background(bg, shape)
            .clip(shape)
            .border(1.dp, Color(0x664C7AF8), shape)
            .padding(top = tailHeight)
            .padding(16.dp)
    ) {
        Column {
            Text("일상")
            Text("말풍선 꼬리 프리뷰용 텍스트")
        }
    }
}
