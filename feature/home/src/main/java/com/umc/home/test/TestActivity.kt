package com.umc.home.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.umc.home.HomeScreen
import com.umc.home.HomeViewModel
import com.umc.home.navigation.AppNavHost
import kotlinx.coroutines.flow.MutableStateFlow

class TestActivity : ComponentActivity() {
    private val testViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 네비게이션 컨트롤러 생성
            val navController = rememberNavController()

            // AppNavHost 호출
            AppNavHost(
                navController = navController,
                viewModel = testViewModel
            )
        }
    }
}