package com.umc.mypage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf

class MyPageApp : ComponentActivity() {
    val viewModel by viewModels<MyPageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // @Composable 함수로 호출
            MyPageScreen(
                viewModel = viewModel,
                onTabSelected = { selectedTab ->
                    // 탭 선택 로직 추가
                    println("탭 선택됨: $selectedTab")
                },
                onThemeChangeClick = {
                    // 테마 변경 화면으로 이동 로직 추가
                    println("테마 변경 버튼 클릭")
                },
                onFabClick = {
                    // 플로팅 버튼 클릭 로직 추가
                    println("플로팅 버튼 클릭")
                }
            )
        }
    }
}
