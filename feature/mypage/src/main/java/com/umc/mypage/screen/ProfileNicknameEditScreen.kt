package com.umc.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.umc.mypage.R

@Composable
fun ProfileNicknameEditScreen(
    nickname: String,
    isDoneButtonEnabled: Boolean,
    onBackButtonClicked: () -> Unit,
    onNicknameChanged: (String) -> Unit,
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
            centerText = "닉네임 수정",
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
                text = "닉네임",
                color = LocalColorTheme.current.grey[600],
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
            Spacer(modifier = Modifier.height(5.dp))
            BasicTextField(
                value = nickname,
                onValueChange = onNicknameChanged,
                textStyle = TextStyle(
                    fontFamily = LocalFontTheme.current.font,
                    fontWeight = FontWeight.W400,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = Color.Black,
                ),
            ) { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            shape = RoundedCornerShape(15.dp),
                            width = 1.dp,
                            color = LocalColorTheme.current.grey[400],
                        )
                        .padding(horizontal = 20.dp, vertical = 15.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "닉네임을 입력해주세요.",
                            color = LocalColorTheme.current.grey[400],
                            fontWeight = FontWeight.W400,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.alpha(if (nickname.isEmpty()) 1f else 0f)
                        )
                        innerTextField.invoke()
                    }
                    if (nickname.isNotEmpty()) Image(
                        painter = painterResource(R.drawable.ic_mypage_x),
                        contentDescription = "모두 지우기",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null,
                                onClick = { onNicknameChanged("") }
                            ),
                    )
                }
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
private fun ProfileNicknameEditScreenPreview() {
    ThemeProvider {
        ProfileNicknameEditScreen(
            nickname = "김따따",
            isDoneButtonEnabled = true,
            onBackButtonClicked = {},
            onNicknameChanged = {},
            onDoneButtonClicked = {},
        )
    }
}