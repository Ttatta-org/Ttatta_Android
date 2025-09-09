package com.umc.mypage

import androidx.compose.runtime.Composable

@Composable
fun LockApp(
    viewModel: LockViewModel,
    onPinCorrect: () -> Unit,
) {
    LockPasswordScreen(
        isChangingPassword = false,
        isCheckingMode = true,
        onComplete = { pin ->
            val isCorrect = viewModel.isPinCorrect(pin = pin)
            if (isCorrect) onPinCorrect()
        }
    )
}