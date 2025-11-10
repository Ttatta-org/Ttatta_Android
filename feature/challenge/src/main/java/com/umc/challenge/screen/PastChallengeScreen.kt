package com.umc.challenge.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.challenge.component.FailedPastChallengeList
import com.umc.challenge.component.PastChallengeTopBar
import com.umc.challenge.component.PastChallengeTopBarProp
import com.umc.challenge.component.SuccessPastChallengeList
import com.umc.design.theme.LocalColorTheme

data class PastChallengeScreenTopBarProp(
    val onHeightChanged: (Dp) -> Unit,
    val onBackIconClicked: () -> Unit,
)

@Composable
fun PastChallengeScreen(
    topBarProp: PastChallengeScreenTopBarProp,
) {
    var topBarHeight by remember { mutableStateOf(0.dp) }

    // 예시 데이터
    val challengeList = listOf(
        Triple(true, "헬스장 가기", "오늘은 하체 뿌시기"),
        Triple(false, "영어 단어 30개 외우기", "오늘 분량은 꼭!"),
        Triple(true, "책 20페이지 읽기", "독서습관 챌린지"),
        Triple(false, "영어 단어 30개 외우기", "오늘 분량은 꼭!"),
        Triple(true, "책 20페이지 읽기", "독서습관 챌린지"),
        Triple(false, "물 2L 마시기", "건강 챌린지"),
        Triple(true, "헬스장 가기", "오늘은 하체 뿌시기"),
        Triple(false, "물 2L 마시기", "건강 챌린지")
    )

    // 지금 선택된 아이템 인덱스 (없으면 -1)
    var selectedIndex by remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.secondary[100])
    ) {
        // Top Bar
        PastChallengeTopBar(
            prop = PastChallengeTopBarProp(
                onHeightChanged = { topBarHeight = it },
                onBackIconClicked = topBarProp.onBackIconClicked
            )
        )

        // 지난 챌린지 리스트
        LazyColumn(
            modifier = Modifier.weight(1f)   // 아래 버튼이 항상 보이게 하려고 weight 줌
        ) {
            itemsIndexed(challengeList) { index, item ->
                val (isSuccess, title, desc) = item

                if (isSuccess) {
                    SuccessPastChallengeList(
                        selected = selectedIndex == index,
                        onSelect = { selectedIndex = index },
                        title = title,
                        description = desc
                    )
                } else {
                    FailedPastChallengeList(
                        selected = selectedIndex == index,
                        onSelect = { selectedIndex = index },
                        title = title,
                        description = desc
                    )
                }
            }
        }

        // 선택 여부에 따른 스타일
        val isSelected = selectedIndex != -1
        val bgColor =
            if (isSelected) LocalColorTheme.current.primary[400] else LocalColorTheme.current.primary[200]

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, bottom = 15.dp)
        ) {
            // 위쪽 그라데이션 바
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp) // 그라데이션 두께
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                LocalColorTheme.current.secondary[100],
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor)
                    .clickable(enabled = isSelected) {
                        // TODO: 선택된 챌린지로 다시 도전하기 액션
                    }
                    .padding(vertical = 12.5.dp)
            ) {
                Text(
                    text = "다시 도전하기",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

val previewPastChallengeScreenTopBarProp = PastChallengeScreenTopBarProp(
    onHeightChanged = {},
    onBackIconClicked = {}
)

@Preview
@Composable
fun PreviewPastChallengeScreen() {
    PastChallengeScreen(topBarProp = previewPastChallengeScreenTopBarProp)
}