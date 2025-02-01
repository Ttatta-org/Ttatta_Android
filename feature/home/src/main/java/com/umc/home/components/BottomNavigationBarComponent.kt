package com.umc.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.umc.home.R

@Composable
fun BottomNavigationBarWithFAB(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = selectedTab == "diary",
            onClick = { onTabSelected("diary") },
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_diary),
                    contentDescription = "일기 보관함",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = { Text("일기 보관함") }
        )
        NavigationBarItem(
            selected = selectedTab == "footprint",
            onClick = { onTabSelected("footprint") },
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_footprint),
                    contentDescription = "나의 발자국",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = { Text("나의 발자국") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Do nothing */ },
            icon = { }, // 플로팅 버튼 자리 차지
            label = { }
        )
        NavigationBarItem(
            selected = selectedTab == "challenge",
            onClick = { onTabSelected("challenge") },
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_challenge),
                    contentDescription = "나의 챌린지",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = { Text("나의 챌린지") }
        )
        NavigationBarItem(
            selected = selectedTab == "mypage",
            onClick = { onTabSelected("mypage") },
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_mypage),
                    contentDescription = "마이페이지",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = { Text("마이페이지") }
        )
    }
}
