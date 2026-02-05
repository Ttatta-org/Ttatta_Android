package com.umc.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme
import com.umc.design.theme.ThemeProvider
import com.umc.login.R
import com.umc.login.model.prop.CustomTextFieldProp
import com.umc.login.model.prop.CustomTextFieldUnderMessageProp

@Composable
fun CustomTextField(
    prop: CustomTextFieldProp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        value = prop.value,
        onValueChange = prop.onValueChanged,
        readOnly = !prop.isEditable,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = prop.keyboardType,
        ),
        textStyle = TextStyle(
            fontFamily = LocalFontTheme.current.font,
            fontWeight = FontWeight.W700,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = (-0.4).sp,
        ),
        visualTransformation = if (prop.isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        interactionSource = interactionSource,
        modifier = Modifier.fillMaxWidth(),
    ) { innerTextField ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = LocalColorTheme.current.primary[300],
                    shape = RoundedCornerShape(15.dp),
                )
                .background(
                    color = LocalColorTheme.current.primary[100].copy(alpha = 0.4f),
                    shape = RoundedCornerShape(15.dp),
                )
                .heightIn(min = 46.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .weight(1f)
                ) {
                    // 플레이스홀더
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (prop.value.isEmpty() && !isFocused) 1f else 0f),
                    ) {
                        Text(
                            text = prop.placeholder,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.W400,
                            color = LocalColorTheme.current.grey[400],
                            maxLines = 1,
                        )
                    }
                    // 입력 텍스트
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = if (prop.tail == null) 20.dp else 0.dp)
                    ) {
                        innerTextField.invoke()
                    }
                }
                prop.tail?.invoke()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCustomTextField() {
    var text by remember { mutableStateOf("따따따따따따따") }

    ThemeProvider {
        CustomTextField(
            prop = CustomTextFieldProp(
                value = text,
                onValueChanged = { text = it },
                placeholder = "입력해주세요",
                isVisible = true,
                tail = {
                    IconButton(
                        onClick = {},
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_visibility_off),
                            contentDescription = null,
                            tint = LocalColorTheme.current.grey[400],
                        )
                    }
                },
            )
        )
    }
}