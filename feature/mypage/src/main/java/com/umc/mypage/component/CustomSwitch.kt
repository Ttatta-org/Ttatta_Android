package com.umc.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    // 전체 Switch 박스
    Box(
        modifier = Modifier
            .width(46.dp) // Switch 전체 너비
            .height(26.dp) // Switch 전체 높이
            .graphicsLayer {
                shadowElevation = 2.dp.toPx() // ✅ 박스용 shadow
                shape = RoundedCornerShape(12.dp)
                clip = false
            }
            .clip(
                androidx.compose.foundation.shape.RoundedCornerShape(12.dp) // 커스텀 Border-Radius
            )
            .background(
                // 오렌지 400
                if (checked) Color(0xFFFF9888) else Color(0xFFE1E1E1) // 상태에 따른 배경색
            )
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        // Thumb (Circle)
        Box(
            modifier = Modifier
                .size(21.9.dp) // Thumb 크기
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart) // 상태에 따른 위치
                .graphicsLayer {
                    shadowElevation = 2.dp.toPx() // ✅ Thumb용 shadow
                    shape = CircleShape
                    clip = false
                }
                .clip(CircleShape) // 원형
                .background(Color(0xFFF5F5F5)) // Thumb 배경색
        )
    }
}

@Preview
@Composable
private fun PreviewCustomSwitchOn() {
    CustomSwitch(
        checked = true,
        onCheckedChange = {},
    )
}

@Preview
@Composable
private fun PreviewCustomSwitchOff() {
    CustomSwitch(
        checked = false,
        onCheckedChange = {},
    )
}