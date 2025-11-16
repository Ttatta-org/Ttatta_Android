package com.umc.ttatta.app.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.ttatta.app.R

@Composable
fun Splash() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFEEDE)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_gradient_background),
            contentDescription = "배경 이미지",
            contentScale = ContentScale.Fit, // 이미지를 전체 영역에 맞게 확대/축소
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 13.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bigttatta_logo),
                contentDescription = "따따로고",
                modifier = Modifier.fillMaxWidth(0.5f),
                contentScale = ContentScale.None // 이미지를 전체 영역에 맞게 확대/축소
            )
            Spacer(modifier = Modifier.height(13.81.dp))
            Text(
                text = "일상을 기록하다, 나만의 비밀공간",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFFFF8072),
                ),
            )
        }
    }
}

@Preview
@Composable
fun SplashPreview(){
    Splash()
}