package com.umc.mypage.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.umc.mypage.LockPasswordScreen
import com.umc.mypage.LockSettingsScreen
import com.umc.mypage.MyPageScreen
import com.umc.mypage.MyPageViewModel
import com.umc.mypage.NotificationSettingsScreen
import com.umc.mypage.SignOutScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: MyPageViewModel,
    onLoginCanceled: () -> Unit
) {
    val userInfo by viewModel.userInfoState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isPinSet by viewModel.isPinEnabled.collectAsState()
    val notiState by viewModel.notificationUi.collectAsState()

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
                        onSuccess = onLoginCanceled,
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
            LaunchedEffect(Unit) {
                viewModel.loadNotificationSettings()
            }
            NotificationSettingsScreen(
                // ⬇️ 화면이 stateful이라면 이 파트만 먼저 적용:
                // 1) 스위치 상태/초깃값들을 notiState로 치환
                // 2) onCheckedChange / 시간 변경 콜백에서 viewModel 메서드 호출
                state = notiState,                                // ⬅️ (아래 3번 참고: 화면 시그니처 바꾸는안)
                onDailyToggle = viewModel::onDailyToggle,
                onDailyTimeChange = viewModel::onDailyTimeChange,
                onSummaryToggle = viewModel::onSummaryToggle,
                onSummaryHourChange = viewModel::onSummaryHourChange,
                onChallengeToggle = viewModel::onChallengeToggle,
                onChallengeHoursChange = viewModel::onChallengeHoursChange,
                onLocationToggle = viewModel::onLocationToggle,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("lock") {

            LaunchedEffect(Unit) {
                viewModel.refreshPinStatus() // 화면 진입 시 최신 상태 반영
            }

            LockSettingsScreen(
                onLockPassword = {
                    navController.navigate("lockpassword")
                },
                onChangePassword = {
                    navController.navigate("changepassword")
                },
                isPinSet = isPinSet,
                clearPin = { viewModel.clearPin() },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("lockpassword") {
            LockPasswordScreen(
                isChangingPassword = false,
                onComplete = { pin ->
                    viewModel.savePin(pin.toInt())
                    MainScope().launch { navController.popBackStack() }
                }
            )
        }
        composable("changepassword") {
            LockPasswordScreen(
                isChangingPassword = true,
                onComplete = { pin ->
                    viewModel.savePin(pin.toInt())
                    MainScope().launch { navController.popBackStack() }
                }
            )
        }
    }
}