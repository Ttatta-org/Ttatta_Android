package com.umc.footprint.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.google.android.material.shape.EdgeTreatment

@Composable
fun ShadowedImage(
    @DrawableRes id: Int,
    contentDescription: String?,
    size: DpSize,
    shadowColor: Color = Color.Black.copy(alpha = 0.25f),
    shadowBlur: Dp = 4.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 2.dp,
) {
    Box {
        Image(
            painter = painterResource(id = id),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = shadowColor),
            modifier = Modifier
                .size(size)
                .offset(x = offsetX, y = offsetY)
                .blur(radius = shadowBlur, edgeTreatment = BlurredEdgeTreatment.Unbounded),
        )
        Image(
            painter = painterResource(id = id),
            contentDescription = contentDescription,
            modifier = Modifier.size(size),
        )
    }
}
