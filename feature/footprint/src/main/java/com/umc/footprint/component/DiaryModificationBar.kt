package com.umc.footprint.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.Grey500
import com.umc.footprint.R
import com.umc.design.R as Res

val diaryModificationBarShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)

data class DiaryModificationBarProp(
    val onModifyOptionClicked: () -> Unit,
    val onDeleteOptionClicked: () -> Unit,
    val onDismissed: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryModificationBar(prop: DiaryModificationBarProp) {
    val bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

    ModalBottomSheet(
        onDismissRequest = prop.onDismissed,
        shape = RectangleShape,
        sheetMaxWidth = 1024.dp,
        containerColor = Color.Transparent,
        scrimColor = Color.Transparent,
        dragHandle = {},
        contentWindowInsets = { WindowInsets(bottom = 0.dp) }
    ) {
        Column {
            // 그림자를 위한 여백
            Spacer(modifier = Modifier.height(16.dp))
            // 본문
            Column(
                modifier = Modifier
                    .shadow(
                        elevation = 16.dp,
                        shape = diaryModificationBarShape,
                    )
                    .background(
                        color = Color.White,
                        shape = diaryModificationBarShape,
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = Res.drawable.ic_header_deco),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
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
                                        .padding(vertical = 4.dp, horizontal = 8.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = text,
                                        fontWeight = FontWeight.W600,
                                        fontSize = 15.sp,
                                        lineHeight = 20.sp,
                                        color = Color.Grey500,
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(bottom))
            }
        }
    }
}

val previewDiaryModificationBarProp = DiaryModificationBarProp(
    onModifyOptionClicked = {},
    onDeleteOptionClicked = {},
    onDismissed = {},
)

@Preview(showBackground = true, widthDp = 500, heightDp = 800)
@Composable
fun PreviewDiaryModificationBar() {
    DiaryModificationBar(
        prop = previewDiaryModificationBarProp
    )
}