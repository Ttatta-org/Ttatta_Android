package com.umc.core.model

data class UnownedItem(
    val id: Long,
    val name: String,
    val cost: Int,
    val imageUrl: String,
    val characterType: CharacterType,
    val bodyPart: BodyPart,
)
