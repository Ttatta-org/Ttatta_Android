package com.umc.footprint.model.prop

data class CategorySelectionBarProp(
    val userName: String,
    val itemProps: List<CategoryItemProp>,
    val onNewCategoryButtonClicked: () -> Unit,
    val onDismiss: () -> Unit,
)