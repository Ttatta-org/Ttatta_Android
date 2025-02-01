package com.umc.category.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InsetProviderDialog(
    onDismissed: () -> Unit,
    content: @Composable (@Composable () -> Unit) -> Unit,
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val imeHeight = WindowInsets.ime.asPaddingValues().calculateBottomPadding()

    Dialog(
        onDismissRequest = onDismissed,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        )
    ) {
        val dialog = LocalView.current.parent as DialogWindowProvider
        LaunchedEffect(key1 = Unit) { dialog.window.setDimAmount(0f) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = null,
                    onClick = onDismissed,
                )
        ) {
            content {
                Spacer(
                    modifier = Modifier
                        .animateContentSize()
                        .height(
                            statusBarHeight + imeHeight + if (WindowInsets.isImeVisible) 0.dp else navigationBarHeight
                        )
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewInsetProviderDialog() {
    InsetProviderDialog(
        onDismissed = {},
        content = { inset ->
            Column {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth()
                        .height(128.dp)
                ) {
                    Text(text = "테스트 화면")
                }
                inset()
            }
        }
    )
}