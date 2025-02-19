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

@Composable
fun MyPageApp(
    viewModel: MyPageViewModel,
    onLoginCanceled: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.loadUserInfo() // ✅ 화면 진입 시 유저 정보 로드
    }

    // ✅ ViewModel에서 데이터 가져오기
    val userInfo by viewModel.userInfoState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // ✅ MyPageScreen에 데이터 전달
    MyPageScreen(
        userInfo = userInfo,
        isLoading = isLoading,
        errorMessage = errorMessage,
        // ✅ 로그아웃 성공 시 `onLoginCanceled` 호출하여 로그인 화면으로 이동
        onLogout = {
            viewModel.logout(
                onSuccess = {
                    onLoginCanceled() // ✅ 로그아웃 후 로그인 화면으로 이동
                },
                onError = { /* 오류 처리 가능 */ }
            )
        },
        // ✅ 회원 탈퇴 성공 시 `onLoginCanceled` 호출하여 로그인 화면으로 이동
        onLeaveUser = {
            viewModel.leaveUser(
                onSuccess = {
                    onLoginCanceled() // ✅ 회원 탈퇴 후 로그인 화면으로 이동
                },
                onError = { /* 오류 처리 가능 */ }
            )
        },
        onFabClick = { /* FAB 클릭 이벤트 처리 */ },
    )
}


//class MyPageApp : ComponentActivity() {
//    val viewModel by viewModels<MyPageViewModel>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//
//            // @Composable 함수로 호출
//            MyPageScreen(
//                viewModel = viewModel,
//                onTabSelected = { selectedTab ->
//                    // 탭 선택 로직 추가
//                    println("탭 선택됨: $selectedTab")
//                },
//                onThemeChangeClick = {
//                    // 테마 변경 화면으로 이동 로직 추가
//                    println("테마 변경 버튼 클릭")
//                },
//                onFabClick = {
//                    // 플로팅 버튼 클릭 로직 추가
//                    println("플로팅 버튼 클릭")
//                }
//            )
//        }
//    }
//}
