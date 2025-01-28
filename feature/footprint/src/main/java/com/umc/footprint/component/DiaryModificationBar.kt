package com.umc.footprint.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.umc.footprint.R
import com.umc.design.R as Res

val diaryModificationBarShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)

data class DiaryModificationBarProp(
    val onModifyOptionClicked: () -> Unit,
    val onDeleteOptionClicked: () -> Unit,
    val onDismissed: () -> Unit,
)

@Composable
fun DiaryModificationBar(prop: DiaryModificationBarProp) {
    Dialog(
        onDismissRequest = prop.onDismissed,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        )
    ) {
        val dialog = LocalView.current.parent as DialogWindowProvider
        LaunchedEffect(key1 = Unit) { dialog.window.setDimAmount(0f) }

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = null,
                    onClick = prop.onDismissed,
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        shape = diaryModificationBarShape,
                        elevation = 16.dp
                    )
                    .background(
                        color = Color.White,
                        shape = diaryModificationBarShape,
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(
                        start = 32.dp,
                        end = 32.dp,
                        bottom = 32.dp,
                        top = 16.dp
                    )
                ) {
                    // 제목 라인
                    Image(
                        painter = painterResource(id = Res.drawable.ic_header_deco),
                        contentDescription = null,
                        modifier = Modifier.width(32.dp),
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .clickable { prop.onModifyOptionClicked() }
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(text = stringResource(id = R.string.modify))
                            }
                        }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .clickable { prop.onDeleteOptionClicked() }
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(text = stringResource(id = R.string.delete))
                            }
                        }
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
    DiaryModificationBar(
        prop = previewDiaryModificationBarProp
    )
}