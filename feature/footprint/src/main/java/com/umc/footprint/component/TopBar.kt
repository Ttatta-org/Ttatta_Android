package com.umc.footprint.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.footprint.R

@Composable
fun TopBar() {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(statusBarHeight))
            Box(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .size(32.dp)
                        .blur(radius = 20.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                ) {
                    drawCircle(
                        color = Color.White,
                        radius = 16.dp.toPx(),
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewTopBar() {
    TopBar()
}