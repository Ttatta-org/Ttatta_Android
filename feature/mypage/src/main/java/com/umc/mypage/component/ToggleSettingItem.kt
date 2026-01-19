package com.umc.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider

@Composable
fun ToggleSettingItem(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    bottomContent: @Composable (() -> Unit)? = null,
    onSwitchOn: (() -> Unit)? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    lineHeight = 20.sp,
                    color = Color.Black,
                )
                if (description != null) {
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W400,
                        lineHeight = 20.sp,
                        color = LocalColorTheme.current.grey[600]
                    )
                }
            }

            CustomSwitch(
                checked = checked,
                onCheckedChange = {
                    onCheckedChange(it)
                    if (it) onSwitchOn?.invoke()
                }
            )
        }

        if (checked && bottomContent != null) {
            Spacer(modifier = Modifier.height(12.dp))
            bottomContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewToggleSettingItem() {
    ThemeProvider {
        ToggleSettingItem(
            title = "이것은 설정입니다.",
            description = "설정을 설정할 수 있습니다.",
            checked = true,
            onCheckedChange = {},
            bottomContent = {
                Text(
                    text = "test",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Yellow)
                )
            },
        )
    }
}