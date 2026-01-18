package com.umc.challenge.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.challenge.R
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider

enum class ChallengeState(
    val borderColor: @Composable () -> Color,
    val backgroundColor: @Composable () -> Color,
    val titleColor: @Composable () -> Color,
    val descriptionColor: @Composable () -> Color,
    @field:DrawableRes val icon: Int
) {
    IN_PROGRESS(
        borderColor = { LocalColorTheme.current.primary[200] },
        backgroundColor = { Color.White },
        titleColor = { LocalColorTheme.current.grey[700] },
        descriptionColor = { LocalColorTheme.current.grey[600] },
        icon = R.drawable.ic_no_stamp,
    ),
    COMPLETED(
        borderColor = { LocalColorTheme.current.primary[200] },
        backgroundColor = { LocalColorTheme.current.primary[100] },
        titleColor = { LocalColorTheme.current.primary[400] },
        descriptionColor = { LocalColorTheme.current.primary[400] },
        icon = R.drawable.ic_complete_stamp,
    ),
    FAILED(
        borderColor = { LocalColorTheme.current.grey[400] },
        backgroundColor = { LocalColorTheme.current.grey[100] },
        titleColor = { LocalColorTheme.current.grey[600] },
        descriptionColor = { LocalColorTheme.current.grey[600] },
        icon = R.drawable.ic_fail_stamp,
    ),
}

data class ChallengeItemProp(
    val title: String,
    val content: String,
    val state: ChallengeState,
    val onClicked: (() -> Unit)?,
)

@Composable
fun ChallengeItem(
    prop: ChallengeItemProp,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = prop.state.backgroundColor.invoke(),
                shape = RoundedCornerShape(16.dp),
            )
            .border(
                width = 1.dp,
                color = prop.state.borderColor.invoke(),
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(
                enabled = prop.onClicked != null,
                indication = null,
                interactionSource = null,
                onClick = { prop.onClicked?.invoke() },
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 13.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp, bottom = 1.dp)
            ) {
                Text(
                    text = prop.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W700,
                    color = prop.state.titleColor.invoke(),
                    maxLines = 1,
                    lineHeight = 18.sp,
                )
                Text(
                    text = prop.content,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    color = prop.state.descriptionColor.invoke(),
                    lineHeight = 13.sp,
                )
            }
            Image(
                painter = painterResource(id = prop.state.icon),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(40.dp)
            )
        }
    }
}

val previewChallengeItemPropList = listOf(
    ChallengeItemProp(
        title = "1시간 공부하기",
        content = "CS 요약 정리 + 백준 2문제",
        state = ChallengeState.IN_PROGRESS,
        onClicked = {},
    ),
    ChallengeItemProp(
        title = "물 하루 3잔 마시기",
        content = "점심 전 1잔, 오후에 2잔",
        state = ChallengeState.COMPLETED,
        onClicked = {},
    ),
    ChallengeItemProp(
        title = "도서관 가기",
        content = "3층 열람실 2시간",
        state = ChallengeState.FAILED,
        onClicked = {},
    ),
)

@Preview
@Composable
fun PreviewChallengeItem() {
    ThemeProvider {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            previewChallengeItemPropList.forEach {
                ChallengeItem(
                    prop = it
                )
            }
        }
    }
}