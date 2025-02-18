package com.umc.challenge.component

import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.umc.design.Primary200
import com.umc.design.Secondary300

data class TitledTopBarProp(
    val mode: TitledTopBarMode,
    val onHeightChanged: (Dp) -> Unit
)

private const val topBarResourceWidthRatio = 390f
private const val topBarResourceCroppedHeightRatio = 87f

enum class TitledTopBarMode(
    val backgroundColor: Color,
    @RawRes val backgroundImage: Int,
    @RawRes val backgroundPreviewImage: Int,
    @DrawableRes val title: Int,
) {
    SHOP(
        backgroundColor = Color.Primary200,
        backgroundImage = R.raw.img_top_bar_shop,
        backgroundPreviewImage = R.raw.img_top_bar_shop_for_preview,
        title = R.drawable.text_shop
    ),
    MY_ITEM(
        backgroundColor = Color.Secondary300,
        backgroundImage = R.raw.img_top_bar_my_item,
        backgroundPreviewImage = R.raw.img_top_bar_my_item_for_preview,
        title = R.drawable.text_my_item
    )
}

@Composable
fun TitledTopBar(
    prop: TitledTopBarProp
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
                .background(color = prop.mode.backgroundColor)
                .onGloballyPositioned { with(density) { prop.onHeightChanged(it.size.height.toDp()) } }
        ) {
            Spacer(modifier = Modifier.height(statusBarHeight))
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Image(
                    painter = painterResource(id = prop.mode.title),
                    contentDescription = null,
                )
            }
        }
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data("android.resource://${context.packageName}/${prop.mode.backgroundImage}")
                    .decoderFactory(SvgDecoder.Factory()).build(),
                // 프리뷰를 위한 이미지
                error = BitmapPainter(
                    image = BitmapFactory.decodeResource(
                        context.resources, prop.mode.backgroundPreviewImage
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

@Preview
@Composable
fun PreviewShopTitledTopBar() {
    TitledTopBar(
        prop = TitledTopBarProp(
            mode = TitledTopBarMode.SHOP,
            onHeightChanged = {}
        )
    )
}

@Preview
@Composable
fun PreviewMyItemTitledTopBar() {
    TitledTopBar(
        prop = TitledTopBarProp(
            mode = TitledTopBarMode.MY_ITEM,
            onHeightChanged = {}
        )
    )
}