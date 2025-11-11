package com.umc.challenge.component

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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.umc.challenge.R
import com.umc.design.theme.LocalColorTheme

private const val topBarResourceWidthRatio = 390f
private const val topBarResourceCroppedHeightRatio = 87f

data class PastChallengeTopBarProp(
    val onHeightChanged: (Dp) -> Unit,
    val onBackIconClicked: () -> Unit,
)

@Composable
fun PastChallengeTopBar(
    prop: PastChallengeTopBarProp
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                // Back Icon
                Box(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = prop.onBackIconClicked
                        )
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = null,
                        modifier = Modifier
                            .width(10.dp)
                    )
                }

                Text(
                    text = "지난 챌린지",
                    color = LocalColorTheme.current.primary[500],
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 40.dp)
                )
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

val previewPastChallengeTopBarProp = PastChallengeTopBarProp(
    onHeightChanged = {},
    onBackIconClicked = {}
)


@Preview
@Composable
fun PreviewPastChallengeTopBar() {
    PastChallengeTopBar(prop = previewPastChallengeTopBarProp)
}