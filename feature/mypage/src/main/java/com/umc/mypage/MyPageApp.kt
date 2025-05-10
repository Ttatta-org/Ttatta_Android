package com.umc.mypage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.umc.mypage.navigation.AppNavHost

@Composable
fun MyPageApp(
    viewModel: MyPageViewModel,
    onLoginCanceled: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.loadUserInfo() // ✅ 화면 진입 시 유저 정보 로드
    }

    // ✅ NavController 선언
    val navController = rememberNavController()

    // ✅ NavHost로 화면 관리
    AppNavHost(
        navController = navController,
        viewModel = viewModel,
        onLoginCanceled = onLoginCanceled // 로그아웃 or 탈퇴 시 로그인화면 이동
    )
}