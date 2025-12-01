package com.umc.mypage.navigation

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.umc.design.component.LocationAccessPopup
import com.umc.mypage.LockPasswordScreen
import com.umc.mypage.LockSettingsScreen
import com.umc.mypage.MyPageScreen
import com.umc.mypage.MyPageViewModel
import com.umc.mypage.NotificationSettingsScreen
import com.umc.mypage.SignOutScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: MyPageViewModel,
    onLoginCanceled: () -> Unit,
    onBackgroundLocationRequirementChanged: (isRequired: Boolean) -> Boolean,
    onNavigationBarVisibilityChanged: (Boolean) -> Unit,
) {
    val userInfo by viewModel.userInfoState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isPinSet by viewModel.isPinEnabled.collectAsState()
    val notiState by viewModel.notificationUi.collectAsState()

    val context = LocalContext.current

    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    val notificationAndLocationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS,
            )
        )
    } else {
        rememberMultiplePermissionsState(
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        )
    }

    var showLocationPermissionPopup by remember { mutableStateOf(false) }

    DisposableEffect(navController, onNavigationBarVisibilityChanged) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.route) {
                "mypage" -> onNavigationBarVisibilityChanged(true)
                else -> onNavigationBarVisibilityChanged(false)
            }
        }

        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    NavHost(
        navController = navController,
        startDestination = "mypage",
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
                        onError = { /* 에러 처리 */ },
                    )
                },
                onLeaveUser = {
                    navController.navigate("signout")
                },
                onFabClick = { /* FAB 클릭 처리 */ },
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
                        },
                    )
                },
                onCancel = {
                    navController.popBackStack()
                },
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
                onDailyToggle = {
                    if (it) {
                        if (notificationPermissionState == null || notificationPermissionState.status == PermissionStatus.Granted) {
                            viewModel.onDailyToggle(true)
                        } else {
                            notificationPermissionState.launchPermissionRequest()
                        }
                    } else {
                        viewModel.onDailyToggle(false)
                    }
                },
                onDailyTimeChange = viewModel::onDailyTimeChange,
                onSummaryToggle = {
                    if (it) {
                        if (notificationPermissionState == null || notificationPermissionState.status == PermissionStatus.Granted) {
                            viewModel.onSummaryToggle(true)
                        } else {
                            notificationPermissionState.launchPermissionRequest()
                        }
                    } else {
                        viewModel.onSummaryToggle(false)
                    }
                },
                onSummaryHourChange = viewModel::onSummaryHourChange,
                onChallengeToggle = {
                    if (it) {
                        if (notificationPermissionState == null || notificationPermissionState.status == PermissionStatus.Granted) {
                            viewModel.onChallengeToggle(true)
                        } else {
                            notificationPermissionState.launchPermissionRequest()
                        }
                    } else {
                        viewModel.onChallengeToggle(false)
                    }
                },
                onChallengeHoursChange = viewModel::onChallengeHoursChange,
                onLocationToggle = {
                    if (it) {
                        if (notificationAndLocationPermissionState.allPermissionsGranted) {
                            viewModel.onLocationToggle(true)
                            onBackgroundLocationRequirementChanged(true)
                        } else {
                            showLocationPermissionPopup = true
                        }
                    } else {
                        viewModel.onLocationToggle(false)
                        onBackgroundLocationRequirementChanged(false)
                    }
                },
                onBackClick = { navController.popBackStack() },
            )

            if (showLocationPermissionPopup) LocationAccessPopup(
                onConfirm = {
                    if (notificationAndLocationPermissionState.shouldShowRationale) {
                        val toast = Toast.makeText(
                            context,
                            "설정에서 위치 권한과 알림 권한을 허용으로 바꾸어주세요.",
                            Toast.LENGTH_SHORT
                        )

                        toast.show()

                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }

                        context.startActivity(intent)
                    } else {
                        notificationAndLocationPermissionState.launchMultiplePermissionRequest()
                    }

                    showLocationPermissionPopup = false
                },
                onDismiss = { showLocationPermissionPopup = false },
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
                onBackClick = { navController.popBackStack() },
            )
        }

        composable("lockpassword") {
            LockPasswordScreen(
                isChangingPassword = false,
                onComplete = { pin ->
                    viewModel.savePin(pin.toInt())
                    MainScope().launch { navController.popBackStack() }
                },
            )
        }

        composable("changepassword") {
            LockPasswordScreen(
                isChangingPassword = true,
                onComplete = { pin ->
                    viewModel.savePin(pin.toInt())
                    MainScope().launch { navController.popBackStack() }
                },
            )
        }
    }
}