package com.umc.login.component.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.design.Grey300
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldLabelScope
import com.umc.login.component.CustomTextFieldProp
import com.umc.login.component.CustomTextFieldTextAlignment
import com.umc.login.component.toCustomTextFieldUnderMessageProp
import com.umc.login.logic.state.PasswordValidationState

@Composable
fun PasswordForm(
    password: String,
    confirmPassword: String,
    state: PasswordValidationState,
    passwordPlaceholder: String,
    confirmPasswordPlaceholder: String,
    textAlignment: CustomTextFieldTextAlignment,
    isPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    isConfirmPasswordFieldShowing: Boolean,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onPasswordVisibilityChanged: (Boolean) -> Unit,
    onConfirmPasswordVisibilityChanged: (Boolean) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomTextFieldLabelScope(
            underMessageProp = state.passwordMessage?.toCustomTextFieldUnderMessageProp(),
        ) {
            CustomTextField(
                prop = CustomTextFieldProp(
                    value = password,
                    onValueChanged = onPasswordChanged,
                    placeholder = passwordPlaceholder,
                    textAlignment = textAlignment,
                    isVisible = isPasswordVisible,
                    tail = {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isPasswordVisible)
                                        R.drawable.ic_visibility_on
                                    else
                                        R.drawable.ic_visibility_off
                                ),
                                contentDescription = null,
                                tint = LocalColorTheme.current.grey[400],
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onPasswordVisibilityChanged(!isPasswordVisible) }
                            )
                        }
                    }
                )
            )
        }
        if (isConfirmPasswordFieldShowing) CustomTextFieldLabelScope(
            underMessageProp = state.confirmPasswordMessage?.toCustomTextFieldUnderMessageProp(),
        ) {
            CustomTextField(
                prop = CustomTextFieldProp(
                    value = confirmPassword,
                    onValueChanged = onConfirmPasswordChanged,
                    placeholder = confirmPasswordPlaceholder,
                    textAlignment = textAlignment,
                    isVisible = isConfirmPasswordVisible,
                    tail = {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isPasswordVisible)
                                        R.drawable.ic_visibility_on
                                    else
                                        R.drawable.ic_visibility_off
                                ),
                                contentDescription = null,
                                tint = LocalColorTheme.current.grey[400],
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        onConfirmPasswordVisibilityChanged(!isConfirmPasswordVisible)
                                    }
                            )
                        }
                    }
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordForm() {
    ThemeProvider {
        PasswordForm(
            password = "this_is_test",
            confirmPassword = "",
            state = PasswordValidationState.CONFIRM_PASSWORD_NOT_ENTERED,
            passwordPlaceholder = stringResource(id = R.string.password),
            confirmPasswordPlaceholder = stringResource(id = R.string.password_check),
            textAlignment = CustomTextFieldTextAlignment.START,
            isPasswordVisible = false,
            isConfirmPasswordVisible = false,
            isConfirmPasswordFieldShowing = true,
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onPasswordVisibilityChanged = {},
            onConfirmPasswordVisibilityChanged = {},
        )
    }
}