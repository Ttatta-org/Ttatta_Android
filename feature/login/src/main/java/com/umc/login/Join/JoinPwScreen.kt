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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.navigation.NavHostController
import com.umc.login.R


@Composable
fun JoinPwScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        JoinPwView(onNext = { navController.navigate("join_confirm") })
    }
}

@Composable
fun JoinPwView(onNext: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isPasswordValid by remember { mutableStateOf(false) }
    var isPasswordMatched by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.join_pw),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(600),
            color = colorResource(R.color.yellow_200)
        )

        Spacer(modifier = Modifier.height(37.dp))

        PwInputTextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordValid = it.length >= 8
            },
            placeholder = stringResource(R.string.join_pw_comment),
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordToggleClick = { passwordVisible = !passwordVisible },
            onFocusChange = {}
        )
        Spacer(modifier = Modifier.height(5.dp))

        if (isPasswordValid) {
            Text(
                text = stringResource(R.string.join_pw_available),
                color = colorResource(R.color.positiveGreen),
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(400)
            )

            Spacer(modifier = Modifier.height(12.dp))

            PwInputTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    isPasswordMatched = (password == it)
                },
                placeholder = stringResource(R.string.join_pw_check),
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordToggleClick = { passwordVisible = !passwordVisible },
                onFocusChange = {}
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = if (isPasswordMatched) "비밀번호가 일치해요!" else "비밀번호가 일치하지 않아요!",
                color = if (isPasswordMatched) colorResource(R.color.positiveGreen) else colorResource(R.color.negativeRed),
                fontSize = 12.sp
            )
        }
        else {
            Text(
                text = stringResource(R.string.join_pw_small_comment),
                color = colorResource(R.color.gray_400),
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(400)

            )
        }

        if (!isPasswordValid) {Spacer(modifier = Modifier.height(101.dp))}
        else if (isPasswordValid){Spacer(modifier = Modifier.height(34.dp))}

        Button(
            enabled = isPasswordValid && isPasswordMatched,
            onClick = { if (isPasswordValid && isPasswordMatched) onNext() },
            modifier = Modifier.width(310.dp).height(45.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPasswordValid && isPasswordMatched) colorResource(R.color.orange_200) else colorResource(R.color.yellow_300),
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

@Composable
fun PwInputTextField(
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
            onValueChange = {
                onValueChange(it)
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Black
            ),
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
                .height(51.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colorResource(R.color.gray_500),
                unfocusedIndicatorColor = colorResource(R.color.gray_500),
                cursorColor = Color.Black
            )
        )
        if (value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.gray_500),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewJoinPwScreen() {
    JoinPwScreen(navController = NavHostController(LocalContext.current))
}
