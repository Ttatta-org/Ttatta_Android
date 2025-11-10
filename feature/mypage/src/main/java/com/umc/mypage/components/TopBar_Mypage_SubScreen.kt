package com.umc.mypage.components // (파일 위치에 따라 패키지명 조절)

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.umc.mypage.R // (R 파일 경로는 프로젝트에 맞게 확인)

/**
 * 마이페이지 하위 화면들(알림, 잠금, 탈퇴)에서 공통으로 사용하는 TopBar
 * (뒤로가기 버튼 + 중앙 타이틀)
 */
@Composable
fun TopBar_SubScreen(
    title: String, // ✅ 화면 제목을 파라미터로 받음
    onBackClick: () -> Unit, // ✅ 뒤로가기 클릭 이벤트
    onHeightChange: (Dp) -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Transparent)
            .zIndex(2f)
            .onSizeChanged { size ->
                onHeightChange(with(density) { size.height.toDp() })
            },
        contentAlignment = Alignment.BottomCenter,
    ) {
        // 배경 이미지 (동일한 웨이브 배경)
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data("android.resource://${context.packageName}/${R.raw.view_top_bar_color}")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                error = BitmapPainter(
                    BitmapFactory.decodeResource(
                        context.resources, R.raw.view_top_bar_color_for_preview
                    ).asImageBitmap()
                )
            ),
            contentDescription = "배경 이미지",
            alignment = Alignment.BottomCenter,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )

        // 내용 (뒤로가기 버튼 + 타이틀)
        Column(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 12.dp)
                        .defaultMinSize(minHeight = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // ✅ 1. 왼쪽: 뒤로가기 버튼
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_left_new), // (ic_arrow_left_new 리소스 필요)
                        contentDescription = "뒤로 가기",
                        modifier = Modifier
                            .width(10.dp)
                            .height(16.25.dp)
                            .align(Alignment.CenterStart)
                            .clickable(
                                indication = null, // 리플 효과 제거
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onBackClick() } // ✅ 클릭 이벤트 연결
                    )

                    // ✅ 2. 중앙: 화면 타이틀 (파라미터로 받음)
                    Text(
                        text = title, // ✅ 파라미터 사용
                        style = TextStyle(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.W700,
                            color = Color(0xFFF07B7B) // (스크린샷 색상)
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}