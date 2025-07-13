package com.umc.footprint.model.prop

data class DiaryModificationModeProp(
    val contentValue: String,
    val onContentValueChanged: (String) -> Unit,
    val onModificationDone: () -> Unit,
)