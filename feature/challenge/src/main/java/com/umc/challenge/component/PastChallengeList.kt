package com.umc.challenge.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.challenge.R
import com.umc.design.theme.LocalColorTheme

@Composable
fun SuccessPastChallengeList(
    selected: Boolean,
    onSelect: () -> Unit,
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 5.dp)
            .background(LocalColorTheme.current.secondary[100]),
        verticalAlignment = Alignment.CenterVertically
    ) {

        CustomRadioButton(
            selected = selected,
            onClick = onSelect
        )

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            modifier = Modifier
                .border(1.dp, LocalColorTheme.current.primary[200], RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(LocalColorTheme.current.primary[100])
                .padding(horizontal = 20.dp, vertical = 15.dp)
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                // 챌린지 제목
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalColorTheme.current.primary[400]
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 챌린지 설명
                Text(
                    text = description,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.primary[400]
                )
            }

            // 성공/실패 여부 스탬프
            Image(
                painter = painterResource(R.drawable.ic_complete_stamp),
                contentDescription = null,
                modifier = Modifier.size(45.dp)
            )
        }
    }
}

@Composable
fun FailedPastChallengeList(
    selected: Boolean,
    onSelect: () -> Unit,
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 5.dp)
            .background(LocalColorTheme.current.secondary[100]),
        verticalAlignment = Alignment.CenterVertically
    ) {

        CustomRadioButton(
            selected = selected,
            onClick = onSelect
        )

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            modifier = Modifier
                .border(1.dp, LocalColorTheme.current.grey[400], RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(LocalColorTheme.current.grey[100])
                .padding(horizontal = 20.dp, vertical = 15.dp)
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                // 챌린지 제목
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalColorTheme.current.grey[600]
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 챌린지 설명
                Text(
                    text = description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.grey[600]
                )
            }

            // 성공/실패 여부 스탬프
            Image(
                painter = painterResource(R.drawable.ic_fail_stamp),
                contentDescription = null,
                modifier = Modifier.size(45.dp)
            )
        }
    }
}

@Composable
fun CustomRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // 바깥 원
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color.Transparent)
                .border(
                    width = 1.dp,
                    color = LocalColorTheme.current.primary[300],
                    shape = CircleShape
                )
        )
        // 안쪽 점
        if (selected) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(LocalColorTheme.current.primary[300], CircleShape)
            )
        }
    }
}

@Preview
@Composable
fun PreviewSuccessPastChallengeList() {
    SuccessPastChallengeList(
        selected = false,
        onSelect = {},
        title = "헬스장 가기",
        description = "오늘은 하체 뿌시기",
    )
}

@Preview
@Composable
fun PreviewFailedPastChallengeList() {
    FailedPastChallengeList(
        selected = false,
        onSelect = {},
        title = "도서관 가기",
        description = "시험공부 및 과제",
    )
}