package com.umc.category.model

data class CategoryDeletionDialogProp(
    val onDismissed: () -> Unit,
    val onConfirmed: () -> Unit,
)