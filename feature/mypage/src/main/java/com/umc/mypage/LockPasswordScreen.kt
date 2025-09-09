package com.umc.mypage

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PasswordStep {
    SET_INPUT, SET_CONFIRM, SET_MISMATCH,
    CHANGE_INPUT, CHANGE_CONFIRM, CHANGE_MISMATCH,
    CHECK_INPUT, CHECK_MISMATCH,
}
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LockPasswordScreen(
    isChangingPassword: Boolean = false,
    isCheckingMode: Boolean = false,
    onComplete: suspend (String) -> Unit
) {
    var step by remember {
        mutableStateOf(
            if (isChangingPassword)
                PasswordStep.CHANGE_INPUT
            else if (isCheckingMode)
                PasswordStep.CHECK_INPUT
            else
                PasswordStep.SET_INPUT
        )
    }
    var input by remember { mutableStateOf("") }
    var firstInput by remember { mutableStateOf("") }

    val title = when (step) {
        PasswordStep.SET_INPUT, PasswordStep.SET_CONFIRM, PasswordStep.CHECK_INPUT -> "암호 입력"
        PasswordStep.SET_MISMATCH, PasswordStep.CHECK_MISMATCH -> "암호 입력"
        PasswordStep.CHANGE_INPUT, PasswordStep.CHANGE_CONFIRM -> "암호 변경"
        PasswordStep.CHANGE_MISMATCH -> "암호 변경"
    }

    val subtitle = when (step) {
        PasswordStep.SET_INPUT, PasswordStep.CHANGE_INPUT, PasswordStep.CHECK_INPUT, PasswordStep.CHECK_MISMATCH -> "암호를 입력해주세요."
        else -> "확인을 위해 한 번 더 입력해 주세요."
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxHeightDp = maxHeight

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeightDp)
                .padding(horizontal = 19.5.dp),
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
                            .height(16.dp)
                            .width(10.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 오렌지 400
                    Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.W700, color = Color(0xFFFF9888))
                    Spacer(modifier = Modifier.height(7.dp))

                    // 오렌지 200
                    Text(text = subtitle, fontSize = 14.sp,fontWeight = FontWeight.W400, color = Color(0xFFFFD0C8))
                    Spacer(modifier = Modifier.height(17.7.dp))

                    PasswordDots(input.length)

                    if (step == PasswordStep.SET_MISMATCH || step == PasswordStep.CHANGE_MISMATCH || step == PasswordStep.CHECK_MISMATCH) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_warning_red),
                                contentDescription = "경고",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                            )
                            Text(
                                text = "암호가 일치하지 않아요! 다시 입력해주세요.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W400,
                                color = Color(0xFFFF6060)
                            )
                        }
                    }
                }

                NumberPad(
                    modifier = Modifier
                        .height(maxHeightDp * 0.4f) // 하단 50%를 차지
                        .width(maxHeightDp * 0.8f)
                        .padding(bottom = 45.dp),
                    onDigitClick = {
                        if (input.length < 4) input += it
                        if (input.length == 4) {
                            when (step) {
                                PasswordStep.SET_INPUT, PasswordStep.CHANGE_INPUT -> {
                                    firstInput = input
                                    input = ""
                                    step = when (step) {
                                        PasswordStep.SET_INPUT -> PasswordStep.SET_CONFIRM
                                        else -> PasswordStep.CHANGE_CONFIRM
                                    }
                                }

                                PasswordStep.SET_CONFIRM, PasswordStep.SET_MISMATCH -> {
                                    if (input == firstInput) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            onComplete(input)
                                        }
                                    } else {
                                        input = ""
                                        step = PasswordStep.SET_MISMATCH
                                    }
                                }

                                PasswordStep.CHANGE_CONFIRM, PasswordStep.CHANGE_MISMATCH -> {
                                    if (input == firstInput) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            onComplete(input)
                                        }
                                    } else {
                                        input = ""
                                        step = PasswordStep.CHANGE_MISMATCH
                                    }
                                }

                                PasswordStep.CHECK_INPUT, PasswordStep.CHECK_MISMATCH -> {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        onComplete(input)
                                        delay(100L)  // UI 단차 해소를 위한 딜레이
                                        input = ""
                                        step = PasswordStep.CHECK_MISMATCH
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
fun Subtitle(step: PasswordStep) {
    when (step) {
        PasswordStep.SET_MISMATCH, PasswordStep.CHANGE_MISMATCH -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_warning_red), // ⚠️ 원하는 이미지로 바꾸세요
                    contentDescription = "경고",
                    modifier = Modifier
                        .size(11.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = "암호가 일치하지 않아요! 다시 입력해주세요.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    color = Color.Red
                )
            }
        }

        PasswordStep.SET_CONFIRM, PasswordStep.CHANGE_CONFIRM -> {
            Text(
                text = "확인을 위해 한 번 더 입력해 주세요.",
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFFFFD0C8)
            )
        }

        else -> {
            Text(
                text = "암호를 입력해주세요.",
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFFFFD0C8)
            )
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
//@Preview(showBackground = true)
//@Composable
//fun PreviewLockPasswordScreen(){
//    LockPasswordScreen(
//        step = PasswordStep.INPUT,
//        onComplete = {}
//    )
//}