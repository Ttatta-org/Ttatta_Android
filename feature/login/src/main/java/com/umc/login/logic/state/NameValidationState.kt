package com.umc.login.logic.state

import androidx.compose.ui.graphics.Color
import com.umc.design.Grey300
import com.umc.design.Negative
import com.umc.login.R
import com.umc.login.logic.StateMessage

enum class NameValidationState(
    val message: StateMessage? = null,
) {
    VALID,
    TOO_SHORT(
        message = StateMessage(
            id = R.string.name_not_allowed_char,
            color = Color.Grey300,
        ),
    ),
    TOO_LONG,
    NOT_ALLOWED_CHAR(
        message = StateMessage(
            id = R.string.name_not_allowed_char,
            color = Color.Negative,
        ),
    ),
}

fun isNameValid(name: String): NameValidationState {
    if (!name.matches(Regex("^[가-힣a-zA-Z0-9]*$"))) return NameValidationState.NOT_ALLOWED_CHAR
    if (name.length < 2) return NameValidationState.TOO_SHORT
    // if (name.length > 8) return NameValidationState.TOO_LONG
    return NameValidationState.VALID
}