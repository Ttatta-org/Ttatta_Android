package com.umc.mypage.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.umc.mypage.R

@Composable
fun TopBarComponent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // 원하는 높이 설정
    ) {
        // 물결 이미지 (배경)
        Image(
            painter = painterResource(id = R.drawable.ic_wave_background), // 물결 이미지
            contentDescription = "배경 물결",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // 아래쪽에 위치
        )

        // 로고 이미지
        Image(
            painter = painterResource(id = R.drawable.ic_logo), // 로고 이미지
            contentDescription = "로고",
            modifier = Modifier
                .size(width = 30.dp, height = 26.01.dp)
                .absoluteOffset(x = 32.dp, y = 49.dp)
        )
    }
}
