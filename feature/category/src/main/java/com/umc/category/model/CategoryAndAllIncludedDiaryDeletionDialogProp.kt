package com.umc.category.model

data class CategoryAndAllIncludedDiaryDeletionDialogProp(
    val onDismissed: () -> Unit,
    val onConfirmed: () -> Unit,
)