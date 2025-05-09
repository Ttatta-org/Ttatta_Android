package com.umc.mypage.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.umc.mypage.R

@Composable
fun Dialog(
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    // 블러 + 반투명 배경
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 블러 배경
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x4D000000)) // #0000004D = alpha 0.3
                .blur(12.dp)
        )

        // 팝업 카드
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f) // 화면 너비의 90%까지만 사용
                .widthIn(min = 280.dp, max = 400.dp) // 최소 ~ 최대 너비 제한
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = Color(0xDE806E38)
                )
                .background(Color.White, shape = RoundedCornerShape(28.dp))
                .padding(vertical = 22.dp, horizontal = 27.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 상단 로고 또는 장식
            Image(
                painter = painterResource(id = R.drawable.ic_point),
                contentDescription = "point",
                modifier = Modifier
                    .width(39.18.dp)
                    .height(16.dp)
            )

            Spacer(modifier = Modifier.height(46.dp))

            Text(
                text = message,
                fontSize = 16.sp,
                color = Color(0xFF4B4B4B),
                fontWeight = FontWeight.W700
            )

            Spacer(modifier = Modifier.height(44.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 취소 버튼 (오렌지 200)
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Color(0xFFFFD0C8)),
                    contentPadding = PaddingValues(vertical = 13.dp),
                ) {
                    Text(text = "취소", fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.W600, color = Color(0xFFFFD0C8))
                }

                // 확인 버튼 (오랜지 400)
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9888)),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 13.dp)
                ) {
                    Text(text = "확인", fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.W600, color = Color.White)
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true, backgroundColor = 0xFFEFEFEF)
fun DialogPreview() {
    MaterialTheme {
        Dialog(
            message = "정말로 로그아웃 하시겠습니까?",
            onDismiss = {},
            onConfirm = {}
        )
    }
}