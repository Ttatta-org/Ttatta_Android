package com.umc.mypage.app.mypage.pinlock

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.umc.core.util.runWithScope
import com.umc.core.util.showToast
import com.umc.design.component.LoadingModal
import com.umc.mypage.screen.LockPasswordScreen
import com.umc.mypage.screen.LockSettingsScreen
import com.umc.mypage.util.NavigationUtil.getSafeBackNavigatorCallback
import com.umc.mypage.util.NavigationUtil.getSafeNavigatorCallback

fun NavGraphBuilder.addPinLockNavGraph(
    route: String,
    navController: NavController,
) = navigation(
    startDestination = "${route}/home",
    route = route,
) {
    composable("${route}/home") { backStackEntry ->
        val viewModel: PinLockViewModel = hiltViewModel(backStackEntry)

        val isPinSet by viewModel.isPinEnabled.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.refreshPinStatus()
        }

        LockSettingsScreen(
            onLockPassword = navController.getSafeNavigatorCallback(
                backStackEntry = backStackEntry,
                route = "${route}/pin?change=false",
            ),
            onChangePassword = navController.getSafeNavigatorCallback(
                backStackEntry = backStackEntry,
                route = "${route}/pin?change=true",
            ),
            isPinSet = isPinSet,
            clearPin = { viewModel.runWithScope { runCatching { clearPin() } } },
            onBackClick = navController.getSafeBackNavigatorCallback(backStackEntry),
        )
    }

    composable(
        route = "${route}/pin?change={change}",
        arguments = listOf(navArgument("change") { this.defaultValue = false }),
    ) { backStackEntry ->
        val viewModel: PinLockPasswordViewModel = hiltViewModel()
        val isChangeMode = backStackEntry.arguments?.getBoolean("change") ?: false
        val context = LocalContext.current

        val isLoading by viewModel.isLoading.collectAsState()
        val title by viewModel.title.collectAsState()
        val description by viewModel.description.collectAsState()
        val fillCount by viewModel.fillCount.collectAsState()
        val showPasswordWrongMessage by viewModel.showPasswordWrongMessage.collectAsState()

        val onNavigateToBack = navController.getSafeBackNavigatorCallback(backStackEntry)

        LaunchedEffect(viewModel, isChangeMode) {
            viewModel.initialize(changeMode = isChangeMode)
        }

        LaunchedEffect(viewModel, onNavigateToBack) {
            viewModel.setOnPinSuccessfullySetListener {
                onNavigateToBack()
            }
        }

        LaunchedEffect(viewModel, context) {
            viewModel.setOnPinSettingFailedListener {
                context.showToast("비밀번호 설정에 오류가 발생했습니다. 다시 시도해주세요.")
            }
        }

        LockPasswordScreen(
            title = title,
            description = description,
            errorMessage = if (showPasswordWrongMessage) "암호가 일치하지 않아요! 다시 입력해주세요." else null,
            totalCount = 4,
            fillCount = fillCount,
            onBackButtonClicked = onNavigateToBack,
            onNumberClicked = { viewModel.onNumberClicked(it) },
            onEraseButtonClicked = viewModel::onEraseButtonClicked,
            onCancelButtonClicked = viewModel::onCancelButtonClicked,
        )

        if (isLoading) LoadingModal()
    }
}
