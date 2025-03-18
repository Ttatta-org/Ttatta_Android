package com.umc.login.component.form

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldLabelScope
import com.umc.login.component.CustomTextFieldProp
import com.umc.login.component.toCustomTextFieldUnderMessageProp
import com.umc.login.logic.state.NameValidationState

@Composable
fun NameForm(
    name: String,
    state: NameValidationState,
    onNameChanged: (String) -> Unit,
) {
    CustomTextFieldLabelScope(
        underMessageProp = state.message?.toCustomTextFieldUnderMessageProp(),
    ) {
        CustomTextField(
            prop = CustomTextFieldProp(
                value = name,
                onValueChanged = onNameChanged,
                placeholder = stringResource(id = R.string.name_placeholder),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNameForm() {
    NameForm(
        name = "",
        state = NameValidationState.VALID,
        onNameChanged = {},
    )
}