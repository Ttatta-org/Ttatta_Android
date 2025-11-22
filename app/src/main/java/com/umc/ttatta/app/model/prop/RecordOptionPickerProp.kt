package com.umc.ttatta.app.model.prop

import com.umc.design.character.AccessorySet

data class RecordOptionPickerProp(
    val userName: String,
    val accessories: AccessorySet,
    val onCameraOptionClicked: () -> Unit,
    val onGalleryOptionClicked: () -> Unit,
)