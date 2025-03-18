package com.umc.login.logic.state

import com.umc.login.logic.StateMessage

enum class EmailValidationState(
    val message: StateMessage? = null,
) {
    VALID,
    WRONG_FORMAT,
    NOT_ALLOWED_CHAR,
    NOT_ALLOWED_DOMAIN,
}

fun isEmailValid(email: String): EmailValidationState {
    if (!email.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))) return EmailValidationState.NOT_ALLOWED_CHAR

    if (!email.contains("@")) return EmailValidationState.WRONG_FORMAT

    val (local, domain) = email.split("@")
    if (local.isEmpty() || domain.isEmpty()) return EmailValidationState.WRONG_FORMAT

    val (name, extension) = domain.split(".")
    if (name.isEmpty() || extension.isEmpty()) return EmailValidationState.WRONG_FORMAT

    return EmailValidationState.VALID
}