package com.umc.mypage.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.umc.mypage.MyPageScreen
import com.umc.mypage.MyPageViewModel

class TestActivity : ComponentActivity() {
    private val testViewModel by viewModels<MyPageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MyPageScreen(
                viewModel = testViewModel,
                onTabSelected = { selectedTab ->
                    println("테스트 - 선택된 탭: $selectedTab")
                },
                onFabClick = {
                    println("테스트 - 플로팅 버튼 클릭")
                },
                onThemeChangeClick = {
                    println("테스트 - 테마 변경 클릭")
                }
            )

        }
    }
}
