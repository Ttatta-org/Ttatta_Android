package com.umc.design.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.umc.design.theme.LocalColorTheme

@Composable
fun LoadingModal() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )
    ) {
        CircularProgressIndicator(
            color = LocalColorTheme.current.primary[500],
            modifier = Modifier.size(40.dp),
        )
    }
}