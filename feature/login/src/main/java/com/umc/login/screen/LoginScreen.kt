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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.component.CustomButton
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme
import com.umc.design.theme.ThemeProvider
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldLabelScope
import com.umc.login.model.prop.CustomTextFieldProp
import com.umc.login.model.prop.CustomTextFieldUnderMessageProp

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
            .padding(
                bottom = WindowInsets.ime
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .widthIn(max = 480.dp)
                .padding(horizontal = 22.dp)
        ) {
            // 로고
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ttatta_logo),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                )
                Text(
                    text = stringResource(id = R.string.under_logo_message),
                    fontSize = 13.sp,
                    letterSpacing = (-0.4).sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.W800,
                    color = LocalColorTheme.current.primary[400],
                )
            }
            Spacer(modifier = Modifier.height(34.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomTextFieldLabelScope(
                    underMessageProp = CustomTextFieldUnderMessageProp(
                        value = stringResource(id = R.string.login_error_message),
                        color = if (isLoginErrorOccurred) LocalColorTheme.current.negative else Color.Transparent
                    )
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // 아이디 입력창
                        CustomTextField(
                            prop = CustomTextFieldProp(
                                value = id,
                                onValueChanged = onIdChanged,
                                placeholder = stringResource(id = R.string.login_id_placeholder),
                                isVisible = true,
                            )
                        )
                        // 비밀 번호 입력창
                        CustomTextField(
                            prop = CustomTextFieldProp(
                                value = password,
                                onValueChanged = onPasswordChanged,
                                placeholder = stringResource(id = R.string.login_password_placeholder),
                                isVisible = isPasswordVisible,
                                tail = {
                                    Box(
                                        modifier = Modifier.padding(
                                            vertical = 8.dp,
                                            horizontal = 16.dp,
                                        ),
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                id = if (isPasswordVisible) R.drawable.ic_visibility_on
                                                else R.drawable.ic_visibility_off
                                            ),
                                            contentDescription = null,
                                            tint = LocalColorTheme.current.grey[400],
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { onPasswordVisibilityChanged(!isPasswordVisible) },
                                        )
                                    }
                                },
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                // 로그인 버튼
                CustomButton(
                    text = stringResource(id = R.string.login),
                    isEnabled = id.isNotEmpty() && password.isNotEmpty(),  // TODO: business logic
                    onClick = onLoginButtonClicked,
                )
                // ID와 비번 찾기 및 회원 가입
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        stringResource(id = R.string.find_id) to onFindIdButtonClicked,
                        stringResource(id = R.string.find_password) to onFindPasswordButtonClicked,
                        stringResource(id = R.string.join) to onJoinButtonClicked,
                    ).forEachIndexed { index, (text, onClick) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .clickable { onClick() },
                        ) {
                            Text(
                                text = text,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.W400,
                                letterSpacing = (-0.4).sp,
                                color = LocalColorTheme.current.grey[500],
                                modifier = Modifier
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            )
                        }
                        if (index < 2) VerticalDivider(
                            modifier = Modifier.height(12.dp),
                            color = LocalColorTheme.current.grey[500],
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(40 .dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 구분선
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = LocalColorTheme.current.grey[400],
                    )
                    Text(
                        text = stringResource(id = R.string.or),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = LocalColorTheme.current.grey[400],
                        fontWeight = FontWeight.W400,
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = LocalColorTheme.current.grey[400],
                    )
                }
                // 카카오 로그인 버튼
                CustomButton(
                    onClick = onKakaoLoginButtonClicked,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xFF3C1E1E),
                        containerColor = Color(0xFFFAE100),
                    ),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 15.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kakao),
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight,
                            modifier = Modifier.height(15.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.kakao_login),
                            fontFamily = LocalFontTheme.current.font,
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.W700,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    ThemeProvider {
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
}