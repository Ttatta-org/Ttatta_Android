package com.umc.mypage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.umc.core.util.runWithScope
import com.umc.core.util.showToast
import com.umc.mypage.screen.LockPasswordScreen

@Composable
fun LockApp(
    viewModel: LockViewModel,
    onPinCorrect: () -> Unit,
) {
    val context = LocalContext.current
    var password by remember { mutableStateOf("") }
    var showIncorrectMessage by remember { mutableStateOf(false) }

    LaunchedEffect(password) {
        if (password.length != 4) return@LaunchedEffect

        viewModel.runWithScope {
            runCatching { viewModel.isPinCorrect(pin = password) }
                .onSuccess { isCorrect ->
                    if (isCorrect) {
                        onPinCorrect()
                    } else {
                        password = ""
                        showIncorrectMessage = true
                    }
                }
                .onFailure {
                    password = ""
                    context.showToast("비밀번호 검증 중 오류가 발생했습니다.")
                }
        }
    }

    LockPasswordScreen(
        title = "암호 입력",
        description = "암호를 입력해주세요.",
        errorMessage = if (showIncorrectMessage) "암호가 일치하지 않아요! 다시 입력해주세요." else null,
        totalCount = 4,
        fillCount = password.length,
        onBackButtonClicked = null,
        onNumberClicked = {
            if (password.length < 4) password += it
        },
        onEraseButtonClicked = {
            password = password.dropLast(1)
        },
        onCancelButtonClicked = {
            password = ""
        },
    )
}