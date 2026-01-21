package com.umc.mypage.app.mypage.pinlock

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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
            onLockPassword = {
                MainScope().launch { navController.navigate("${route}/pin?change=false") }
            },
            onChangePassword = {
                MainScope().launch { navController.navigate("${route}/pin?change=true") }
            },
            isPinSet = isPinSet,
            clearPin = { viewModel.runWithScope { runCatching { clearPin() } } },
            onBackClick = {
                MainScope().launch { navController.popBackStack() }
            },
        )
    }

    composable(
        route = "${route}/pin?change={change}",
        arguments = listOf(navArgument("change") { this.defaultValue = false }),
    ) { backStackEntry ->
        val viewModel: PinLockViewModel = hiltViewModel(backStackEntry)

        val isChangeMode = backStackEntry.arguments?.getBoolean("change") ?: false
        val context = LocalContext.current
        val isLoading by viewModel.isLoading.collectAsState()

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
                            this
                                .runCatching { viewModel.savePin(pin = password2.toInt()) }
                                .onSuccess {
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
