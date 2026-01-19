package com.umc.mypage.app.mypage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.umc.mypage.app.mypage.navigation.addNotificationSettingNavGraph
import com.umc.mypage.app.mypage.navigation.addPinLockNavGraph
import com.umc.mypage.app.mypage.navigation.addSignOutNavGraph
import com.umc.mypage.app.mypage.navigation.addUpdateProfileNavGraph
import com.umc.mypage.screen.MyPageScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserInfo()
    }

    DisposableEffect(onNavigationBarVisibilityChanged) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.route) {
                "my-page" -> onNavigationBarVisibilityChanged(true)
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
        composable("my-page") {
            MyPageScreen(
                userInfo = userInfo,
                errorMessage = errorMessage,
                onUpdateProfileButtonClicked = {
                    MainScope().launch { navController.navigate("update-profile") }
                },
                onNotificationSettingButtonClicked = {
                    MainScope().launch { navController.navigate("notification") }
                },
                onPinLockButtonClicked = {
                    MainScope().launch { navController.navigate("pin-lock") }
                },
                onLogoutButtonClicked = {
                    viewModel.logout(
                        onSuccess = onLoginCanceled,
                        onError = { /* 에러 처리 */ },
                    )
                },
                onLeaveUserButtonClicked = {
                    MainScope().launch { navController.navigate("sign-out") }
                },
            )
        }

        addUpdateProfileNavGraph(
            route = "update-profile",
            viewModel = viewModel,
            navController = navController,
        )

        addSignOutNavGraph(
            route = "sign-out",
            viewModel = viewModel,
            navController = navController,
            onLoginCanceled = onLoginCanceled,
        )

        addNotificationSettingNavGraph(
            route = "notification",
            viewModel = viewModel,
            navController = navController,
            onBackgroundLocationRequirementChanged = onBackgroundLocationRequirementChanged,
        )

        addPinLockNavGraph(
            route = "pin-lock",
            viewModel = viewModel,
            navController = navController,
        )
    }
}