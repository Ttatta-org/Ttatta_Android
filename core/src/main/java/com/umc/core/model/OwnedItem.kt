package com.umc.core.model

data class OwnedItem(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val characterType: CharacterType,
    val bodyPart: BodyPart,
    val isEquipped: Boolean,
)