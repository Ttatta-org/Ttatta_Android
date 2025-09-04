package com.umc.ttatta.model.prop

import com.umc.ttatta.model.prop.RecordOptionPickerProp

data class CenterButtonProp(
    val recordOptionPickerProp: RecordOptionPickerProp,
    val onDismissed: () -> Unit,
)