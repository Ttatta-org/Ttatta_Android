package com.umc.login.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.umc.login.logic.StateMessage

@Composable
fun StateMessage.toCustomTextFieldUnderMessageProp() = CustomTextFieldUnderMessageProp(
    value = stringResource(this.id),
    color = this.color
)