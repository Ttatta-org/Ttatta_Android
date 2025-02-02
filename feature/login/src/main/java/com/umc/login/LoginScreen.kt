package com.umc.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlin.math.log

@Composable
fun LoginScreen(navController: NavHostController) {
    // TODO: 화면 구현
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        LoginTopView()
        LoginMiddleView()
        LoginLinkText(navController)
        LoginOrDivider()
        KakaoLoginButton()

    }
}

@Composable
fun LoginTopView() {
    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 158.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_ttatta_logo),
            modifier = Modifier.size(55.36.dp, 48.dp),
            contentDescription = "ttatta_login_logo"
        )
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.login_comment),
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            color = colorResource(R.color.yellow_200)

        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginMiddleView(loginViewModel: LoginViewModel = viewModel()) {
    val idState by loginViewModel.idState
    val pwState by loginViewModel.pwState
    var passwordVisible by loginViewModel.passwordVisible
    var idTextFieldFocused by remember { mutableStateOf(false) }
    var pwTextFieldFocused by remember { mutableStateOf(false) }
    val isButtonActive = loginViewModel.isButtonActive()
        Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 75.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 아이디 입력 필드
        InputTextField(
            value = idState,
            onValueChange = { loginViewModel.onIdChange(it)},
            placeholder = "아이디 입력",
            isPassword = false,
            onFocusChange = { idTextFieldFocused = it }
        )
        Spacer(modifier = Modifier.height(6.dp))

        // 비밀번호 입력 필드
        InputTextField(
            value = pwState,
            onValueChange = { loginViewModel.onPwChange(it) },
            placeholder = "비밀번호 입력",
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordToggleClick = { passwordVisible = !passwordVisible },
            onFocusChange = { pwTextFieldFocused = it }
        )
        Button(
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 1.dp, // 기본 그림자
                pressedElevation = 0.dp, // 버튼을 눌렀을 때 그림자
                disabledElevation = 0.dp // enabled가 false일때 그림자
            ),
            onClick = { /* TODO: 로그인 로직 */ },
            modifier = Modifier
                .width(310.dp)
                .padding(top = 30.dp)
                .height(45.dp),
            shape = RoundedCornerShape(24.dp), // 라운딩 처리
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isButtonActive) colorResource(R.color.orange_200) else colorResource(R.color.yellow_300)
            )
        ) {
            Text(
                text = stringResource(R.string.login_button),
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(600),
                color = Color.White
            )
        }
    }
}


@Composable
fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean,
    passwordVisible: Boolean = false,
    onPasswordToggleClick: (() -> Unit)? = null,
    onFocusChange: (Boolean) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            ),
            placeholder = {
            },
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { onPasswordToggleClick?.invoke() }) {
                        Image(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                            ),
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                }
            } else null,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
            modifier = Modifier
                .width(310.dp)
                .height(52.dp)
                .onFocusChanged { onFocusChange(it.isFocused) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colorResource(R.color.gray_500),
                unfocusedIndicatorColor = colorResource(R.color.gray_500),
                cursorColor = Color.Black
            )
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = placeholder,
                fontSize = 14.sp,
                fontWeight = FontWeight(600),
                color = colorResource(R.color.gray_500)
            )
        }
    }
}

@Composable
fun LoginButton() {
    var isButtonActive by remember { mutableStateOf(false) }
    Button(
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 1.dp, // 기본 그림자
            pressedElevation = 0.dp, // 버튼을 눌렀을 때 그림자
            disabledElevation = 0.dp // enabled가 false일때 그림자
        ),
        onClick = { /* TODO: 로그인 로직 */ },
        modifier = Modifier
            .width(310.dp)
            .padding(top = 30.dp)
            .height(45.dp),
        shape = RoundedCornerShape(24.dp), // 라운딩 처리
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isButtonActive) colorResource(R.color.yellow_300) else Color.Gray
        )
    ) {
        Text(
            text = stringResource(R.string.login_button),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun LoginLinkText(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.clickable { navController.navigate("find_id") },
            text = stringResource(R.string.loginLink_id),
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            color = colorResource(R.color.orange_500)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "|",  color = colorResource(R.color.orange_500))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            modifier = Modifier.clickable { navController.navigate("find_pw") },
            text = stringResource(R.string.loginLink_pw),
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            color = colorResource(R.color.orange_500)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "|",  color = colorResource(R.color.orange_500))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            modifier = Modifier.clickable { navController.navigate("join") },
            text = stringResource(R.string.loginLink_join),
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            color = colorResource(R.color.orange_500)
        )
    }
}

@Composable
fun LoginOrDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(310.dp)
            .padding(top = 25.dp)
    ) {
        // 왼쪽 구분선
        Divider(
            color = colorResource(R.color.gray_500),
            thickness = 1.dp,
            modifier = Modifier.weight(1f) // 가로 여백 비율로 확장
        )

        Spacer(modifier = Modifier.width(8.dp))

        // '또는' 텍스트
        Text(
            text = "또는",
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            color = colorResource(R.color.gray_500),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 오른쪽 구분선
        Divider(
            color = colorResource(R.color.gray_500),
            thickness = 1.dp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun KakaoLoginButton() {
    Button(
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 1.dp, // 기본 그림자
            pressedElevation = 0.dp, // 버튼을 눌렀을 때 그림자
            disabledElevation = 0.dp // enabled가 false일때 그림자
        ),
        onClick = { /* TODO: 카카오로그인 로직 */ },
        modifier = Modifier
            .width(310.dp)
            .padding(top = 10.dp)
            .height(45.dp),
        shape = RoundedCornerShape(24.dp), // 라운딩 처리
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.kakaoYellow)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_kakao),
                contentDescription = "Kakao Login",
                modifier = Modifier.size(12.99.dp, 12.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = stringResource(R.string.kakao_login),
                fontSize = 15.sp,
                fontWeight = FontWeight(600),
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(navController = NavHostController(LocalContext.current))
}