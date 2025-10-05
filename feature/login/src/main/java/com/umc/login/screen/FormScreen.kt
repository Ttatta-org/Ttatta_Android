package com.umc.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme
import com.umc.design.theme.ThemeProvider
import com.umc.login.R
import com.umc.login.component.AnimatedProgressBar
import com.umc.login.component.AnimatedProgressBarProp
import com.umc.login.component.PreviewCustomTextField

data class FormScreenDescriptionMessageProp(
    val message: String,
    val color: Color,
)

@Composable
fun FormScreen(
    topLineMessage: String?,
    nextButtonLabel: String,
    nextButtonOverMessage: String?,
    formScreenDescriptionMessageProp: FormScreenDescriptionMessageProp?,
    animatedProgressBarProp: AnimatedProgressBarProp?,
    isNextButtonEnabled: Boolean,
    isLogoVisible: Boolean,
    onNextButtonClicked: () -> Unit,
    onBackButtonClicked: () -> Unit,
    form: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.union(WindowInsets.ime).asPaddingValues())
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 32.dp)
        ) {
            Box(
                contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxWidth()
            ) {
                // 뒤로가기 버튼
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onBackButtonClicked() },
                    tint = LocalColorTheme.current.primary[500],
                    contentDescription = null,
                )
                // 최상단 메시지
                if (topLineMessage != null) Box(
                    contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = topLineMessage,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        color = LocalColorTheme.current.primary[500],
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 480.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 로고
                    if (isLogoVisible) Box(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_ttatta_logo),
                            modifier = Modifier.size(48.dp),
                            contentScale = ContentScale.Fit,
                            contentDescription = null,
                        )
                    }
                    // 진행도
                    animatedProgressBarProp?.let { AnimatedProgressBar(prop = it) }
                    Spacer(modifier = Modifier.height(48.dp))
                    // 설명 메시지
                    formScreenDescriptionMessageProp?.let {
                        Box (
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = it.message,
                                fontSize = 18.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.W800,
                                color = it.color,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    form()
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 버튼 위 메시지
                    if (nextButtonOverMessage != null) Text(
                        text = nextButtonOverMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W400,
                        color = LocalColorTheme.current.primary[600],
                    )
                    // 다음 버튼
                    ElevatedButton(
                        enabled = isNextButtonEnabled,
                        onClick = onNextButtonClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 2.dp,
                            disabledElevation = 2.dp,
                        ),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = LocalColorTheme.current.primary[400],
                            disabledContentColor = Color.White,
                            disabledContainerColor = LocalColorTheme.current.primary[200],
                        ),
                        shape = RoundedCornerShape(15.dp),
                    ) {
                        Text(
                            text = nextButtonLabel,
                            fontFamily = LocalFontTheme.current.font,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.W600,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFormScreen() {
    ThemeProvider {
        FormScreen(
            topLineMessage = "비밀번호 찾기",
            nextButtonLabel = "다음으로",
            nextButtonOverMessage = "필수 입력 항목입니다",
            animatedProgressBarProp = AnimatedProgressBarProp(currentStep = 2, totalSteps = 6),
            formScreenDescriptionMessageProp = FormScreenDescriptionMessageProp(
                message = "이메일을 입력해주세요\n이메일을 입력해주세요",
                color = LocalColorTheme.current.grey[700],
            ),
            isNextButtonEnabled = false,
            isLogoVisible = false,
            onNextButtonClicked = {},
            onBackButtonClicked = {},
        ) {
            PreviewCustomTextField()
        }
    }
}