package com.umc.design.character

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

val characterSize = Size(326f, 230f)

@Composable
fun CharacterView(
    accessorySet: AccessorySet,
    width: Dp,
    height: Dp = width * characterSize.height / characterSize.width
) {
    val painter = rememberCharacterPainter(accessorySet = accessorySet)
    val size = remember(width, height) { DpSize(width = width, height = height) }

    Canvas(
        modifier = Modifier.size(size)
    ) {
        with(painter) { draw(size = size.toSize()) }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCharacterView() {
    val accessorySet = remember {
        AccessorySet.create(
            Accessory.TTOTTO_COZY_MUFFLER,
            Accessory.TTUTTU_THREE_COLOR_BALLOONS,
            Accessory.TTUTTU_HAT,
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CharacterView(
            accessorySet = accessorySet,
            width = 240.dp,
            height = 480.dp
        )
    }
}