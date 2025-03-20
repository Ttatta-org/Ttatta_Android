package com.umc.login.logic.state

import androidx.compose.ui.graphics.Color
import com.umc.design.Grey300
import com.umc.design.Negative
import com.umc.design.Positive
import com.umc.login.R
import com.umc.login.logic.StateMessage

enum class PasswordValidationState(
    val passwordMessage: StateMessage? = null,
    val confirmPasswordMessage: StateMessage? = null,
) {
    VALID(
        passwordMessage = StateMessage(
            id = R.string.password_available,
            color = Color.Positive,
        ),
        confirmPasswordMessage = StateMessage(
            id = R.string.password_match,
            color = Color.Positive,
        ),
    ),
    TOO_SHORT(
        passwordMessage = StateMessage(
            id = R.string.password_too_short_or_too_simple,
            color = Color.Grey300,
        ),
        confirmPasswordMessage = StateMessage(
            id = R.string.password_enter_confirm,
            color = Color.Grey300,
        ),
    ),
    TOO_LONG,
    TOO_SIMPLE(
        passwordMessage = StateMessage(
            id = R.string.password_too_short_or_too_simple,
            color = Color.Negative,
        ),
        confirmPasswordMessage = StateMessage(
            id = R.string.password_enter_confirm,
            color = Color.Grey300,
        ),
    ),
    NOT_ALLOWED_CHAR(
        confirmPasswordMessage = StateMessage(
            id = R.string.password_enter_confirm,
            color = Color.Grey300,
        ),
    ),
    CONFIRM_PASSWORD_NOT_ENTERED(
        passwordMessage = StateMessage(
            id = R.string.password_available,
            color = Color.Positive,
        ),
        confirmPasswordMessage = StateMessage(
            id = R.string.password_enter_confirm,
            color = Color.Grey300,
        ),
    ),
    NOT_MATCH(
        passwordMessage = StateMessage(
            id = R.string.password_available,
            color = Color.Positive,
        ),
        confirmPasswordMessage = StateMessage(
            id = R.string.password_not_match,
            color = Color.Negative,
        ),
    ),
}

fun isPasswordValid(password: String, confirmPassword: String? = null): PasswordValidationState {
    if (password.length < 8) return PasswordValidationState.TOO_SHORT

    // if (password.length > 16) return PasswordValidationState.TOO_LONG
    // if (!password.matches(Regex("^[a-zA-Z0-9!@#\$%^&*()_+=<>?]*$"))) return PasswordValidationState.NOT_ALLOWED_CHAR

    // 비밀번호 검증 조건
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasLowerCase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { it in "!@#\$%^&*()_+=<>?" }

    if (!hasSpecialChar || !hasDigit || !hasLowerCase && !hasUpperCase) return PasswordValidationState.TOO_SIMPLE

    if (confirmPassword.isNullOrEmpty()) return PasswordValidationState.CONFIRM_PASSWORD_NOT_ENTERED
    if (password != confirmPassword) return PasswordValidationState.NOT_MATCH
    return PasswordValidationState.VALID
}