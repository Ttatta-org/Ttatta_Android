package com.umc.category.model

data class CategoryManagementBarProp(
    val onDismissed: () -> Unit,
    val onModifyOptionClicked: () -> Unit,
    val onDeleteCategoryOptionClicked: () -> Unit,
    val onDeleteCategoryAndAllIncludedDiariesOptionClicked: () -> Unit,
)