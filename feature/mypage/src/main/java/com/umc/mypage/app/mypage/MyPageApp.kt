package com.umc.mypage.app.mypage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.umc.core.util.runWithScope
import com.umc.mypage.app.mypage.notification.addNotificationSettingNavGraph
import com.umc.mypage.app.mypage.pinlock.addPinLockNavGraph
import com.umc.mypage.app.mypage.signout.addSignOutNavGraph
import com.umc.mypage.app.mypage.updateprofile.addUpdateProfileNavGraph
import com.umc.mypage.screen.MyPageScreen
import com.umc.mypage.util.NavigationUtil.getSafeNavigatorCallback

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyPageApp(
    viewModel: MyPageViewModel,
    onLoginCanceled: () -> Unit,
    onBackgroundLocationRequirementChanged: (isRequired: Boolean) -> Boolean,
    onNavigationBarVisibilityChanged: (Boolean) -> Unit,
) {
    val navController = rememberNavController()

    val userInfo by viewModel.userInfoState.collectAsState()
    var errorMessage: String? by remember { mutableStateOf(null) }

    DisposableEffect(onNavigationBarVisibilityChanged) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.route) {
                "my-page" -> {
                    viewModel.runWithScope {
                        runCatching { viewModel.loadUserInfo() }
                            .onFailure { errorMessage = "유저 정보를 불러오지 못했습니다." }
                    }
                    onNavigationBarVisibilityChanged(true)
                }

                else -> onNavigationBarVisibilityChanged(false)
            }
        }

        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    NavHost(
        navController = navController,
        startDestination = "my-page",
    ) {
        composable("my-page") { backStackEntry ->
            MyPageScreen(
                userInfo = userInfo,
                errorMessage = errorMessage,
                onUpdateProfileButtonClicked = navController.getSafeNavigatorCallback(
                    backStackEntry = backStackEntry,
                    route = "update-profile",
                ),
                onNotificationSettingButtonClicked = navController.getSafeNavigatorCallback(
                    backStackEntry = backStackEntry,
                    route = "notification",
                ),
                onPinLockButtonClicked = navController.getSafeNavigatorCallback(
                    backStackEntry = backStackEntry,
                    route = "pin-lock",
                ),
                onLogoutButtonClicked = {
                    viewModel.runWithScope {
                        runCatching { logout() }.onSuccess { onLoginCanceled() }
                    }
                },
                onLeaveUserButtonClicked = navController.getSafeNavigatorCallback(
                    backStackEntry = backStackEntry,
                    route = "sign-out",
                ),
            )
        }

        addUpdateProfileNavGraph(
            route = "update-profile",
            navController = navController,
        )

        addSignOutNavGraph(
            route = "sign-out",
            navController = navController,
            onLoginCanceled = onLoginCanceled,
        )

        addNotificationSettingNavGraph(
            route = "notification",
            navController = navController,
            onBackgroundLocationRequirementChanged = onBackgroundLocationRequirementChanged,
        )

        addPinLockNavGraph(
            route = "pin-lock",
            navController = navController,
        )
    }
}