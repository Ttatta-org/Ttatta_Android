package com.umc.challenge.component

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.umc.challenge.R

private const val topBarResourceWidthRatio = 390f
private const val topBarResourceCroppedHeightRatio = 87f

data class ChallengeTopBarProp(
    val point: Int,
    val onHeightChanged: (Dp) -> Unit,
    val onShopIconClicked: () -> Unit,
    val onMyItemsIconClicked: () -> Unit,
)

@Composable
fun ChallengeTopBar(
    prop: ChallengeTopBarProp,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
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
                .onGloballyPositioned {
                    with(density) { prop.onHeightChanged(it.size.height.toDp()) }
                }
        ) {
            Spacer(modifier = Modifier.height(statusBarHeight))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Box(
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = prop.onShopIconClicked
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_shop),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Box(
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = prop.onMyItemsIconClicked
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_my_items),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    PointChip(point = prop.point)
                }
            }
        }
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data("android.resource://${context.packageName}/${R.raw.img_top_bar_challenge}")
                    .decoderFactory(SvgDecoder.Factory()).build(),
                // 프리뷰를 위한 이미지
                error = BitmapPainter(
                    image = BitmapFactory.decodeResource(
                        context.resources, R.raw.img_top_bar_challenge_for_preview
                    ).asImageBitmap(),
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

val previewChallengeTopBarProp = ChallengeTopBarProp(
    point = 1300,
    onHeightChanged = {},
    onShopIconClicked = {},
    onMyItemsIconClicked = {}
)

@Preview
@Composable
fun PreviewTopBar() {
    ChallengeTopBar(prop = previewChallengeTopBarProp)
}