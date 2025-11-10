package com.umc.mypage.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.umc.mypage.R
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TopBar_Mypage_Default(
    modifier: Modifier = Modifier,
    onHeightChange: (Dp) -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 12.dp)
                        .height(IntrinsicSize.Min), // 자식들의 높이를 감싸도록 설정
                    contentAlignment = Alignment.Center // 자식들을 수직 중앙 정렬
                ) {
                    // ✅ 2. 로고를 왼쪽에 정렬
                    Image(
                        painter = painterResource(id = R.drawable.ic_ttatta_logo),
                        contentDescription = "로고",
                        modifier = Modifier
                            .width(34.6.dp)
                            .height(30.dp)
                            .align(Alignment.CenterStart) // 왼쪽 정렬
                    )

                    // ✅ 3. "마이페이지" 텍스트를 중앙에 추가
                    Text(
                        text = "마이페이지",
                        style = TextStyle( // 🎨 스크린샷과 유사한 스타일
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W800,
                            color = Color(0xFFFF8072) // (디자인에 맞게 색상 변경)
                        ),
                        modifier = Modifier.align(Alignment.Center) // 중앙 정렬
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}