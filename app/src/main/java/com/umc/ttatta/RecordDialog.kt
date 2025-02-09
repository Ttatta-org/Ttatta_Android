package com.umc.ttatta

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.umc.design.Primary300
import com.umc.design.Primary500

@Composable
fun RecordDialog(
    userName: String,
    onDismissed: () -> Unit,
    onCameraOptionClicked: () -> Unit,
    onGalleryOptionClicked: () -> Unit,
) {

    Dialog(
        onDismissRequest = onDismissed,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {
        val dialog = LocalView.current.parent as DialogWindowProvider
        LaunchedEffect(key1 = Unit) { dialog.window.setDimAmount(0f) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        0f to Color.White,
                        1f to Color.Primary300
                    ),
                    alpha = 0.2f,
                )
                .clickable(
                    indication = null,
                    interactionSource = null,
                    onClick = onDismissed,
                )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = buildAnnotatedString {
                            appendLine(userName + stringResource(id = R.string.record_top_line_message))
                            appendLine(stringResource(id = R.string.record_body1_message))
                            appendLine(stringResource(id = R.string.record_body2_message))
                        },
                        color = Color.Primary500,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Image(
                        painter = painterResource(id = R.drawable.img_ttotto_ttuttu),
                        contentDescription = null,
                        modifier = Modifier.width(240.dp)
                    )
                    IconButton(
                        onClick = onCameraOptionClicked
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.btn_camera),
                            contentDescription = null,
                            modifier = Modifier.width(180.dp)
                        )
                    }
                    IconButton(
                        onClick = onGalleryOptionClicked
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.btn_gallary),
                            contentDescription = null,
                            modifier = Modifier.width(180.dp)
                        )
                    }
                    IconButton(
                        onClick = onDismissed
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.btn_cancel_record),
                            contentDescription = null,
                            modifier = Modifier.width(48.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecordDialog() {
    RecordDialog(
        userName = "test",
        onDismissed = {},
        onCameraOptionClicked = {},
        onGalleryOptionClicked = {},
    )
}