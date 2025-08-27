package com.umc.footprint.component.card

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.addSvg
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.R
import com.umc.footprint.core.DesignConstant
import com.umc.footprint.model.prop.DiaryCardFrameProp
import java.time.LocalDate

val contentSize = DpSize(220.dp, 220.dp)
val contentOffset = DpOffset(14.dp, 53.dp)
const val diaryCardFrameAnimationDurationMillis = 500
private val contentBorderRadius = 8.dp
private val diaryColorAnimationSpec = tween<Color>(
    durationMillis = diaryCardFrameAnimationDurationMillis,
    easing = LinearEasing,
)
const val path = "M14 0.75H234.148C241.466 0.75 247.398 6.68223 247.398 14V272.449C247.398 279.767 241.466 285.699 234.148 285.699H141.316C136.516 285.699 132.144 288.463 130.085 292.8C127.339 298.583 119.109 298.583 116.363 292.8C114.304 288.463 109.933 285.699 105.133 285.699H14C6.68231 285.699 0.750132 279.767 0.75 272.449V14C0.75 6.68223 6.68223 0.75 14 0.75Z"

@Composable
fun DiaryCardFrame(prop: DiaryCardFrameProp) {
    val density = LocalDensity.current
    val path = remember { Path().apply { addSvg(pathData = path) } }
    val scaleFactor = remember { with(density) { DesignConstant.DiaryCardSize.width.toPx() } / path.getBounds().width }

    val borderColor by animateColorAsState(
        targetValue = prop.borderColor,
        animationSpec = diaryColorAnimationSpec
    )
    val backgroundColor by animateColorAsState(
        targetValue = prop.backgroundColor,
        animationSpec = diaryColorAnimationSpec
    )
    val contentContainerColor by animateColorAsState(
        targetValue = prop.contentContainerColor,
        animationSpec = diaryColorAnimationSpec
    )

    Box(
        modifier = Modifier.size(DesignConstant.DiaryCardSize)
    ) {
        // 그림자
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(
                    radiusX = DesignConstant.DiaryCardFrameShadowRadius,
                    radiusY = DesignConstant.DiaryCardFrameShadowRadius,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded,
                )
                .offset(y = DesignConstant.DiaryCardFrameShadowYOffset)
        ) {
            scale(scale = scaleFactor, pivot = Offset.Zero) {
                drawPath(
                    path = path,
                    color = Color(0xFF999999).copy(alpha = 0.5f),
                    style = Fill,
                )
            }
        }
        // 카드
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            scale(scale = scaleFactor, pivot = Offset.Zero) {
                drawPath(
                    path = path, color = backgroundColor, style = Fill
                )

                drawPath(
                    path = path,
                    color = borderColor,
                    style = Stroke(
                        width = 1.5f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )

                drawRoundRect(
                    color = contentContainerColor,
                    topLeft = with(density) {
                        Offset(
                            x = contentOffset.x.value, y = contentOffset.y.value
                        )
                    },
                    size = with(density) {
                        Size(
                            width = contentSize.width.value, height = contentSize.height.value
                        )
                    },
                    cornerRadius = CornerRadius(contentBorderRadius.value),
                )
            }
        }
        Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 30.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 데코
                Image(
                    painter = painterResource(id = com.umc.design.R.drawable.ic_header_deco),
                    contentDescription = null,
                    modifier = Modifier.width(32.dp),
                )
                // 날짜
                Text(
                    text = prop.date?.let { date ->
                        "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일"
                    } ?: stringResource(id = R.string.loading),
                    fontSize = with(density) { 12.dp.toSp() },
                    letterSpacing = with(density) { (-0.4).dp.toSp() },
                    color = borderColor,
                )
            }
            prop.onModifyButtonClicked?.let { onClicked ->
                IconButton(
                    onClick = onClicked, modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_modify),
                        contentDescription = null,
                        tint = borderColor,
                    )
                }
            }
        }
        // 내용
        Box(
            modifier = Modifier
                .offset(x = contentOffset.x, y = contentOffset.y)
                .size(contentSize)
                .graphicsLayer(clip = true, shape = RoundedCornerShape(contentBorderRadius)),
        ) {
            prop.content()
        }
    }
}

private val previewDiaryCardFrameProp = DiaryCardFrameProp(
    date = LocalDate.now(),
    borderColor = Color(0xFF000000),
    backgroundColor = Color(0xFFEEEEEE),
    contentContainerColor = Color(0xFF555555),
    onModifyButtonClicked = null,
    content = {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(text = "DiaryCardFrame")
        }
    },
)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ThemeProvider {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            DiaryCardFrame(prop = previewDiaryCardFrameProp)
        }
    }
}