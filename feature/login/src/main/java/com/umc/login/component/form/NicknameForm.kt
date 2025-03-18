package com.umc.login.component.form

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldLabelScope
import com.umc.login.component.CustomTextFieldProp
import com.umc.login.component.toCustomTextFieldUnderMessageProp
import com.umc.login.logic.state.NicknameValidationState

@Composable
fun NicknameForm(
    nickname: String,
    state: NicknameValidationState,
    onNicknameChanged: (String) -> Unit,
) {
    CustomTextFieldLabelScope(
        underMessageProp = state.message?.toCustomTextFieldUnderMessageProp(),
    ) {
        CustomTextField(
            prop = CustomTextFieldProp(
                value = nickname,
                onValueChanged = onNicknameChanged,
                placeholder = stringResource(id = R.string.nickname),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNicknameForm() {
    NicknameForm(
        nickname = "",
        state = NicknameValidationState.TOO_LONG,
        onNicknameChanged = {},
    )
}