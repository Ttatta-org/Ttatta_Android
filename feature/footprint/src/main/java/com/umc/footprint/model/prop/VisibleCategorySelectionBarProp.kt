package com.umc.footprint.model.prop

data class VisibleCategorySelectionBarProp(
    val isVisible: Boolean,
    val prop: CategorySelectionBarProp,
    val onDismissed: () -> Unit,
)
