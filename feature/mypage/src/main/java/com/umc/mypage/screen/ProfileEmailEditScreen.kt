package com.umc.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.component.CustomButton
import com.umc.design.component.CustomHeader
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme
import com.umc.design.theme.ThemeProvider
import java.time.Duration

@Composable
fun ProfileEmailEditScreen(
    previousEmail: String,
    newEmail: String,
    code: String,
    codeSendingErrorMessage: String?,
    remainTime: Duration?,
    showSendCodeButtonAsResend: Boolean,
    showTimeOutMessage: Boolean,
    isSendCodeButtonEnabled: Boolean,
    isCodeFieldEditable: Boolean,
    isDoneButtonEnabled: Boolean,
    onBackButtonClicked: () -> Unit,
    onNewEmailChanged: (String) -> Unit,
    onCodeChanged: (String) -> Unit,
    onSendCodeButtonClicked: () -> Unit,
    onDoneButtonClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
            .background(Color.White)
    ) {
        CustomHeader(
            showLogo = false,
            centerText = "이메일 변경",
            onBackButtonClicked = onBackButtonClicked,
            backgroundColor = LocalColorTheme.current.secondary[100],
            waveColor = LocalColorTheme.current.primary[400],
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 20.dp, horizontal = 22.dp),
        ) {
            Text(
                text = "기존 이메일",
                color = LocalColorTheme.current.grey[600],
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        shape = RoundedCornerShape(15.dp),
                        width = 1.dp,
                        color = LocalColorTheme.current.grey[400],
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = previousEmail,
                    color = LocalColorTheme.current.grey[400],
                    fontWeight = FontWeight.W400,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "새 이메일",
                color = LocalColorTheme.current.grey[600],
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        shape = RoundedCornerShape(15.dp),
                        width = 1.dp,
                        color = LocalColorTheme.current.grey[400],
                    )
            ) {
                BasicTextField(
                    value = newEmail,
                    onValueChange = onNewEmailChanged,
                    textStyle = TextStyle(
                        fontFamily = LocalFontTheme.current.font,
                        fontWeight = FontWeight.W400,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        color = Color.Black,
                    ),
                    modifier = Modifier.weight(1f),
                ) { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 15.dp, bottom = 15.dp)
                    ) {
                        Text(
                            text = "이메일 주소를 입력해주세요.",
                            color = LocalColorTheme.current.grey[400],
                            fontWeight = FontWeight.W400,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.alpha(if (newEmail.isEmpty()) 1f else 0f)
                        )
                        innerTextField.invoke()
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(end = 7.dp)
                        .background(
                            color = if (isSendCodeButtonEnabled) LocalColorTheme.current.primary[300] else LocalColorTheme.current.grey[300],
                            shape = RoundedCornerShape(10.dp),
                        )
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = onSendCodeButtonClicked,
                        ),
                ) {
                    Text(
                        text = if (showSendCodeButtonAsResend) "인증번호 재전송" else "인증코드 발송",
                        color = Color.White,
                        fontWeight = FontWeight.W700,
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
            }
            if (codeSendingErrorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = codeSendingErrorMessage,
                    color = LocalColorTheme.current.negative,
                    fontWeight = FontWeight.W700,
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    modifier = Modifier.padding(start = 10.dp),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        shape = RoundedCornerShape(15.dp),
                        width = 1.dp,
                        color = LocalColorTheme.current.grey[400],
                    )
            ) {
                BasicTextField(
                    value = code,
                    onValueChange = onCodeChanged,
                    textStyle = TextStyle(
                        fontFamily = LocalFontTheme.current.font,
                        fontWeight = FontWeight.W400,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        color = Color.Black,
                    ),
                    enabled = isCodeFieldEditable,
                    modifier = Modifier.weight(1f),
                ) { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 15.dp, bottom = 15.dp)
                    ) {
                        Text(
                            text = "인증번호 입력",
                            color = LocalColorTheme.current.grey[400],
                            fontWeight = FontWeight.W400,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.alpha(if (code.isEmpty()) 1f else 0f),
                        )
                        innerTextField.invoke()
                    }
                }
                if (remainTime != null) Box(
                    modifier = Modifier.padding(end = 16.dp),
                ) {
                    Text(
                        text = "${"%02d".format(remainTime.seconds / 60)}:${"%02d".format(remainTime.seconds % 60)}",
                        color = LocalColorTheme.current.negative,
                        fontWeight = FontWeight.W700,
                        fontSize = 13.sp,
                    )
                }
            }
            if (showTimeOutMessage) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "입력 시간이 초과되었습니다.",
                    color = LocalColorTheme.current.negative,
                    fontWeight = FontWeight.W700,
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    modifier = Modifier.padding(start = 10.dp),
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "※ 닉네임은 8글자 이하로 작성해주세요.",
                color = LocalColorTheme.current.grey[400],
                fontWeight = FontWeight.W700,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 10.dp),
            )
            if (showSendCodeButtonAsResend) Text(
                text = "※ 인증번호가 오지 않았다면 재전송 버튼을 눌러주세요.",
                color = LocalColorTheme.current.grey[400],
                fontWeight = FontWeight.W700,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 10.dp),
            )
        }
        Box(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
        ) {
            CustomButton(
                text = "완료",
                isEnabled = isDoneButtonEnabled,
                onClick = onDoneButtonClicked,
            )
        }
    }
}

@Preview
@Composable
private fun ProfileEmailEditScreenPreview() {
    ThemeProvider {
        ProfileEmailEditScreen(
            previousEmail = "ttatta@gmail.com",
            newEmail = "ttatta1234@gmail.com",
            code = "123456",
            codeSendingErrorMessage = "이미 가입된 이메일입니다.",
            remainTime = Duration.ofSeconds(123),
            showTimeOutMessage = true,
            showSendCodeButtonAsResend = true,
            isSendCodeButtonEnabled = true,
            isCodeFieldEditable = false,
            isDoneButtonEnabled = false,
            onBackButtonClicked = {},
            onNewEmailChanged = {},
            onCodeChanged = {},
            onSendCodeButtonClicked = {},
            onDoneButtonClicked = {},
        )
    }
}