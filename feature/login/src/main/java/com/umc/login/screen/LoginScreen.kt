package com.umc.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.Grey300
import com.umc.design.Negative
import com.umc.design.Primary200
import com.umc.design.Primary300
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldLabelScope
import com.umc.login.component.CustomTextFieldProp
import com.umc.login.component.CustomTextFieldTextAlignment
import com.umc.login.component.CustomTextFieldUnderMessageProp

@Composable
fun LoginScreen(
    id: String,
    password: String,
    isPasswordVisible: Boolean,
    isLoginErrorOccurred: Boolean,
    onIdChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityChanged: (Boolean) -> Unit,
    onLoginButtonClicked: () -> Unit,
    onKakaoLoginButtonClicked: () -> Unit,
    onFindIdButtonClicked: () -> Unit,
    onFindPasswordButtonClicked: () -> Unit,
    onJoinButtonClicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding())
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(64.dp),
            modifier = Modifier
                .widthIn(max = 480.dp)
                .padding(horizontal = 32.dp)
        ) {
            // 로고
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ttatta_logo),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = stringResource(id = R.string.under_logo_message),
                    color = Color.Primary300,
                    fontSize = 12.sp,
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomTextFieldLabelScope(
                        underMessageProp = CustomTextFieldUnderMessageProp(
                            value = stringResource(id = R.string.login_error_message),
                            color = if (isLoginErrorOccurred) Color.Negative else Color.Transparent
                        )
                    ) {
                        // 아이디 입력창
                        CustomTextField(
                            prop = CustomTextFieldProp(
                                value = id,
                                onValueChanged = onIdChanged,
                                placeholder = stringResource(id = R.string.login_id_placeholder),
                                textAlignment = CustomTextFieldTextAlignment.CENTER,
                                isVisible = true,
                                tail = { Spacer(modifier = Modifier.height(32.dp)) },
                            )
                        )
                        // 비밀 번호 입력창
                        CustomTextField(
                            prop = CustomTextFieldProp(
                                value = password,
                                onValueChanged = onPasswordChanged,
                                placeholder = stringResource(id = R.string.login_password_placeholder),
                                textAlignment = CustomTextFieldTextAlignment.CENTER,
                                isVisible = isPasswordVisible,
                                tail = {
                                    Box(
                                        modifier = Modifier.padding(
                                            vertical = 8.dp,
                                            horizontal = 16.dp
                                        ),
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                id = if (isPasswordVisible)
                                                    R.drawable.ic_visibility
                                                else
                                                    R.drawable.ic_visibility_off
                                            ),
                                            contentDescription = null,
                                            tint = Color.Grey300,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { onPasswordVisibilityChanged(!isPasswordVisible) }
                                        )
                                    }
                                }
                            )
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    // 로그인 버튼
                    ElevatedButton(
                        onClick = onLoginButtonClicked,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color.Primary200
                        )
                    ) {
                        Text(text = stringResource(id = R.string.login))
                    }
                    // ID와 비번 찾기 및 회원 가입
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.find_id),
                            fontSize = 12.sp,
                            color = Color.Primary300,
                            modifier = Modifier.clickable { onFindIdButtonClicked() }
                        )
                        VerticalDivider(
                            modifier = Modifier.height(12.dp),
                            color = Color.Primary300,
                        )
                        Text(
                            text = stringResource(id = R.string.find_password),
                            fontSize = 12.sp,
                            color = Color.Primary300,
                            modifier = Modifier.clickable { onFindPasswordButtonClicked() }
                        )
                        VerticalDivider(
                            modifier = Modifier.height(12.dp),
                            color = Color.Primary300,
                        )
                        Text(
                            text = stringResource(id = R.string.join),
                            fontSize = 12.sp,
                            color = Color.Primary300,
                            modifier = Modifier.clickable { onJoinButtonClicked() }
                        )
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 구분선
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.Grey300
                    )
                    Text(
                        text = stringResource(id = R.string.or),
                        color = Color.Grey300
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.Grey300
                    )
                }
                // 카카오 로그인 버튼
                ElevatedButton(
                    onClick = onKakaoLoginButtonClicked,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = Color(0xFFFAE100)
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_kakao),
                            contentDescription = null,
                        )
                        Text(text = stringResource(id = R.string.kakao_login))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        id = "",
        password = "",
        isPasswordVisible = false,
        isLoginErrorOccurred = true,
        onIdChanged = {},
        onPasswordChanged = {},
        onPasswordVisibilityChanged = {},
        onLoginButtonClicked = {},
        onKakaoLoginButtonClicked = {},
        onFindIdButtonClicked = {},
        onFindPasswordButtonClicked = {},
        onJoinButtonClicked = {},
    )
}