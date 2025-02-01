package com.umc.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.home.R

@Composable
fun BottomNavigationBarWithFAB(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
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
                NavigationBarItem(
                    selected = selectedTab == "diary",
                    onClick = { onTabSelected("diary") },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_diary),
                            contentDescription = "일기 보관함",
                            modifier = Modifier
                                .width(20.dp)
                                .height(19.dp),
                            tint = if (selectedTab == "diary") Color(0xFFFF896B) else Color(0xFFCACACA)
                        )
                    },
                    label = {
                        Text(
                            "일기 보관함",
                            color = if (selectedTab == "diary") Color(0xFFFF896B) else Color(0xFFCACACA)
                        )
                    },
                    modifier = Modifier
                        .drawBehind {
                            if (selectedTab == "diary") {
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
                    interactionSource = remember { MutableInteractionSource() }, // ✅ 클릭 효과 감추기
                )

                NavigationBarItem(
                    selected = selectedTab == "footprint",
                    onClick = { onTabSelected("footprint") },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_footprint),
                            contentDescription = "나의 발자국",
                            modifier = Modifier
                                .width(25.dp)
                                .height(19.dp),
                            tint = if (selectedTab == "footprint") Color(0xFFFF896B) else Color(0xFFCACACA)
                        )
                    },
                    label = {
                        Text(
                            "나의 발자국",
                            color = if (selectedTab == "footprint") Color(0xFFFF896B) else Color(0xFFCACACA)
                        )
                    },
                    modifier = Modifier
                        .drawBehind {
                            if (selectedTab == "footprint") {
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
                    )
                )

                Spacer(modifier = Modifier.weight(1f)) // ✅ 플로팅 버튼 자리 확보

                NavigationBarItem(
                    selected = selectedTab == "challenge",
                    onClick = { onTabSelected("challenge") },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_challenge),
                            contentDescription = "나의 챌린지",
                            modifier = Modifier
                                .width(19.dp)
                                .height(20.dp),
                            tint = if (selectedTab == "challenge") Color(0xFFFF896B) else Color(0xFFCACACA)
                        )
                    },
                    label = {
                        Text(
                            "나의 챌린지",
                            color = if (selectedTab == "challenge") Color(0xFFFF896B) else Color(0xFFCACACA)
                        )
                    },
                    modifier = Modifier
                        .drawBehind {
                            if (selectedTab == "challenge") {
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
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == "mypage",
                    onClick = { onTabSelected("mypage") },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_mypage),
                            contentDescription = "마이페이지",
                            modifier = Modifier
                                .width(20.9.dp)
                                .height(19.5.dp),
                            tint = if (selectedTab == "mypage") Color(0xFFFF896B) else Color(0xFFCACACA)
                        )
                    },
                    label = {
                        Text(
                            "마이페이지",
                            color = if (selectedTab == "mypage") Color(0xFFFF896B) else Color(0xFFCACACA)
                        )
                    },
                    modifier = Modifier
                        .drawBehind {
                            if (selectedTab == "mypage") {
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
                    )
                )
            }
        }

        // ✅ 플로팅 버튼 추가
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            CustomFloatingImageButton(onClick = onFabClick)
        }
    }
}


@Composable
fun CustomFloatingImageButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(79.dp)
            .height(73.59.dp)
            .offset(y = (-25).dp) // 바텀 네비게이션에 걸치도록 위치 조정
            .clickable(onClick = onClick),
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
                    onClick()
                },
        )
    }
}