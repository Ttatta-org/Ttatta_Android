package com.umc.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.umc.design.R
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider

@Composable
fun CustomPopup(
    title: String? = null,
    message: String? = null,
    cancelText: String = "취소",
    confirmText: String = "확인",
    onDismiss: () -> Unit,
    onConfirm: (() -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .padding(25.dp)
                .widthIn(max = 340.dp)
                .background(color = Color.White, shape = RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 22.dp, horizontal = 27.dp)
                    .heightIn(min = 170.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_header_deco),
                    contentDescription = null,
                    modifier = Modifier.height(16.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (title != null) Text(
                        text = title,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.W700,
                    )
                    if (message != null) Text(
                        text = message,
                        fontSize = 13.sp,
                        color = LocalColorTheme.current.grey[700],
                        fontWeight = FontWeight.W400,
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(13.dp),
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, LocalColorTheme.current.primary[200]),
                        contentPadding = PaddingValues(vertical = 13.dp),
                    ) {
                        Text(
                            text = cancelText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W600,
                            color = LocalColorTheme.current.primary[200],
                        )
                    }
                    if (onConfirm != null) Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = LocalColorTheme.current.primary[400]),
                        shape = RoundedCornerShape(13.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 13.dp)
                    ) {
                        Text(
                            text = confirmText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W600,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true, backgroundColor = 0xFFEFEFEF)
fun DialogPreview() {
    ThemeProvider {
        CustomPopup(
            title = "로그아웃 하시겠습니까?",
            message = "언제든 따따와 함께하고 싶다면 찾아와주세요!",
            onDismiss = {},
            onConfirm = null,
        )
    }
}