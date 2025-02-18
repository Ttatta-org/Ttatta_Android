package com.umc.challenge.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.umc.challenge.R
import com.umc.design.Primary300

data class PointGrantedCardDialogProp(
    val onDismissed: () -> Unit
)

@Composable
fun PointGrantedCardDialog(
    prop: PointGrantedCardDialogProp
) {
    Dialog(
        onDismissRequest = prop.onDismissed,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {
        val dialog = LocalView.current.parent as DialogWindowProvider
        LaunchedEffect(key1 = Unit) { dialog.window.setDimAmount(0f) }

        Box(
            contentAlignment = Alignment.Center,
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
                    onClick = prop.onDismissed,
                )
                .padding(32.dp)
        ) {
            ShadowBoxScope(
                radius = 4.dp,
                offset = DpOffset(0.dp, 4.dp),
                color = Color.Black.copy(alpha = 0.5f),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_point_granted_card),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

val previewPointGrantedCardDialogProp = PointGrantedCardDialogProp(
    onDismissed = {}
)

@Preview(showBackground = true)
@Composable
fun PreviewPointGrantedCardDialog() {
    PointGrantedCardDialog(
        prop = previewPointGrantedCardDialogProp
    )
}