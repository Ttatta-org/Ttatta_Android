package com.umc.category.component

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.umc.category.R
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider

private const val topBarResourceWidthRatio = 390f
private const val topBarResourceCroppedHeightRatio = 87f

@Composable
fun TopBar(
    topBarTitle: String,
    onHeightChanged: (Dp) -> Unit,
    onBackButtonClicked: () -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()
    var screenWidth by remember { mutableStateOf(0.dp) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { with(density) { screenWidth = it.size.width.toDp() } }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .onGloballyPositioned { with(density) { onHeightChanged(it.size.height.toDp()) } }
        ) {
            Spacer(modifier = Modifier.height(statusBarHeight))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onBackButtonClicked() },
                    ) {
                        Box(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_back_bracket),
                                contentScale = ContentScale.Fit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Text(
                    text = topBarTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    color = LocalColorTheme.current.primary[500],
                )
            }
        }
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest
                    .Builder(context)
                    .data("android.resource://${context.packageName}/${R.raw.img_top_bar_category}")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                // 프리뷰를 위한 이미지
                error = BitmapPainter(
                    image = BitmapFactory
                        .decodeResource(context.resources, R.raw.img_top_bar_category_for_preview)
                        .asImageBitmap(),
                )
            ),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
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
    ThemeProvider {
        TopBar(
            topBarTitle = "발자국 새로 만들기",
            onHeightChanged = {},
            onBackButtonClicked = {},
        )
    }
}