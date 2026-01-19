package com.umc.mypage.navigation

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.umc.core.util.runWithScope
import com.umc.core.util.showToast
import com.umc.design.component.LoadingModal
import com.umc.design.component.LocationAccessPopup
import com.umc.mypage.screen.LockPasswordScreen
import com.umc.mypage.screen.LockSettingsScreen
import com.umc.mypage.screen.MyPageScreen
import com.umc.mypage.MyPageViewModel
import com.umc.mypage.screen.NotificationSettingsScreen
import com.umc.mypage.screen.SignOutScreen
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

    val notificationAndLocationPermissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
            )
        }

        composable("signout") {
            SignOutScreen(
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
                        context.showToast("설정에서 위치 권한과 알림 권한을 허용으로 바꾸어주세요.")

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
                    navController.navigate("pin?change=false")
                },
                onChangePassword = {
                    navController.navigate("pin?change=true")
                },
                isPinSet = isPinSet,
                clearPin = { viewModel.clearPin() },
                onBackClick = { navController.popBackStack() },
            )
        }

        composable(
            route = "pin?change={change}",
            arguments = listOf(navArgument("change") { defaultValue = false }),
        ) { backStackEntry ->
            val isChangeMode = backStackEntry.arguments?.getBoolean("change") ?: false
            val context = LocalContext.current

            var password1 by remember { mutableStateOf("") }
            var password2 by remember { mutableStateOf("") }
            var currentStep by remember { mutableIntStateOf(0) }
            var showPasswordWrongMessage by remember { mutableStateOf(false) }

            LaunchedEffect(currentStep) {
                if (currentStep == 0) {
                    password1 = ""
                    password2 = ""
                    showPasswordWrongMessage = false
                }
            }

            LaunchedEffect(isChangeMode, currentStep, password1, password2) {
                when (currentStep) {
                    0 -> {
                        if (password1.length == 4) currentStep++
                    }

                    1 -> {
                        if (password2.length != 4) return@LaunchedEffect

                        if (password1 == password2) {
                            viewModel.runWithScope {
                                runCatching { viewModel.savePin(pin = password2.toInt()) }
                                    .onSuccess {
                                        context.showToast("비밀번호가 설정되었습니다.")
                                        MainScope().launch { navController.popBackStack() }
                                    }
                                    .onFailure {
                                        password2 = ""
                                        context.showToast("비밀번호 설정에 오류가 발생했습니다. 다시 시도해주세요.")
                                    }
                            }
                        } else {
                            password2 = ""
                            showPasswordWrongMessage = true
                        }
                    }
                }
            }

            LockPasswordScreen(
                title = if (isChangeMode) "암호 변경" else "암호 잠금",
                description = when (isChangeMode) {
                    true -> when (currentStep) {
                        0 -> "새로운 암호를 입력해주세요."
                        1 -> "확인을 위해 한 번 더 입력해주세요."
                        else -> throw Exception()
                    }

                    false -> when (currentStep) {
                        0 -> "암호를 입력해주세요."
                        1 -> "확인을 위해 한 번 더 입력해주세요."
                        else -> throw Exception()
                    }
                },
                errorMessage = if (showPasswordWrongMessage) "암호가 일치하지 않아요! 다시 입력해주세요." else null,
                totalCount = 4,
                fillCount = when (currentStep) {
                    0 -> password1.length
                    1 -> password2.length
                    else -> throw Exception()
                },
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onNumberClicked = {
                    when (currentStep) {
                        0 -> if (password1.length < 4) password1 += it
                        1 -> if (password2.length < 4) password2 += it
                    }
                },
                onEraseButtonClicked = {
                    when (currentStep) {
                        0 -> password1 = password1.dropLast(1)
                        1 -> password2 = password2.dropLast(1)
                    }

                },
                onCancelButtonClicked = {
                    when (currentStep) {
                        0 -> password1 = ""
                        1 -> password2 = ""
                    }
                },
            )

            if (isLoading) LoadingModal()
        }
    }
}