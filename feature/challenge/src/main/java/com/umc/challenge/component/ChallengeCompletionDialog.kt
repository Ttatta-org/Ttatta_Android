package com.umc.challenge.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.umc.challenge.R
import com.umc.design.component.CustomPopup

data class ChallengeCompletionDialogProp(
    val onDismissed: () -> Unit,
    val onConfirmed: () -> Unit,
)

@Composable
fun ChallengeCompletionDialog(
    prop: ChallengeCompletionDialogProp
) {
    CustomPopup(
        title = stringResource(id = R.string.challenge_completion_dialog_content_1),
        message = stringResource(id = R.string.challenge_completion_dialog_content_2),
        confirmText = "기록하러 가기",
        onConfirm = prop.onConfirmed,
        onDismiss = prop.onDismissed,
    )
}

val previewChallengeCompletionDialogProp = ChallengeCompletionDialogProp(
    onDismissed = {},
    onConfirmed = {},
)

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun PreviewCategoryDeletionDialog() {
    ChallengeCompletionDialog(prop = previewChallengeCompletionDialogProp)
}