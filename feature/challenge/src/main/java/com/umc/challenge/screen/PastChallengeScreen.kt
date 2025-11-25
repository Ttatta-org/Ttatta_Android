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
import com.umc.core.model.Challenge
import com.umc.design.theme.LocalColorTheme

data class PastChallengeScreenTopBarProp(
    val onHeightChanged: (Dp) -> Unit,
    val onBackIconClicked: () -> Unit,
)

@Composable
fun PastChallengeScreen(
    topBarProp: PastChallengeScreenTopBarProp,
    pastChallenges: List<Challenge>,
    onRetryChallenge: (Challenge) -> Unit,
) {
    var topBarHeight by remember { mutableStateOf(0.dp) }

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
            itemsIndexed(pastChallenges) {index, challenge ->
                val isSuccess = challenge.isCompleted
                val title = challenge.title
                val desc = challenge.content

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
                .padding(start = 22.dp, end = 22.dp, bottom = 70.dp)
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
                        val target = pastChallenges.getOrNull(selectedIndex)
                        if (target != null) {
                            onRetryChallenge(target)
                        }
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
    val dummyChallenges = listOf(
        com.umc.core.model.Challenge(
            id = 1L,
            title = "헬스장 가기",
            content = "오늘은 하체 뿌시기",
            isCompleted = true
        ),
        com.umc.core.model.Challenge(
            id = 2L,
            title = "영어 단어 30개 외우기",
            content = "오늘 분량은 꼭!",
            isCompleted = false
        )
    )

    PastChallengeScreen(
        topBarProp = previewPastChallengeScreenTopBarProp,
        pastChallenges = dummyChallenges,
        onRetryChallenge = {}
    )
}