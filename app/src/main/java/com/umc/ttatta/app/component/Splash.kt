package com.umc.ttatta.app.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.theme.ThemeProvider
import com.umc.design.theme.font.Pretendard
import com.umc.ttatta.app.R

@Composable
fun Splash() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFEEDE)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize(),
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFC7A5),
                        Color(0xFFFFEEDE),
                    ),
                    radius = 422.dp.toPx(),
                ),
                center = center,
                radius = 422.dp.toPx(),
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                fontFamily = Pretendard.font,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFFFF8072),
            )
        }
    }
}

@Preview
@Composable
fun SplashPreview(){
    ThemeProvider {
        Splash()
    }
}