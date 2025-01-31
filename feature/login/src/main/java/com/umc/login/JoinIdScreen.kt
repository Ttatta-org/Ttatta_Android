package com.umc.login

import android.graphics.Paint.Align
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun JoinIdScreen(navController: NavHostController) {
    Column (
        //horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        JoinIdView(
            onNext = { navController.navigate("join/pw") },
            onBack = { navController.navigate("join") }
        )

    }
}

@Composable
fun IdBackButton(navController: NavHostController) {
    Box (
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 50.dp, start = 30.dp)
    ){
        Image(
            painter = painterResource(R.drawable.ic_back),
            modifier = Modifier
                .size(15.dp, 21.dp)
                .clickable { navController.navigate("join") },
            contentDescription = "back_button",

            )
    }
}


@Composable
fun JoinIdView(onNext: () -> Unit, onBack: () -> Unit) {
    var idState by remember { mutableStateOf("") }
    var isTextFieldFocused by remember { mutableStateOf(false) }
    var isWarningVisible by remember { mutableStateOf(false) }

    // [규칙 요약]
    // A. 아무것도 입력하지 않은 경우 (idState.isEmpty()):
    //     - 버튼 배경색: #FDDDC1
    //     - 버튼 비활성화 (enabled=false)
    // B. 닉네임 칸에 한 글자 이상 입력된 경우 (idState.isNotEmpty()):
    //     - 버튼 배경색: #FCAD98
    // C. 1글자 이상 && 8글자 이하인 경우에만 다음 화면 이동 가능

    // 버튼 활성/비활성 조건
    val isButtonEnabled = idState.isNotEmpty() && idState.length <= 15

    // 버튼 색상 조건
    val buttonColor = if (idState.isNotEmpty()) {
        Color(0xFFFCAD98) // FCAD98
    } else {
        Color(0xFFFDDDC1) // FDDDC1
    }

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

        // TextField
        IdInputTextField(
            value = idState,
            onValueChange = { newText ->
                if (newText.length < 15) {
                    idState = newText
                    isWarningVisible = (newText.length == 15)
                }
            },
            placeholder = stringResource(R.string.id_comment),
            onFocusChange = { isTextFieldFocused = it },
            isWarning = isWarningVisible
        )

        Spacer(modifier = Modifier.height(5.dp))

        // 기존 경고 표시 로직
        if (isWarningVisible)
            Text(
                text = stringResource(R.string.join_id_small_comment),
                color = colorResource(R.color.negativeRed),
                fontSize = 12.sp,
            )
        else
            Text(
                text = stringResource(R.string.join_id_small_comment),
                color = colorResource(R.color.gray_400),
                fontSize = 12.sp
            )

        // Button
        Button(
            // [중요] 버튼 활성/비활성
            enabled = isButtonEnabled,
            onClick = {
                // 조건 만족 시만 다음 화면 이동
                if (isButtonEnabled) {
                    onNext()
                }
            },
            modifier = Modifier
                .width(310.dp)
                .padding(top = 139.dp)
                .height(45.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 1.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
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
fun IdInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onFocusChange: (Boolean) -> Unit,
    isWarning: Boolean
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = value,
            onValueChange = {
                // 글자 수 제한 로직
                if (it.length < 15) {
                    onValueChange(it)
                }
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,    // 가운데 정렬
                fontSize = 14.sp,
                color = if (isWarning) colorResource(R.color.negativeRed) else Color.Black
            ),
            // 기본 placeholder 대신, 아래 Box 를 통해 직접 표시하므로 비워 둡니다.
            placeholder = { /* No-op */ },

            // trailingIcon을 이용해 "중복확인" 버튼을 텍스트필드 안 오른쪽에 추가
            trailingIcon = {
                Text(
                    text = "중복확인",
                    color = colorResource(R.color.gray_500),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clickable {
                            // TODO: 여기에 '중복확인' 로직을 넣으세요.
                        }
                        .padding(end = 6.dp)
                )
            },
            keyboardOptions = KeyboardOptions.Default,
            modifier = Modifier
                .width(310.dp)
                .height(52.dp)
                .onFocusChanged { onFocusChange(it.isFocused) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colorResource(R.color.gray_500),
                unfocusedIndicatorColor = colorResource(R.color.gray_500),
                cursorColor = if (isWarning) colorResource(R.color.negativeRed) else Color.Black
            )
        )

        // 값이 비었을 때만(== 실제 입력이 없을 때만) 직접 플레이스홀더를 가운데 표시
        if (value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.gray_500)
                )
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewJoinIdScreen() {
    JoinIdScreen(navController = NavHostController(LocalContext.current))
}