package com.umc.footprint.model.prop

import androidx.compose.ui.unit.Dp

data class CategorySelectionBarProp(
    val userName: String,
    val itemProps: List<CategoryItemProp>,
    val onNewCategoryButtonClicked: () -> Unit,
    val onHeightChanged: (Dp) -> Unit,
    val onDismiss: () -> Unit,
)