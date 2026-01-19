package com.umc.challenge.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import kotlin.math.min

@Composable
fun ChatBubble(
    text: String,
    left: Boolean = false,
    fontSize: TextUnit = 16.sp,
    lineHeight: TextUnit = (fontSize.value + 6).sp,
    matchHeightConstraintsFirst: Boolean = false,
) {
    val density = LocalDensity.current

    val path = remember {
        val pathString =
            "M307 68.8889C307 30.8426 276.17 0 238.14 0H60.6266C27.1434 0 0 27.1549 0 60.6522C0 94.1494 27.1434 121.304 60.6266 121.304L225.151 121.304C244.126 121.304 249.696 147.188 232.402 155H238.14C276.17 155 307 124.157 307 86.1111V68.8889Z"

        PathParser()
            .parsePathString(pathString)
            .toPath()
    }

    var size by remember { mutableStateOf(Size.Zero) }

    val (originalWidth, originalHeight, originalBottomPadding) = remember {
        listOf(
            307.dp,
            155.dp,
            34.dp,
        )
    }

    val (width, height, bottomPadding) = remember(
        size,
        originalWidth,
        originalHeight,
        originalBottomPadding,
    ) {
        val scaleFactor = with(density) {
            min(
                size.width / originalWidth.toPx(),
                size.height / originalHeight.toPx(),
            )
        }

        listOf(
            originalWidth,
            originalHeight,
            originalBottomPadding,
        ).map { it * scaleFactor }
    }

    Box(
        modifier = Modifier
            .aspectRatio(
                ratio = originalWidth.value / originalHeight.value,
                matchHeightConstraintsFirst = matchHeightConstraintsFirst,
            )
            .fillMaxSize()
            .onSizeChanged { size = it.toSize() }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .let { if (left) it.graphicsLayer { rotationY = 180f } else it },
        ) {
            scale(
                scaleX = width.toPx() / originalWidth.value,
                scaleY = height.toPx() / originalHeight.value,
                pivot = Offset.Zero,
            ) {
                drawPath(
                    path = path,
                    style = Fill,
                    color = Color.White,
                )
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomPadding),
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.W700,
                lineHeight = lineHeight,
                letterSpacing = (-0.4).sp,
                color = LocalColorTheme.current.grey[700],
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
fun PreviewChatBubble() {
    ThemeProvider {
        ChatBubble(text = "안녕하세요!", left = true)
    }
}