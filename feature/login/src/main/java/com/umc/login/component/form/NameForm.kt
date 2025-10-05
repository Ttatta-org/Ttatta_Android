package com.umc.login.component.form

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.design.theme.ThemeProvider
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldLabelScope
import com.umc.login.component.CustomTextFieldProp
import com.umc.login.component.CustomTextFieldTextAlignment
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
                textAlignment = CustomTextFieldTextAlignment.START,
                placeholder = stringResource(id = R.string.name_placeholder),
                tail = {
                    if (name.isNotEmpty()) ClearButton(onClick = { onNameChanged("") })
                }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNameForm() {
    ThemeProvider {
        NameForm(
            name = "asd",
            state = NameValidationState.VALID,
            onNameChanged = {},
        )
    }
}