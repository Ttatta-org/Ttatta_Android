package com.umc.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class PasswordStep {
    INPUT, CONFIRM, MISMATCH, CHANGE_INPUT, CHANGE_CONFIRM, CHANGE_MISMATCH
}

@Composable
fun LockPasswordScreen(
    step: PasswordStep,
    onComplete: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val title = when (step) {
        PasswordStep.INPUT, PasswordStep.CONFIRM -> "암호 입력"
        PasswordStep.CHANGE_INPUT, PasswordStep.CHANGE_CONFIRM -> "암호 변경"
        PasswordStep.MISMATCH, PasswordStep.CHANGE_MISMATCH -> "암호 변경"
    }

    val subtitle = when (step) {
        PasswordStep.INPUT, PasswordStep.CHANGE_INPUT -> "암호를 입력해주세요."
        PasswordStep.CONFIRM, PasswordStep.CHANGE_CONFIRM -> "확인을 위해 한 번 더 입력해 주세요."
        PasswordStep.MISMATCH, PasswordStep.CHANGE_MISMATCH -> "🔴 암호가 일치하지 않아요! 다시 입력해주세요."
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxHeightDp = maxHeight

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeightDp)
                .padding(horizontal = 22.dp),
            //verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "뒤로가기",
                        modifier = Modifier
                            .clickable { /* 뒤로가기 로직 */ }
                            .height(18.dp)
                            .width(14.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 오렌지 400
                    Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.W700, color = Color(0xFFFF9888))
                    Spacer(modifier = Modifier.height(7.dp))

                    // dhfpswl 200
                    Text(text = subtitle, fontSize = 14.sp,fontWeight = FontWeight.W400, color = Color(0xFFFFD0C8))
                    Spacer(modifier = Modifier.height(17.7.dp))

                    PasswordDots(input.length)
                }

                NumberPad(
                    modifier = Modifier
                        .height(maxHeightDp * 0.4f) // 하단 50%를 차지
                        .width(maxHeightDp * 0.8f)
                        .padding(bottom = 20.dp),
                    onDigitClick = {
                        if (input.length < 4) input += it
                        if (input.length == 4) {
                            when (step) {
                                PasswordStep.INPUT, PasswordStep.CHANGE_INPUT -> {
                                    onComplete(input)
                                }
                                PasswordStep.CONFIRM, PasswordStep.CHANGE_CONFIRM -> {
                                    if (input == confirm) {
                                        onComplete(input)
                                    } else {
                                        input = ""
                                    }
                                }
                                else -> {}
                            }
                        }
                    },
                    onBackspace = { if (input.isNotEmpty()) input = input.dropLast(1) },
                    onCancel = { input = "" }
                )
            }

        }
    }
}

@Composable
fun PasswordDots(filledCount: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(4) { index ->
            val imageRes = if (index < filledCount) {
                R.drawable.ic_dot_filled   // ✅ 채워진 이미지 (예: 핑크 곰돌이)
            } else {
                R.drawable.ic_dot_empty    // ✅ 비어있는 이미지 (예: 흰 곰돌이)
            }

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(45.dp)
            )
        }
    }
}


data class KeyButton(
    val label: String,
    val color: Color,
    val fontSize: Int,
    val fontWeight: FontWeight
)

@Composable
fun NumberPad(
    modifier: Modifier = Modifier,
    onDigitClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val keys = listOf(
            listOf(
                KeyButton("1", Color(0xFFFFD0C8), 22, FontWeight.W400),
                KeyButton("2", Color(0xFFFFD0C8), 22, FontWeight.W400),
                KeyButton("3", Color(0xFFFFD0C8), 22, FontWeight.W400)
            ),
            listOf(
                KeyButton("4", Color(0xFFFFD0C8), 22, FontWeight.W400),
                KeyButton("5", Color(0xFFFFD0C8), 22, FontWeight.W400),
                KeyButton("6", Color(0xFFFFD0C8), 22, FontWeight.W400)
            ),
            listOf(
                KeyButton("7", Color(0xFFFFD0C8), 22, FontWeight.W400),
                KeyButton("8", Color(0xFFFFD0C8), 22, FontWeight.W400),
                KeyButton("9", Color(0xFFFFD0C8), 22, FontWeight.W400)
            ),
            listOf(
                KeyButton("취소", Color(0xFFFFD0C8), 16, FontWeight.W400),
                KeyButton("0", Color(0xFFFFD0C8), 22, FontWeight.W400),
                KeyButton("back", Color.Unspecified, 0, FontWeight.Normal) // 이미지 자리
            )
        )


        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { key ->
                        when (key.label) {
                            "취소" -> {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp) // 원하는 크기
                                        .clickable { onCancel() },
                                    contentAlignment = Alignment.Center // ← 중심 정렬
                                ) {
                                    Text(
                                        text = key.label,
                                        fontSize = key.fontSize.sp,
                                        fontWeight = key.fontWeight,
                                        color = key.color
                                    )
                                }
                            }

                            "back" -> {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp) // 원하는 크기
                                        .clickable { onBackspace() },
                                    contentAlignment = Alignment.Center // ← 중심 정렬
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_numberpad_back),
                                        contentDescription = "지우기"
                                    )
                                }

                            }

                            else -> {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp) // 원하는 크기
                                        .clickable { onDigitClick(key.label) },
                                    contentAlignment = Alignment.Center // ← 중심 정렬
                                ) {
                                    Text(
                                        text = key.label,
                                        fontSize = key.fontSize.sp,
                                        fontWeight = key.fontWeight,
                                        color = key.color
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewLockPasswordScreen(){
    LockPasswordScreen(
        step = PasswordStep.INPUT,
        onComplete = {}
    )
}