package com.umc.mypage.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.umc.mypage.R

@Composable
fun TopBarComponent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // 물결 이미지 (배경)
//        Image(
//            painter = painterResource(id = R.drawable.ic_wave_background), // 물결 이미지
//            contentDescription = "배경 물결",
//            contentScale = ContentScale.FillWidth,
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter) // 아래쪽에 위치
//        )

        val context = LocalContext.current

        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data("android.resource://${context.packageName}/${R.raw.view_top_bar}") // ✅ SVG 파일
                    .decoderFactory(SvgDecoder.Factory()) // ✅ SVG 지원
                    .build(),
                error = BitmapPainter( // ✅ 에러 시 사용할 기본 이미지
                    BitmapFactory.decodeResource(
                        context.resources, R.raw.view_top_bar_for_preview
                    ).asImageBitmap()
                )
            ),
            contentDescription = "배경 이미지",
            alignment = Alignment.BottomCenter,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth(), // 필요에 따라 수정
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 왼쪽 로고
                    Image(
                        painter = painterResource(id = R.drawable.ic_ttatta_logo),
                        contentDescription = "로고",
                        modifier = Modifier
                            .width(30.dp)
                            .height(26.dp)
                    )
                }
            }
        }


    }
}
