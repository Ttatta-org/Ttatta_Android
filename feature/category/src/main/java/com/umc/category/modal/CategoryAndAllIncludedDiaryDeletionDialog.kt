package com.umc.category.modal

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.umc.category.R
import com.umc.design.component.CustomPopup
import com.umc.design.theme.ThemeProvider

data class CategoryAndAllIncludedDiaryDeletionDialogProp(
    val onDismissed: () -> Unit,
    val onConfirmed: () -> Unit,
)

@Composable
fun CategoryAndAllIncludedDiaryDeletionDialog(
    prop: CategoryAndAllIncludedDiaryDeletionDialogProp,
) {
    CustomPopup(
        title = stringResource(id = R.string.delete_all),
        message = stringResource(id = R.string.delete_all_description),
        cancelText = stringResource(id = R.string.cancel),
        confirmText = stringResource(id = R.string.delete),
        onConfirm = prop.onConfirmed,
        onDismiss = prop.onDismissed,
    )
}

val previewCategoryAndAllIncludedDiaryDeletionDialogProp = CategoryAndAllIncludedDiaryDeletionDialogProp(
    onDismissed = {},
    onConfirmed = {},
)

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun PreviewCategoryAndAllIncludedDiaryDeletionDialog() {
    ThemeProvider {
        CategoryAndAllIncludedDiaryDeletionDialog(prop = previewCategoryAndAllIncludedDiaryDeletionDialogProp)
    }
}