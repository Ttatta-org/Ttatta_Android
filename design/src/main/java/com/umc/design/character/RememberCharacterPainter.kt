package com.umc.design.character

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

val characterSize = Size(326f, 230f)
private val totalOffset = Offset(0f, 10f)

@Composable
fun rememberCharacterPainter(
    accessorySet: AccessorySet,
    characterType: CharacterType? = null,
): Painter {
    var painter by remember { mutableStateOf<RecursivePainter?>(null) }
    var prevSet by remember { mutableStateOf(accessorySet) }

    return if (prevSet != accessorySet) {
        prevSet = accessorySet
        getNewCharacterPainter(
            accessorySet = accessorySet,
            characterType = characterType,
        ).apply { painter = this }
    } else {
        painter ?: run {
            getNewCharacterPainter(
                accessorySet = accessorySet,
                characterType = characterType,
            ).apply { painter = this }
        }
    }
}

@Composable
private fun getCharacterRenderInfo(
    accessorySet: AccessorySet,
    characterType: CharacterType,
): RenderInfo {
    val (headItemRender, torsoItemRender) = listOf(
        BodyPart.HEAD,
        BodyPart.TORSO,
    ).map { bodyPart ->
        accessorySet.firstOrNull {
            it.characterType == characterType && it.bodyPart == bodyPart
        }?.let { accessory ->
            ChildRenderInfo(
                offset = accessory.offset,
                info = object: RenderInfo {
                    override val painter = painterResource(id = accessory.res)
                    override val originalSize = accessory.size
                },
            )
        }
    }

    val handRender = ChildRenderInfo(
        offset = characterType.handOffset,
        info = object: RenderInfo {
            override val painter = painterResource(id = characterType.handRes)
            override val originalSize = characterType.handSize
        },
    )

    return object: RenderInfo {
        override val painter = painterResource(id = characterType.bodyRes)
        override val originalSize = characterType.size
        override val children = listOfNotNull(
            headItemRender,
            torsoItemRender,
            handRender,
        )
    }
}

@Composable
private fun getNewCharacterPainter(
    accessorySet: AccessorySet,
    characterType: CharacterType?,
): RecursivePainter {
    val characters = if (characterType == null) listOf(
        CharacterType.TTUTTU to CharacterType.TTUTTU.offset,
        CharacterType.TTOTTO to CharacterType.TTOTTO.offset,
    ) else listOf(
        characterType to Offset.Zero,
    )

    val renders = characters.map { (characterType, offset) ->
        ChildRenderInfo(
            offset = offset + totalOffset,
            info = getCharacterRenderInfo(
                accessorySet = accessorySet,
                characterType = characterType,
            ),
        )
    }

    return RecursivePainter.create(
        root = object: RenderInfo {
            override val originalSize = characterType?.size ?: characterSize
            override val children = renders
        },
    )
}