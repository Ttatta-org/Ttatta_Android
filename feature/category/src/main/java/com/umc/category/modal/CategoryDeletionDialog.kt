package com.umc.category.modal

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.umc.category.R
import com.umc.category.model.CategoryDeletionDialogProp
import com.umc.design.component.CustomPopup
import com.umc.design.theme.ThemeProvider

@Composable
fun CategoryDeletionDialog(
    prop: CategoryDeletionDialogProp,
) {
    CustomPopup(
        title = stringResource(id = R.string.delete_category),
        message = stringResource(id = R.string.delete_category_description),
        cancelText = stringResource(id = R.string.cancel),
        confirmText = stringResource(id = R.string.delete),
        onConfirm = prop.onConfirmed,
        onDismiss = prop.onDismissed,
    )
}

val previewCategoryDeletionDialogProp = CategoryDeletionDialogProp(
    onDismissed = {},
    onConfirmed = {},
)

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun PreviewCategoryDeletionDialog() {
    ThemeProvider {
        CategoryDeletionDialog(prop = previewCategoryDeletionDialogProp)
    }
}