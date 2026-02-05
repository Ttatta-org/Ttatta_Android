package com.umc.login.model.prop

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType

data class CustomTextFieldProp(
    val value: String,
    val onValueChanged: (String) -> Unit,
    val placeholder: String,
    val isVisible: Boolean = true,
    val isEditable: Boolean = true,
    val keyboardType: KeyboardType = KeyboardType.Companion.Unspecified,
    val tail: (@Composable () -> Unit)? = null,
)