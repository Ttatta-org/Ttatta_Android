package com.umc.mypage.app.mypage.notification

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.umc.core.util.runWithScope
import com.umc.core.util.showToast
import com.umc.design.component.LocationAccessPopup
import com.umc.mypage.screen.NotificationSettingsScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
fun NavGraphBuilder.addNotificationSettingNavGraph(
    route: String,
    navController: NavController,
    onBackgroundLocationRequirementChanged: (isRequired: Boolean) -> Boolean,
) = navigation(
    startDestination = "${route}/toggles",
    route = route,
) {
    composable("${route}/toggles") { backStackEntry ->
        val viewModel: NotificationSettingViewModel = hiltViewModel(backStackEntry)

        val context = LocalContext.current
        val notificationUiState by viewModel.notificationUi.collectAsState()

        val notificationPermissionState =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
                    listOf(Manifest.permission.ACCESS_FINE_LOCATION),
                )
            }

        var showLocationPermissionPopup by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            viewModel.loadNotificationSettings()
        }

        NotificationSettingsScreen(
            // ⬇️ 화면이 stateful이라면 이 파트만 먼저 적용:
            // 1) 스위치 상태/초깃값들을 notiState로 치환
            // 2) onCheckedChange / 시간 변경 콜백에서 viewModel 메서드 호출
            state = notificationUiState,                                // ⬅️ (아래 3번 참고: 화면 시그니처 바꾸는안)
            onDailyToggle = { isOn ->
                if (isOn && notificationPermissionState != null && notificationPermissionState.status != PermissionStatus.Granted) {
                    notificationPermissionState.launchPermissionRequest()
                } else {
                    viewModel.runWithScope { onDailyToggle(isOn) }
                }
            },
            onDailyTimeChange = { isPm, hour12, minute ->
                viewModel.runWithScope {
                    onDailyTimeChange(isPm, hour12, minute)
                }
            },
            onSummaryToggle = { isOn ->
                if (isOn && notificationPermissionState != null && notificationPermissionState.status != PermissionStatus.Granted) {
                    notificationPermissionState.launchPermissionRequest()
                } else {
                    viewModel.runWithScope { onSummaryToggle(isOn) }
                }
            },
            onSummaryHourChange = { hour ->
                viewModel.runWithScope { onSummaryHourChange(hour) }
            },
            onChallengeToggle = { isOn ->
                if (isOn && notificationPermissionState != null && notificationPermissionState.status != PermissionStatus.Granted) {
                    notificationPermissionState.launchPermissionRequest()
                } else {
                    viewModel.runWithScope { onChallengeToggle(isOn) }
                }
            },
            onChallengeHoursChange = { hours ->
                viewModel.runWithScope { onChallengeHoursChange(hours) }
            },
            onLocationToggle = { isOn ->
                if (isOn && !notificationAndLocationPermissionState.allPermissionsGranted) {
                    showLocationPermissionPopup = true
                } else {
                    viewModel.runWithScope { onLocationToggle(isOn) }
                    onBackgroundLocationRequirementChanged(isOn)
                }
            },
            onBackClick = {
                MainScope().launch { navController.popBackStack() }
            },
        )

        if (showLocationPermissionPopup) LocationAccessPopup(
            onConfirm = {
                if (notificationAndLocationPermissionState.shouldShowRationale) {
                    context.showToast("설정에서 위치 권한과 알림 권한을 허용으로 바꾸어주세요.")

                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            this.data = Uri.fromParts("package", context.packageName, null)
                            this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
}