package com.umc.design

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class NavigationItem(
    val title: String,
    @DrawableRes val icon: Int,
) {
    DIARY(
        title = "일기 보관함",
        icon = R.drawable.ic_diary
    ),
    FOOTPRINT(
        title = "나의 발자국",
        icon = R.drawable.ic_footprint
    ),
    CHALLENGE(
        title = "나의 챌린지",
        icon = R.drawable.ic_challenge
    ),
    MY_PAGE(
        title = "마이페이지",
        icon = R.drawable.ic_mypage
    )
}

@Composable
fun BottomNavigationBar(
    selectedTab: NavigationItem,
    onTabSelected: (NavigationItem) -> Unit,
    onFabClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawLine(
                        color = Color(0xFFEDEDED), // 상단 테두리 색상
                        start = Offset(0f, 0f), // 좌측 상단에서 시작
                        end = Offset(size.width, 0f), // 우측 상단까지 선을 그림
                        strokeWidth = 1.dp.toPx() // 테두리 두께
                    )
                },
            containerColor = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp), // ✅ 내부 아이콘들만 좌우 패딩 적용
                horizontalArrangement = Arrangement.SpaceBetween // ✅ 균등 분배
            ) {
                listOf(
                    NavigationItem.DIARY,
                    NavigationItem.FOOTPRINT,
                    null,
                    NavigationItem.CHALLENGE,
                    NavigationItem.MY_PAGE,
                ).forEach { item ->
                    item?.let {
                        NavigationBarItem(
                            selected = selectedTab == item,
                            onClick = { onTabSelected(item) },
                            icon = {
                                Icon(
                                    painterResource(id = item.icon),
                                    contentDescription = "일기 보관함",
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(19.dp),
                                    tint = if (selectedTab == item) Color(0xFFFF896B) else Color(0xFFCACACA)
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    color = if (selectedTab == item) Color(0xFFFF896B) else Color(0xFFCACACA)
                                )
                            },
                            modifier = Modifier
                                .drawBehind {
                                    if (selectedTab == item) {
                                        drawLine(
                                            color = Color(0xFFFF896B), // ✅ 인디케이터 색상
                                            start = Offset(0f, 0f), // ✅ 좌측 상단에서 시작
                                            end = Offset(size.width, 0f), // ✅ 우측 상단까지 선을 그림
                                            strokeWidth = 3.dp.toPx() // ✅ 테두리 두께 조절
                                        )
                                    }
                                },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent // ✅ 클릭 시 보라색 배경 제거!
                            ),
                            interactionSource = null, // ✅ 클릭 효과 감추기
                        )
                    } ?: run {
                        Spacer(modifier = Modifier.weight(1f)) // ✅ 플로팅 버튼 자리 확보
                    }
                }
            }
        }

        // ✅ 플로팅 버튼 추가
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .width(79.dp)
                    .height(73.59.dp)
                    .offset(y = (25.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()) * -1), // 바텀 네비게이션에 걸치도록 위치 조정
                contentAlignment = Alignment.Center
            ) {
                // ✅ 블러 효과를 활용한 그림자 레이어
                Image(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null, // 그림자는 접근성 필요 없음
                    modifier = Modifier
                        .width(79.dp)
                        .height(73.59.dp)
                        .offset(y = (4).dp)
                        .blur(4.dp), // ✅ 블러 효과 적용
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black.copy(alpha = 0.25f)) // ✅ `tint` 오류 해결
                )

                // ✅ 실제 플로팅 버튼 이미지
                Image(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "추가 버튼",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            indication = null, // ✅ 클릭 시 회색 박스(터치 피드백) 제거
                            interactionSource = remember { MutableInteractionSource() } // ✅ 클릭 효과 감추기
                        ) {
                            onFabClick()
                        },
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewBottomNavigationBar() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.fillMaxSize())
        }
        BottomNavigationBar(
            selectedTab = NavigationItem.DIARY,
            onTabSelected = {},
            onFabClick = {}
        )
    }
}