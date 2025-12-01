package com.umc.design.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.R
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import kotlin.math.asin
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun CustomHeader(
    showLogo: Boolean = true,
    showLogoWhiteShadow: Boolean = false,
    showShadow: Boolean = false,
    shadowColor: Color = Color(0xFFD7806F).copy(alpha = 0.35f),
    centerText: String? = null,
    centerTextColor: Color = LocalColorTheme.current.primary[500],
    onBackButtonClicked: (() -> Unit)? = null,
    backButtonColor: Color = LocalColorTheme.current.primary[400],
    headerTrailing: (@Composable RowScope.() -> Unit)? = null,
    headerTrailingStartPadding: Dp = 13.4.dp,
    backgroundColor: Color = Color.White,
    waveColor: Color = LocalColorTheme.current.primary[400],
    content: (@Composable ColumnScope.(waveHeight: Dp) -> Unit)? = null,
) {
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()

    val waveWidth = remember { 2.dp }
    val wavePeriod = remember { 21.dp }
    val waveRadius = remember { 16.235.dp }
    val shadowRadius = remember { 10.dp }

    val waveHeight = remember(wavePeriod, waveRadius, waveWidth) {
        waveRadius - sqrt(waveRadius.value.pow(2) - (wavePeriod.value / 2).pow(2)).dp + waveWidth
    }

    var solidHeight: Dp? by remember { mutableStateOf(null) }

    Box {
        solidHeight?.let { solidHeight ->
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(solidHeight)
            ) {
                val width = size.width
                val startX =
                    (width / 2 - wavePeriod.toPx() / 2).mod(wavePeriod.toPx()) - wavePeriod.toPx()

                val (fillPath, wavePath) = listOf(true, false).map { isFill ->
                    Path().apply {

                        if (isFill) {
                            moveTo(startX, 0f)
                            lineTo(startX, solidHeight.toPx())
                        } else {
                            moveTo(startX, solidHeight.toPx())
                        }

                        var currentX = startX
                        do {
                            val left = currentX - (waveRadius.toPx() - wavePeriod.toPx() / 2)
                            val top =
                                solidHeight.toPx() - waveWidth.toPx() / 2 - waveRadius.toPx() * 2
                            val startAngleRadian = asin(wavePeriod / (waveRadius * 2)) + Math.PI / 2
                            val sweepAngleRadian = (Math.PI / 2 - startAngleRadian) * 2

                            arcTo(
                                rect = Rect(
                                    left = left,
                                    top = top,
                                    right = left + waveRadius.toPx() * 2,
                                    bottom = top + waveRadius.toPx() * 2,
                                ),
                                startAngleDegrees = (startAngleRadian * 180f / Math.PI).toFloat(),
                                sweepAngleDegrees = (sweepAngleRadian * 180f / Math.PI).toFloat(),
                                forceMoveTo = false
                            )

                            currentX += wavePeriod.toPx()
                        } while (currentX < width)

                        if (isFill) {
                            lineTo(currentX, 0f)
                            close()
                        }
                    }
                }

                if (showShadow) {
                    for (i in 1..shadowRadius.roundToPx()) {
                        translate(top = i.toFloat()) {
                            drawPath(
                                path = wavePath,
                                color = shadowColor.copy(alpha = shadowColor.alpha * (1f - i / shadowRadius.toPx())),
                                style = Stroke(width = 1f),
                            )
                        }
                    }
                }

                drawPath(
                    path = fillPath,
                    color = backgroundColor,
                )

                drawPath(
                    path = wavePath,
                    color = waveColor,
                    style = Stroke(width = waveWidth.toPx()),
                )
            }
        }
        Column(
            modifier = Modifier.onSizeChanged {
                with(density) { solidHeight = it.height.toDp() }
            }
        ) {
            Spacer(modifier = Modifier.height(statusBarHeight))
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.heightIn(min = 64.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.heightIn(min = 64.dp),
                ) {
                    if (onBackButtonClicked != null) Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = onBackButtonClicked,
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back_arrow),
                            contentDescription = null,
                            tint = backButtonColor,
                            modifier = Modifier
                                .padding(3.875.dp)
                                .size(16.25.dp)
                        )
                    }
                    if (showLogo) Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(start = 30.dp)
                            .width(34.6.dp)
                            .height(30.dp),
                    ) {
                        if (showLogoWhiteShadow) Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(
                                    radius = 20.dp,
                                    edgeTreatment = BlurredEdgeTreatment.Unbounded,
                                )
                        ) {
                            drawCircle(
                                color = Color.White,
                                radius = 16.dp.toPx(),
                                center = center,
                            )
                        }
                        Image(
                            painter = painterResource(R.drawable.img_header_logo),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = headerTrailingStartPadding)
                    ) {
                        headerTrailing?.invoke(this)
                    }
                }
                centerText?.let {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = centerText,
                            color = centerTextColor,
                            fontWeight = FontWeight.W800,
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                        )
                    }
                }
            }
            content?.invoke(this, waveHeight)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomHeader() {
    ThemeProvider {
        Box(
            modifier = Modifier.height(300.dp)
        ) {
            Image(
                painter = painterResource(R.raw.test_map_image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            CustomHeader(
                showLogo = true,
                showShadow = true,
                backgroundColor = Color.White.copy(alpha = 0.8f),
                headerTrailing = {
                    Text(
                        text = "headerTrailing area",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.Green.copy(alpha = 0.5f)),
                    )
                }
            ) { waveHeight ->
                Column {
                    Text(
                        text = "content area",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.Green.copy(alpha = 0.5f))
                            .height(100.dp),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(waveHeight)
                            .background(color = Color.Yellow.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}