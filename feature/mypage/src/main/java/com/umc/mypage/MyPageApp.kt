package com.umc.mypage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.umc.mypage.navigation.AppNavHost

@Composable
fun MyPageApp(
    viewModel: MyPageViewModel,
    onLoginCanceled: () -> Unit,
    onBackgroundLocationRequirementChanged: (isRequired: Boolean) -> Boolean,
    onNavigationBarVisibilityChanged: (Boolean) -> Unit,
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
        onLoginCanceled = onLoginCanceled, // 로그아웃 or 탈퇴 시 로그인화면 이동
        onBackgroundLocationRequirementChanged = onBackgroundLocationRequirementChanged,
        onNavigationBarVisibilityChanged = onNavigationBarVisibilityChanged,
    )
}