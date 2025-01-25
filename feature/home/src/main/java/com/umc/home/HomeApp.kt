package com.umc.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.umc.home.HomeScreen

class HomeApp : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen(
                viewModel = homeViewModel,
                onFabClick = { /* FAB 클릭 이벤트 */ },
                onCalendarToggle = { /* 캘린더 열기/닫기 이벤트 */ }
            )
        }
    }
}