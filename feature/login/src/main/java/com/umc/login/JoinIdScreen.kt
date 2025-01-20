package com.umc.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JoinIdScreen() {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        JoinIdTopView(currentStep = 2, totalSteps = 4)
        JoinIdView()


    }
}

@Composable
fun JoinIdTopView(currentStep: Int, totalSteps: Int) {
    val nickname = remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 92.dp)
            .fillMaxWidth()

    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_ttatta_logo),
            modifier = Modifier
                .size(55.36.dp, 48.dp),
            contentDescription = "main_logo_join"
        )
        Spacer(modifier = Modifier.height(35.dp))
        IdProgressBar(currentStep = currentStep, totalSteps = totalSteps)
        Spacer(modifier = Modifier.height(12.dp))

    }
}

@Composable
fun JoinIdView() {
    var idState by remember { mutableStateOf("") }
    var isTextFieldFocused by remember { mutableStateOf(false) }
    var isWarningVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.join_id),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .width(280.dp)
                .padding(bottom = 35.dp),
            color = colorResource(R.color.yellow_200)
        )

        // Input Text Field
        PwInputTextField(
            value = idState,
            onValueChange = {
                idState = it
                isWarningVisible = it.length == 15 // Show warning when 8 characters reached
            },
            placeholder = stringResource(R.string.id_comment),
            onFocusChange = { isTextFieldFocused = it },
            isWarning = isWarningVisible
        )
        Spacer(modifier = Modifier.height(5.dp))

        if (isWarningVisible)
            Text(
                text = stringResource(R.string.join_id_small_comment),
                color = colorResource(R.color.negativeRed),
                fontSize = 12.sp,
            )
        else if (!isWarningVisible)
            Text(
                text = stringResource(R.string.join_id_small_comment),
                color = colorResource(R.color.gray_400),
                fontSize = 12.sp
            )

        // Button
        Button(
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 1.dp, // 기본 그림자
                pressedElevation = 0.dp, // 버튼을 눌렀을 때 그림자
                disabledElevation = 0.dp // enabled가 false일때 그림자
            ),
            onClick = { /* TODO: 로그인 로직 */ },
            modifier = Modifier
                .width(310.dp)
                .padding(top = 139.dp)
                .height(45.dp),
            shape = RoundedCornerShape(24.dp), // 라운딩 처리
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isTextFieldFocused) Color(0xFFFCAD98) else colorResource(R.color.yellow_300)
            )
        ) {
            Text(
                text = stringResource(R.string.next_button),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun IdProgressBar(currentStep: Int, totalSteps: Int) {
    // 전체 진행률을 계산합니다.
    val progress = currentStep.toFloat() / totalSteps

    Box(
        modifier = Modifier
            .width(340.dp)
            .height(5.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFF9681), Color(0xFFFDDDC1)), // 그라데이션 색상
                    startX = 0f,
                    endX = 1000f // 그라데이션이 전체 너비를 덮도록 설정
                ),
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress) // 진행률에 따라 너비 설정
                .height(8.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFA07A), Color(0xFFFFC1A1)), // 채워진 부분 색상
                        startX = 0f,
                        endX = 1000f
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewJoinIdScreen() {
    JoinIdScreen()
}