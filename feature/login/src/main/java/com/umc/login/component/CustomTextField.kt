package com.umc.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.login.R

@Composable
fun LoginInputTextField( // 로그인 화면 텍스트필드
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean,
    passwordVisible: Boolean = false,
    onPasswordToggleClick: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            ),
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { onPasswordToggleClick?.invoke() }) {
                        Image(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                            ),
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                }
            } else null,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
            modifier = Modifier
                .width(310.dp)
                .height(52.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colorResource(R.color.gray_500),
                unfocusedIndicatorColor = colorResource(R.color.gray_500),
                cursorColor = Color.Black
            )
        )
        // 값이 비어있고 포커스가 없을 때만 플레이스홀더 표시
        if (value.isEmpty() && !isFocused) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.gray_500),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}