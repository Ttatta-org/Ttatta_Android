package com.umc.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.login.R
import com.umc.login.model.prop.CustomTextFieldProp
import com.umc.login.model.prop.CustomTextFieldUnderMessageProp

@Composable
fun CustomTextFieldLabelScope(
    underMessageProp: CustomTextFieldUnderMessageProp?,
    customTextField: @Composable () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        customTextField.invoke()
        // 하단 메시지
        underMessageProp?.let { prop ->
            Row {
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = prop.value,
                    fontSize = 13.sp,
                    letterSpacing = (-0.4).sp,
                    fontWeight = FontWeight.W700,
                    color = prop.color,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCustomTextField() {
    var text by remember { mutableStateOf("따따따따따따따") }

    ThemeProvider {
        CustomTextFieldLabelScope(
            underMessageProp = CustomTextFieldUnderMessageProp(
                value = "이것은 통과 메시지입니다",
                color = LocalColorTheme.current.positive,
            ),
            customTextField = {
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
            },
        )
    }
}