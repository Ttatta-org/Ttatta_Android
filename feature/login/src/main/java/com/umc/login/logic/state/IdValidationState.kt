package com.umc.login.logic.state

import androidx.compose.ui.graphics.Color
import com.umc.design.Grey300
import com.umc.design.Negative
import com.umc.design.Positive
import com.umc.login.R
import com.umc.login.logic.StateMessage

enum class IdValidationState(
    val message: StateMessage? = null,
) {
    VALID(
        message = StateMessage(
            id = R.string.id_available,
            color = Color.Positive,
        ),
    ),
    TOO_SHORT(
        message = StateMessage(
            id = R.string.id_too_long_or_has_not_allowed_char,
            color = Color.Grey300,
        ),
    ),
    TOO_LONG(
        message = StateMessage(
            id = R.string.id_too_long_or_has_not_allowed_char,
            color = Color.Negative,
        ),
    ),
    NOT_ALLOWED_CHAR(
        message = StateMessage(
            id = R.string.id_too_long_or_has_not_allowed_char,
            color = Color.Negative,
        ),
    ),
    DID_NOT_CHECKED_DUPLICATED,
    DUPLICATED(
        message = StateMessage(
            id = R.string.id_duplicated,
            color = Color.Negative,
        ),
    )
}

fun isIdValid(id: String): IdValidationState {
    if (!id.matches(Regex("^[a-zA-Z]*$"))) return IdValidationState.NOT_ALLOWED_CHAR
    if (id.length < 6) return IdValidationState.TOO_SHORT
    if (id.length > 15) return IdValidationState.TOO_LONG
    return IdValidationState.DID_NOT_CHECKED_DUPLICATED
}