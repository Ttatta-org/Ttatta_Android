package com.umc.design.character

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

private val totalOffset = Offset(0f, 10f)

@Composable
fun rememberCharacterPainter(
    accessorySet: AccessorySet
): Painter {
    var painter by remember { mutableStateOf<RecursivePainter?>(null) }
    var prevSet by remember { mutableStateOf(accessorySet) }

    return if (prevSet != accessorySet) {
        prevSet = accessorySet
        getNewCharacterPainter(accessorySet).apply { painter = this }
    } else {
        painter ?: run {
            getNewCharacterPainter(accessorySet).apply { painter = this }
        }
    }
}

@Composable
private fun getNewCharacterPainter(
    accessorySet: AccessorySet,
): RecursivePainter {
    val (ttottoRender, ttuttuRender) = listOf(
        CharacterType.TTOTTO,
        CharacterType.TTUTTU,
    ).map { characterType ->

        val (headItemRender, torsoItemRender) = listOf(
            BodyPart.HEAD,
            BodyPart.TORSO,
        ).map { bodyPart ->
            accessorySet.firstOrNull {
                it.characterType == characterType && it.bodyPart == bodyPart
            }?.let { accessory ->
                ChildRenderInfo(
                    offset = accessory.offset,
                    info = object : RenderInfo {
                        override val painter = painterResource(id = accessory.res)
                        override val originalSize = accessory.size
                    }
                )
            }
        }

        val handRender = ChildRenderInfo(
            offset = characterType.handOffset,
            info = object : RenderInfo {
                override val painter = painterResource(id = characterType.handRes)
                override val originalSize = characterType.handSize
            }
        )

        ChildRenderInfo(
            offset = characterType.offset + totalOffset,
            info = object : RenderInfo {
                override val painter = painterResource(id = characterType.bodyRes)
                override val originalSize = characterType.size
                override val children = listOfNotNull(
                    headItemRender,
                    torsoItemRender,
                    handRender,
                )
            }
        )
    }

    return RecursivePainter.create(
        root = object : RenderInfo {
            override val originalSize = characterSize
            override val children = listOf(ttuttuRender, ttottoRender)
        },
    )
}