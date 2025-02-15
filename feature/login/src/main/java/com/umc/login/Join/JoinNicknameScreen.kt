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
import androidx.compose.material3.CircularProgressIndicator
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
import com.umc.login.LoginViewModel
import com.umc.login.R
import com.umc.login.component.NicknameInputTextField


@Composable
fun JoinScreen(navController: NavHostController, viewModel: JoinViewModel = viewModel()) {

    Column(modifier = Modifier.wrapContentSize()) {
        JoinNicknameView(viewModel = viewModel, onNext = { navController.navigate("join_id") })
    }
}

@Composable
fun JoinNicknameView(viewModel: JoinViewModel, onNext: () -> Unit) {
    val nickNameState by viewModel.nickNameState.collectAsState()
    val isWarningVisible by viewModel.isWarningVisible.collectAsState()
    val isButtonEnabled by viewModel.isNicknameButtonEnabled.collectAsState()
    val nicknameError by viewModel.nicknameError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.join_nickname),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(600),
            color = colorResource(R.color.yellow_200)
        )
        Spacer(modifier = Modifier.height(35.dp))

        NicknameInputTextField(
            value = nickNameState,
            onValueChange = viewModel::onNickNameChange,
            onImeAction = {
                keyboardController?.hide()
                viewModel.checkNicknameAvailability()
            },
            placeholder = stringResource(R.string.nickname_comment),
            isWarning = isWarningVisible,
            errorMessage = nicknameError,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = stringResource(R.string.nickname_warning),
            color = if (isWarningVisible) colorResource(R.color.negativeRed) else colorResource(R.color.gray_400),
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(400)
        )
        Spacer(modifier = Modifier.height(101.dp))

        Button(
            enabled = isButtonEnabled,
            onClick = { if (isButtonEnabled) onNext() },
            modifier = Modifier
                .width(310.dp)
                .height(45.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isButtonEnabled) colorResource(R.color.orange_200) else colorResource(R.color.yellow_300),
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
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewJoinScreen() {
    JoinScreen(navController = NavHostController(LocalContext.current))
}
