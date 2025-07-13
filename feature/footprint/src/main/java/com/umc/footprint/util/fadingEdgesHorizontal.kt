package com.umc.footprint.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

/**
 * 컴포저블의 양쪽 수평 끝을 지정된 Dp 너비만큼 투명하게 페이드 아웃 시키는 Modifier 확장 함수입니다.
 *
 * @param fadeWidth 각 끝에서 페이드 효과가 적용될 너비 (Dp 단위).
 */
fun Modifier.fadingEdgesHorizontal(fadeWidth: Dp): Modifier {
    return this
        .graphicsLayer(
            // alpha = 0.99f 대신 또는 함께 사용하여 명시적으로 오프스크린 버퍼 사용
            compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
        )
        .drawWithContent {
            drawContent() // 원본 콘텐츠를 먼저 그립니다.

            val widthPx = size.width // 컴포저블의 전체 너비 (픽셀 단위)
            val fadeWidthPx = fadeWidth.toPx() // Dp를 픽셀로 변환

            // 페이드 너비가 0 이하이거나 전체 너비가 0이면 페이드 효과를 적용할 필요가 없습니다.
            if (fadeWidthPx <= 0f || widthPx == 0f) {
                return@drawWithContent // 아무것도 더 그리지 않고 종료
            }

            // 각 끝에서 페이드될 비율을 계산합니다.
            // 한쪽 페이드 너비가 전체 너비의 절반을 넘지 않도록 합니다 (양쪽 페이드이므로).
            val fraction = (fadeWidthPx / widthPx).coerceAtMost(0.5f)

            val fadeBrushWithStops = Brush.horizontalGradient(
                colorStops = arrayOf(
                    0.0f to Color.Transparent,
                    fraction to Color.Black, // 왼쪽 페이드 종료 지점
                    (1.0f - fraction) to Color.Black, // 오른쪽 페이드 시작 지점
                    1.0f to Color.Transparent
                )
            )

            // 그라데이션을 콘텐츠 위에 그려 알파 마스크 역할을 하도록 합니다.
            drawRect(
                brush = fadeBrushWithStops,
                blendMode = BlendMode.DstIn
            )
        }
}