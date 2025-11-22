package com.umc.design.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.design.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    containerColor: Color = Color.White,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val bottom = WindowInsets.systemBars
        .asPaddingValues()
        .calculateBottomPadding()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetMaxWidth = 1024.dp,
        containerColor = Color.Transparent,
        scrimColor = Color.Transparent,
        dragHandle = null,
        contentWindowInsets = { WindowInsets(bottom = 0.dp) }
    ) {
        Column {
            // 그림자를 위한 여백
            Spacer(modifier = Modifier.height(16.dp))
            // 본문
            Column(
                modifier = Modifier
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                    )
                    .background(
                        color = containerColor,
                        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_header_deco),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    content.invoke(this)
                }
                Spacer(modifier = Modifier.height(bottom))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomBottomSheet() {
    CustomBottomSheet(
        onDismissRequest = {},
    ) {
        Text(text = "test", modifier = Modifier.height(100.dp))
    }
}