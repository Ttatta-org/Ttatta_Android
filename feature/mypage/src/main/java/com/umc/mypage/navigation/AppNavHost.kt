package com.umc.mypage.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import com.umc.mypage.MyPageScreen
import com.umc.mypage.MyPageViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.umc.mypage.LockPasswordScreen
import com.umc.mypage.LockSettingsScreen
import com.umc.mypage.NotificationSettingsScreen
import com.umc.mypage.SignOutScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: MyPageViewModel,
    onLoginCanceled: () -> Unit
) {
    val userInfo by viewModel.userInfoState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "mypage"
    ) {
        composable("mypage") {
            MyPageScreen(
                userInfo = userInfo,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onNavigateToNotifications = {
                    navController.navigate("notification")
                },
                onNavigateToLockSetting = {
                    navController.navigate("lock")
                },
                onLogout = {
                    viewModel.logout(
                        onSuccess = { /* 로그아웃 후 동작 */ },
                        onError = { /* 에러 처리 */ }
                    )
                },
                onLeaveUser = {
                    navController.navigate("signout")
                },
                onFabClick = { /* FAB 클릭 처리 */ }
            )
        }

        composable("signout") {
            SignOutScreen(
                name = userInfo?.name ?: "",
                onLeaveUser = { reason ->
                    viewModel.leaveUser(
                        reason = reason,
                        onSuccess = { onLoginCanceled() },
                        onError = { errorMsg ->
                            Log.e("SignOut", "❌ 탈퇴 실패: $errorMsg")
                        }
                    )
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable("notification") {
            NotificationSettingsScreen(

            )
        }

        composable("lock") {
            LockSettingsScreen(
                onLockPassword = {
                    navController.navigate("lockpassword")
                },
                onChangePassword = {
                    navController.navigate("changepassword")
                }
            )
        }
        composable("lockpassword") {
            LockPasswordScreen(
                isChangingPassword = false,
                onComplete = {
                    navController.popBackStack()
                }
            )
        }
        composable("changepassword") {
            LockPasswordScreen(
                isChangingPassword = true,
                onComplete = {
                    navController.popBackStack()
                }
            )
        }
    }
}