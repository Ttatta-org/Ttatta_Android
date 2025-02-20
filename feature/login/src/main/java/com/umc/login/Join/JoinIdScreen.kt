package com.umc.login.Join

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.umc.login.R
import com.umc.login.component.IdInputTextField

@Composable
fun JoinIdScreen(navController: NavHostController, viewModel: JoinViewModel = viewModel()) {
    Column(modifier = Modifier.wrapContentSize()) {
        JoinIdView(
            viewModel = viewModel,
            onNext = { navController.navigate("join_pw") }
        )
    }
}

@Composable
fun JoinIdView(viewModel: JoinViewModel, onNext: () -> Unit) {
    val idState by viewModel.idState.collectAsState()
    val isWarningVisible by viewModel.isWarningVisible.collectAsState()
    val isButtonEnabled by viewModel.isIdButtonEnabled.collectAsState()
    val idError by viewModel.idError.collectAsState()
    val idSuccessMessage by viewModel.idSuccessMessage.collectAsState() // ✅ 성공 메시지 상태
    val isLoading by viewModel.isLoading.collectAsState()
    val isCheckButtonVisible by viewModel.isCheckButtonVisible.collectAsState() // ✅ 버튼 가시성 상태


    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.join_id),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(600),
            color = colorResource(R.color.yellow_200)
        )
        Spacer(modifier = Modifier.height(35.dp))

        IdInputTextField(
            value = idState,
            onValueChange = viewModel::onIdChange,
            onImeAction = {
                keyboardController?.hide()
                viewModel.checkIdAvailability(

                )
            },
            placeholder = stringResource(R.string.join_id_comment),
            isWarning = isWarningVisible,
            errorMessage = idError,
            isLoading = isLoading,
            isCheckButtonVisible = isCheckButtonVisible
        )

        //✅ 중복 확인 성공 메시지 표시
        if (idSuccessMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = idSuccessMessage!!,
                color = colorResource(R.color.positiveGreen), // ✅ 성공 메시지 색상 변경
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(400)
            )
        }
        // ✅ 중복 확인 실패 시 기존 경고 메시지 유지
        else if (idError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = idError!!,
                color = colorResource(R.color.negativeRed),
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(400)
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.join_id_small_comment),
                color = if (isWarningVisible) colorResource(R.color.negativeRed) else colorResource(R.color.gray_400),
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(400)
            )
        }




        Spacer(modifier = Modifier.height(101.dp))

        Button(
            enabled = isButtonEnabled,
            onClick = { if (isButtonEnabled) { onNext() } },
            modifier = Modifier
                .width(310.dp)
                .height(45.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.orange_200),
                disabledContainerColor = colorResource(R.color.yellow_300)
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
                lineHeight = 20.sp,
                fontWeight = FontWeight(600),
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewJoinIdScreen() {
    JoinIdScreen(navController = NavHostController(LocalContext.current))
}
