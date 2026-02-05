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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.component.CustomButton
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.login.R
import com.umc.login.component.AnimatedProgressBar
import com.umc.login.component.AnimatedProgressBarProp
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldLabelScope
import com.umc.login.model.prop.CustomTextFieldProp
import com.umc.login.model.prop.CustomTextFieldUnderMessageProp

data class FormScreenDescriptionMessageProp(
    val message: String,
    val color: Color,
    val content: (@Composable () -> Unit)? = null,
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
            .padding(
                WindowInsets.systemBars
                    .union(WindowInsets.ime)
                    .asPaddingValues()
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 32.dp)
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // 뒤로가기 버튼
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onBackButtonClicked() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        modifier = Modifier
                            .padding(8.dp)
                            .size(16.dp),
                        tint = LocalColorTheme.current.primary[500],
                        contentDescription = null,
                    )
                }
                // 최상단 메시지
                if (topLineMessage != null) Box(
                    contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = topLineMessage,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W800,
                        color = LocalColorTheme.current.primary[500],
                    )
                }
            }
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.weight(1f),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .widthIn(max = 480.dp)
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
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = it.message,
                                fontSize = 18.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.W800,
                                color = it.color,
                            )
                            it.content?.invoke()
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    form()
                    Spacer(modifier = Modifier.height(120.dp))
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
                    CustomButton(
                        text = nextButtonLabel,
                        onClick = onNextButtonClicked,
                        isEnabled = isNextButtonEnabled,
                    )
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
            CustomTextFieldLabelScope(
                underMessageProp = CustomTextFieldUnderMessageProp(
                    value = "이것은 통과 메시지입니다",
                    color = LocalColorTheme.current.positive,
                ),
                customTextField = {
                    CustomTextField(
                        prop = CustomTextFieldProp(
                            value = "따따따따따따따",
                            onValueChanged = {},
                            placeholder = "입력해주세요",
                            isVisible = true,
                            tail = {
                                IconButton(
                                    onClick = {},
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_visibility_off),
                                        contentDescription = null,
                                        tint = LocalColorTheme.current.grey[400],
                                    )
                                }
                            },
                        )
                    )
                },
            )
        }
    }
}