package com.umc.challenge.modal

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.umc.design.component.CustomPopup
import com.umc.design.theme.ThemeProvider

data class PointGrantedCardDialogProp(
    val point: Int,
    val onDismissed: () -> Unit,
    val onGoToShopButtonClicked: () -> Unit,
)

@Composable
fun PointGrantedCardDialog(
    prop: PointGrantedCardDialogProp
) {
    CustomPopup(
        title = "${prop.point}포인트를 받았어요!",
        message = "또또와 뚜뚜를 꾸밀 아이템을\n구경하러 가볼까요?",
        confirmText = "구경하러 가기",
        onConfirm = prop.onGoToShopButtonClicked,
        onDismiss = prop.onDismissed,
    )
}

val previewPointGrantedCardDialogProp = PointGrantedCardDialogProp(
    point = 50,
    onDismissed = {},
    onGoToShopButtonClicked = {},
)

@Preview(showBackground = true)
@Composable
fun PreviewPointGrantedCardDialog() {
    ThemeProvider {
        PointGrantedCardDialog(
            prop = previewPointGrantedCardDialogProp
        )
    }
}