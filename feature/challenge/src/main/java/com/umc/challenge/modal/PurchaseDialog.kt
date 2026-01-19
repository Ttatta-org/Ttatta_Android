package com.umc.challenge.modal

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.umc.challenge.R
import com.umc.challenge.util.hasFinalConsonant
import com.umc.design.component.CustomPopup
import com.umc.design.theme.ThemeProvider

data class PurchaseDialogProp(
    val itemName: String,
    val onDismissed: () -> Unit,
    val onPurchase: () -> Unit,
)

@Composable
fun PurchaseDialog(
    prop: PurchaseDialogProp,
) {
    CustomPopup(
        title = "아이템 구매",
        message = "${prop.itemName}${if (prop.itemName.hasFinalConsonant()) "을" else "를"} ${
            stringResource(id = R.string.purchase_dialog_content)
        }",
        confirmText = "구매",
        onDismiss = prop.onDismissed,
        onConfirm = prop.onPurchase,
    )
}

val previewPurchaseDialogProp = PurchaseDialogProp(
    itemName = "아이템 이름",
    onDismissed = {},
    onPurchase = {},
)

@Preview
@Composable
fun PreviewPurchaseDialog() {
    ThemeProvider {
        PurchaseDialog(prop = previewPurchaseDialogProp)
    }
}