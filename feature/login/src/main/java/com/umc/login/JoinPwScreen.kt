package com.umc.login

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
fun JoinPwScreen(navController: NavHostController) {
    Column (
        //horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        PwBackButton(navController)
    }
}

@Composable
fun PwBackButton(navController: NavHostController) {
    Box (
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 50.dp, start = 30.dp)
    ){
        Image(
            painter = painterResource(R.drawable.ic_back),
            modifier = Modifier
                .size(15.dp, 21.dp)
                .clickable { navController.navigate("join_id") },
            contentDescription = "back_button",

            )
    }
}



@Composable
fun JoinPwView(onNext: () -> Unit, onBack: () -> Unit) {
    var pwState by remember { mutableStateOf("") }
    var isTextFieldFocused by remember { mutableStateOf(false) }
    var isWarningVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.join_pw),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(280.dp)
                .padding(bottom = 35.dp),
            color = colorResource(R.color.yellow_200)
        )

        // Input Text Field
        PwInputTextField(
            value = pwState,
            onValueChange = {
                pwState = it
                isWarningVisible = it.length > 8 // Show warning when 8 characters reached
            },
            placeholder = stringResource(R.string.join_pw_comment),
            onFocusChange = { isTextFieldFocused = it },
            isWarning = isWarningVisible
        )
        Spacer(modifier = Modifier.height(5.dp))

        if (isWarningVisible)
            Text(
                text = stringResource(R.string.join_pw_small_comment),
                color = colorResource(R.color.negativeRed),
                fontSize = 12.sp,
            )
        else if (!isWarningVisible)
            Text(
                text = stringResource(R.string.join_pw_small_comment),
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
            onClick = onNext,
            modifier = Modifier
                .width(310.dp)
                .padding(top = 139.dp)
                .height(45.dp),
            shape = RoundedCornerShape(24.dp), // 라운딩 처리
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isTextFieldFocused) colorResource(R.color.orange_200) else colorResource(R.color.yellow_300)
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
fun PwInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onFocusChange: (Boolean) -> Unit,
    isWarning: Boolean
) {
    TextField(
        value = value,
        onValueChange = {
            if (it.length < 8) { // 8글자 제한
                onValueChange(it)
            }
        },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = if (isWarning) colorResource(R.color.negativeRed) else Color.Black
        ),
        placeholder = {
            Box (
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
}


@Preview(showBackground = true)
@Composable
fun PreviewJoinPwScreen() {
    JoinPwScreen(navController = NavHostController(LocalContext.current))
}