package com.umc.footprint.model.prop

data class DiaryModificationBarProp(
    val onModifyOptionClicked: () -> Unit,
    val onDeleteOptionClicked: () -> Unit,
    val onDismissed: () -> Unit,
)