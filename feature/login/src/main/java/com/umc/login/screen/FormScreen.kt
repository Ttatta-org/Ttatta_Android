package com.umc.login.screen

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.Grey400
import com.umc.design.Primary200
import com.umc.design.Primary400
import com.umc.design.Secondary300
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
    Log.d("JoinScreen", "ime: ${WindowInsets.ime.asPaddingValues().calculateBottomPadding()}")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.union(WindowInsets.ime).asPaddingValues())
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 뒤로가기 버튼
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onBackButtonClicked() },
                    tint = Color.Primary400,
                    contentDescription = null,
                )
                // 최상단 메시지
                if (topLineMessage != null) Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = topLineMessage,
                        color = Color.Primary400,
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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
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
                    // 설명 메시지
                    formScreenDescriptionMessageProp?.let {
                        Text(
                            text = it.message,
                            color = it.color,
                        )
                    }
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
                        color = Color.Primary400,
                        fontSize = 12.sp
                    )
                    // 다음 버튼
                    ElevatedButton(
                        enabled = isNextButtonEnabled,
                        onClick = onNextButtonClicked,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color.Primary200,
                            disabledContentColor = Color.White,
                            disabledContainerColor = Color.Secondary300,
                        )
                    ) {
                        Text(text = nextButtonLabel)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFormScreen() {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                (context as Activity).window.setDecorFitsSystemWindows(false)
        } catch (_: ClassCastException) {
            // empty
        }
    }

    FormScreen(
        topLineMessage = "비밀번호 찾기",
        nextButtonLabel = "다음으로",
        nextButtonOverMessage = "필수 입력 항목입니다",
        animatedProgressBarProp = AnimatedProgressBarProp(currentStep = 2, totalSteps = 6),
        formScreenDescriptionMessageProp = FormScreenDescriptionMessageProp(
            message = "이메일을 입력해주세요",
            color = Color.Grey400,
        ),
        isNextButtonEnabled = false,
        isLogoVisible = true,
        onNextButtonClicked = {},
        onBackButtonClicked = {}
    ) {
        PreviewCustomTextField()
    }
}