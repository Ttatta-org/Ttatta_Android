package com.umc.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.umc.design.Positive
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme
import com.umc.design.theme.ThemeProvider
import com.umc.login.R

enum class CustomTextFieldTextAlignment {
    START,
    CENTER,
    FLEX_CENTER,
}

data class CustomTextFieldProp(
    val value: String,
    val onValueChanged: (String) -> Unit,
    val placeholder: String,
    val textAlignment: CustomTextFieldTextAlignment = CustomTextFieldTextAlignment.START,
    val isVisible: Boolean = true,
    val isEditable: Boolean = true,
    val keyboardType: KeyboardType = KeyboardType.Unspecified,
    val tail: (@Composable () -> Unit) = { Spacer(modifier = Modifier.height(32.dp)) },
)

data class CustomTextFieldUnderMessageProp(
    val value: String,
    val color: Color,
)

@Composable
fun CustomTextField(
    prop: CustomTextFieldProp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val innerTextStyle = TextStyle(
        fontFamily = LocalFontTheme.current.font,
        fontWeight = FontWeight.W700,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.4).sp,
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        BasicTextField(
            value = prop.value,
            onValueChange = prop.onValueChanged,
            readOnly = !prop.isEditable,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = prop.keyboardType,
            ),
            textStyle = innerTextStyle,
            visualTransformation = if (prop.isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            interactionSource = interactionSource,
            modifier = Modifier.widthIn(min = 0.dp),
        ) { innerTextField ->
            // 텍스트
            @Composable
            fun CustomInnerTextField() {
                val arrangement =
                    if (prop.textAlignment == CustomTextFieldTextAlignment.START) Arrangement.Start
                    else Arrangement.Center

                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    // 플레이스홀더
                    if (prop.value.isEmpty() && !isFocused) {
                        Row(
                            horizontalArrangement = arrangement,
                            modifier = Modifier
                                .padding(horizontal = 23.dp)
                                .fillMaxWidth(),
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
                    }
                    // 입력 텍스트
                    Row(
                        horizontalArrangement = arrangement,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .width(
                                    width = max(
                                        with(density) {
                                            textMeasurer.measure(
                                                text = if (prop.isVisible) prop.value
                                                else "\u2022".repeat(prop.value.length),
                                                style = innerTextStyle,
                                            ).size.width.toDp()
                                        },
                                        2.dp,
                                    )
                                )
                                .graphicsLayer(clip = false),
                        ) {
                            innerTextField()
                        }
                    }
                }
            }

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
                if (prop.textAlignment == CustomTextFieldTextAlignment.FLEX_CENTER) Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.weight(1f),
                    ) {
                        CustomInnerTextField()
                    }
                    prop.tail.invoke()
                } else Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CustomInnerTextField()
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        prop.tail.invoke()
                    }
                }
            }
        }
    }
}

@Composable
fun CustomTextFieldLabelScope(
    underMessageProp: CustomTextFieldUnderMessageProp?,
    customTextField: @Composable () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        customTextField()
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

val previewCustomTextFieldProp = CustomTextFieldProp(
    value = "따따따따따따따",
    onValueChanged = {},
    placeholder = "입력해주세요",
    textAlignment = CustomTextFieldTextAlignment.FLEX_CENTER,
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

val previewCustomTextFieldUnderMessageProp = CustomTextFieldUnderMessageProp(
    value = "이것은 통과 메시지입니다",
    color = Color.Positive,
)

@Preview(showBackground = true)
@Composable
fun PreviewCustomTextField() {
    var text by remember { mutableStateOf(previewCustomTextFieldProp.value) }

    ThemeProvider {
        CustomTextFieldLabelScope(
            underMessageProp = previewCustomTextFieldUnderMessageProp,
            customTextField = {
                CustomTextField(
                    prop = previewCustomTextFieldProp.copy(
                        value = text,
                        onValueChanged = { text = it },
                    )
                )
            },
        )
    }
}