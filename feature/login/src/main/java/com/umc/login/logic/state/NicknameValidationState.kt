package com.umc.login.logic.state

import androidx.compose.ui.graphics.Color
import com.umc.design.Grey300
import com.umc.design.Negative
import com.umc.login.R
import com.umc.login.logic.StateMessage

enum class NicknameValidationState(
    val message: StateMessage? = null,
) {
    VALID,
    TOO_SHORT(
        message = StateMessage(
            id = R.string.nickname_too_long,
            color = Color.Grey300,
        ),
    ),
    TOO_LONG(
        message = StateMessage(
            id = R.string.nickname_too_long,
            color = Color.Negative,
        ),
    ),
    NOT_ALLOWED_CHAR,
}

fun isNicknameValid(nickname: String): NicknameValidationState {
    if (nickname.isEmpty()) return NicknameValidationState.TOO_SHORT
    if (nickname.length > 8) return NicknameValidationState.TOO_LONG
    // if (!nickname.matches(Regex("^[가-힣a-zA-Z0-9]*$"))) return NicknameValidationState.NOT_ALLOWED_CHAR
    return NicknameValidationState.VALID
}