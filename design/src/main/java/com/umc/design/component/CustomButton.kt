package com.umc.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme

@Composable
fun CustomButton(
    text: String? = null,
    showShadow: Boolean = true,
    isEnabled: Boolean = true,
    colors: ButtonColors = ButtonColors(
        containerColor = LocalColorTheme.current.primary[400],
        contentColor = Color.White,
        disabledContainerColor = LocalColorTheme.current.primary[200],
        disabledContentColor = Color.White,
    ),
    border: BorderStroke? = null,
    onClick: () -> Unit,
    content: (@Composable () -> Unit)? = null,
) {
    val shape = remember { RoundedCornerShape(15.dp) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (showShadow) it.shadow(
                    elevation = 4.dp,
                    shape = shape,
                    spotColor = Color.Black.copy(alpha = 0.4f),
                ) else it
            }
            .background(
                color = if (isEnabled) colors.containerColor else colors.disabledContainerColor,
                shape = shape,
            )
            .let {
                if (border != null) it.border(border = border, shape = shape)
                else it
            }
            .clip(shape)
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = { if (isEnabled) onClick() },
            ),
    ) {
        if (content != null) {
            content.invoke()
        } else if (text != null) {
            Text(
                text = text,
                fontFamily = LocalFontTheme.current.font,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.W700,
                letterSpacing = (-0.4).sp,
                color = if (isEnabled) colors.contentColor else colors.disabledContentColor,
                modifier = Modifier.padding(vertical = 13.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewCustomButton() {
    CustomButton(
        text = "로그인",
        onClick = {},
    )
}