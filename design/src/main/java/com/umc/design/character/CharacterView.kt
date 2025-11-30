package com.umc.design.character

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun CharacterView(
    accessorySet: AccessorySet,
    characterType: CharacterType? = null,
    width: Dp,
    height: Dp = (characterType?.size ?: characterSize).let { width * it.height / it.width },
) {
    val size = remember(width, height) { DpSize(width = width, height = height) }
    val painter = rememberCharacterPainter(
        accessorySet = accessorySet,
        characterType = characterType,
    )

    Canvas(
        modifier = Modifier.size(size)
    ) {
        with(painter) { draw(size = size.toSize()) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
fun PreviewCharacterView() {
    var accessorySet by remember { mutableStateOf(AccessorySet.create()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f),
        ) {
            CharacterView(
                accessorySet = accessorySet,
                width = 240.dp,
            )
        }
        Box(
            modifier = Modifier
                .heightIn(max = 300.dp)
                .verticalScroll(rememberScrollState())
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 16.dp,
                    alignment = Alignment.CenterHorizontally,
                ),
            ) {
                Accessory.entries.forEach { accessory ->
                    Button(
                        onClick = {
                            if (accessorySet.contains(accessory)) accessorySet -= accessory
                            else accessorySet = accessorySet.plusReplacingConflict(accessory)
                        },
                    ) {
                        Text(text = accessory.title)
                    }
                }
            }
        }
    }
}