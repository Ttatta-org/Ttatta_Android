package com.umc.ttatta.app.model.prop

data class CenterButtonProp(
    val recordOptionPickerProp: RecordOptionPickerProp,
    val onDismissed: () -> Unit,
)