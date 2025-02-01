package com.umc.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.umc.category.component.CategoryDeletionDialogProp
import com.umc.category.component.CategoryManagementBarProp
import com.umc.category.component.CategoryModificationBarProp
import com.umc.core.model.CategoryInfo
import com.umc.design.CategoryColor

const val MAX_CATEGORY_NAME_LENGTH = 20

@Composable
fun CategoryApp(viewModel: CategoryViewModel) {
    var categoryNameInputFieldValue by remember { mutableStateOf("") }
    var selectedCategoryColor by remember { mutableStateOf<CategoryColor?>(null) }
    var selectedCategory by remember { mutableStateOf<CategoryInfo?>(null) }

    var modifiedCategoryNameInputFieldValue by remember { mutableStateOf("") }
    var modifiedCategoryColor by remember { mutableStateOf<CategoryColor?>(null) }

    var showCategoryManagementBar by remember { mutableStateOf(false) }
    var showCategoryModificationBar by remember { mutableStateOf(false) }
    var showCategoryDeletionDialog by remember { mutableStateOf(false) }

    CategoryScreen(
        maxCategoryNameLength = MAX_CATEGORY_NAME_LENGTH,
        categoryNameInputFieldValue = categoryNameInputFieldValue,
        selectedCategoryColor = selectedCategoryColor,
        categoryList = viewModel.categoryList.map { category ->
            CategoryListItemProp(
                name = category.name,
                color = category.color,
                onClicked = {
                    selectedCategory = category
                    showCategoryManagementBar = true
                    modifiedCategoryNameInputFieldValue = category.name
                    modifiedCategoryColor = category.color
                },
            )
        },
        categoryManagementBarProp = if (showCategoryManagementBar) {
            val category = selectedCategory!!
            CategoryManagementBarProp(
                onDismissed = {
                    selectedCategory = null
                    showCategoryManagementBar = false
                },
                onModifyOptionClicked = {
                    showCategoryManagementBar = false
                    showCategoryModificationBar = true
                },
                onDeleteCategoryOptionClicked = {
                    viewModel.deleteCategory(
                        id = category.id,
                        onSucceed = { showCategoryManagementBar = false },
                        onFailed = { showCategoryManagementBar = false }
                    )
                },
                onDeleteCategoryAndAllIncludedDiariesOptionClicked = {
                    showCategoryManagementBar = false
                    showCategoryDeletionDialog = true
                }
            )
        } else null,
        categoryModificationBarProp = if (showCategoryModificationBar) {
            val category = selectedCategory!!
            CategoryModificationBarProp(
                maxCategoryNameLength = MAX_CATEGORY_NAME_LENGTH,
                categoryNameInputFieldValue = modifiedCategoryNameInputFieldValue,
                selectedColor = modifiedCategoryColor,
                onDismissed = { showCategoryModificationBar = false },
                onCategoryNameInputFieldValueChanged = { modifiedCategoryNameInputFieldValue = it },
                onCategoryColorClicked = {
                    modifiedCategoryColor = if (modifiedCategoryColor == it) null else it
                },
                onDoneButtonClicked = {
                    viewModel.modifyCategory(
                        id = category.id,
                        name = modifiedCategoryNameInputFieldValue,
                        color = modifiedCategoryColor,
                        onSucceed = { showCategoryModificationBar = false },
                        onFailed = { showCategoryModificationBar = false }
                    )
                }
            )
        } else null,
        categoryDeletionDialogProp = if (showCategoryDeletionDialog) {
            val category = selectedCategory!!
            CategoryDeletionDialogProp(
                onDismissed = { showCategoryDeletionDialog = false },
                onConfirmed = {
                    viewModel.deleteCategoryAndAllIncludedDiaries(
                        id = category.id,
                        onSucceed = { showCategoryDeletionDialog = false },
                        onFailed = { showCategoryDeletionDialog = false },
                    )
                }
            )
        } else null,
        onCategoryNameInputFieldValueChanged = {
            if (it.length <= MAX_CATEGORY_NAME_LENGTH) categoryNameInputFieldValue = it
        },
        onCategoryColorClicked = {
            selectedCategoryColor = if (selectedCategoryColor == it) null else it
        },
        onDoneButtonClicked = {
            viewModel.createCategory(
                name = categoryNameInputFieldValue,
                color = selectedCategoryColor,
                onSucceed = {
                    categoryNameInputFieldValue = ""
                    selectedCategoryColor = null
                },
                onFailed = {}
            )
        },
    )
}