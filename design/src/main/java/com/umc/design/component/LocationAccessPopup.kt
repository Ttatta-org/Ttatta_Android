package com.umc.design.component

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.R
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider

@Composable
fun LocationAccessPopup(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalColorTheme.current
    val resources = LocalContext.current.resources

    val deco = remember {
        BitmapFactory
            .decodeResource(resources, R.raw.img_location_permission_deco)
            .asImageBitmap()
    }

    CustomPopup(
        title = "위치 정보 액세스",
        message = "발자취와 함께 하루를 기록하기 위해\n위치 권한을 허용해주세요",
        cancelText = "안 할래요",
        onDismiss = onDismiss,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                bitmap = deco,
                contentDescription = null,
                modifier = Modifier
                    .width(155.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colors.grey[100],
                        shape = RoundedCornerShape(13.dp),
                    )
                    .padding(22.dp),
            ) {
                Text(
                    text = buildAnnotatedString {
                        val normal = SpanStyle(
                            color = colors.grey[600],
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                        )

                        val highlighted = SpanStyle(
                            color = colors.primary[600],
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                        )

                        withStyle(normal) {
                            appendLine("따따는 사용자의 위치를")
                        }

                        withStyle(highlighted) {
                            append("알림과 일기 기록 목적")
                        }

                        withStyle(normal) {
                            append("으로만 활용해요.")
                        }
                    },
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            CustomButton(
                text = "네, 허용할래요",
                showShadow = false,
                onClick = onConfirm,
            )
        }
    }
}

@Preview
@Composable
fun PreviewLocationAccessPopup() {
    ThemeProvider {
        LocationAccessPopup(
            onConfirm = {},
            onDismiss = {},
        )
    }
}