package com.umc.footprint.modal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.component.CustomBottomSheet
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.R
import com.umc.footprint.model.prop.DiaryModificationBarProp

@Composable
fun DiaryModificationBar(prop: DiaryModificationBarProp) {
    CustomBottomSheet(
        onDismissRequest = prop.onDismissed,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 32.dp),
        ) {
            listOf(
                stringResource(id = R.string.modify) to prop.onModifyOptionClicked,
                stringResource(id = R.string.delete) to prop.onDeleteOptionClicked,
            ).forEach { (text, onClicked) ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable { onClicked() }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 12.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = text,
                            fontWeight = FontWeight.W600,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            color = LocalColorTheme.current.grey[700],
                        )
                    }
                }
            }
        }
    }
}

val previewDiaryModificationBarProp = DiaryModificationBarProp(
    onModifyOptionClicked = {},
    onDeleteOptionClicked = {},
    onDismissed = {},
)

@Preview(showBackground = true)
@Composable
fun PreviewDiaryModificationBar() {
    ThemeProvider {
        DiaryModificationBar(
            prop = previewDiaryModificationBarProp
        )
    }
}