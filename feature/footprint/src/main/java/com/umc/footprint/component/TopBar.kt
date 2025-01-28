package com.umc.footprint.component

import android.graphics.BitmapFactory
import androidx.compose.animation.core.animate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.umc.footprint.R

private const val topBarResourceWidthRatio = 390f
private const val topBarResourceCroppedHeightRatio = 87f

@Composable
fun TopBar(
    showBackground: Boolean
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var screenWidth by remember { mutableStateOf(0.dp) }
    var backgroundOpacity by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = showBackground) {
        animate(
            initialValue = backgroundOpacity,
            targetValue = if (showBackground) 1f else 0f,
            block = { value, _ -> backgroundOpacity = value }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { with(density) { screenWidth = it.size.width.toDp() } }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White.copy(alpha = backgroundOpacity))
        ) {
            Spacer(modifier = Modifier.height(statusBarHeight))
            Box(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data("android.resource://${context.packageName}/${R.raw.view_top_bar}")
                    .decoderFactory(SvgDecoder.Factory()).build(),
                // 프리뷰를 위한 이미지
                error = BitmapPainter(
                    image = BitmapFactory.decodeResource(
                        context.resources, R.raw.view_top_bar_for_preview
                    ).asImageBitmap(),
                )
            ),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
                .alpha(backgroundOpacity)
                .offset {
                    IntOffset(
                        x = 0,
                        y = -screenWidth
                            .times(topBarResourceCroppedHeightRatio)
                            .div(topBarResourceWidthRatio)
                            .roundToPx(),
                    )
                }
        )
    }
}

@Preview
@Composable
fun PreviewTopBar() {
    TopBar(
        showBackground = true
    )
}