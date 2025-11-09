package com.umc.home.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.umc.home.R
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun TopBar_Default(
    modifier: Modifier = Modifier,
    onHeightChange: (Dp) -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight() // ✅ HomeScreen과 동일하게 wrapContentHeight 사용
            .background(Color.Transparent)
            .zIndex(2f) // 다른 콘텐츠 위에 오도록 zIndex 설정
            .onSizeChanged { size ->
                onHeightChange(with(density) { size.height.toDp() })
            },
        contentAlignment = Alignment.BottomCenter,
    ) {
        // ✅ 배경 이미지 (HomeScreen과 100% 동일한 로직)
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data("android.resource://${context.packageName}/${R.raw.view_top_bar}")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                error = BitmapPainter(
                    BitmapFactory.decodeResource(
                        context.resources, R.raw.view_top_bar_for_preview
                    ).asImageBitmap()
                )
            ),
            contentDescription = "배경 이미지",
            alignment = Alignment.BottomCenter,
            contentScale = ContentScale.Crop, // ✅ HomeScreen과 동일하게 Crop
            modifier = Modifier.matchParentSize(), // ✅ HomeScreen과 동일하게 matchParentSize
        )

        // ✅ 로고 레이아웃 (HomeScreen과 100% 동일한 로직)
        Column(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding() // ✅ 상태바 영역 확보
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 12.dp), // ✅ HomeScreen과 동일한 패딩
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ✅ 로고 (HomeScreen 기본 로고와 동일)
                    Image(
                        painter = painterResource(id = R.drawable.ic_ttatta_logo),
                        contentDescription = "로고",
                        modifier = Modifier
                            .width(34.6.dp) // ✅ HomeScreen과 동일한 크기
                            .height(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}