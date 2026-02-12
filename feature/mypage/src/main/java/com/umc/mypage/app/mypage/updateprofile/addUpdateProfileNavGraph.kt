package com.umc.mypage.app.mypage.updateprofile

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.umc.core.util.runWithScope
import com.umc.core.util.showToast
import com.umc.design.component.LoadingModal
import com.umc.mypage.screen.ProfileEditScreen
import com.umc.mypage.screen.ProfileEmailEditScreen
import com.umc.mypage.screen.ProfileNicknameEditScreen
import com.umc.mypage.util.NavigationUtil.getSafeBackNavigatorCallback
import com.umc.mypage.util.NavigationUtil.getSafeNavigatorCallback

fun NavGraphBuilder.addUpdateProfileNavGraph(
    route: String,
    navController: NavController,
) = navigation(
    startDestination = "$route/info",
    route = route,
) {
    composable("$route/info") { backStackEntry ->
        val viewModel: UpdateProfileViewModel = hiltViewModel(backStackEntry)

        val userInfo by viewModel.userInfo.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.loadUserInfo()
        }

        ProfileEditScreen(
            nickname = userInfo?.name,
            email = userInfo?.email,
            onBackButtonClicked = navController.getSafeBackNavigatorCallback(backStackEntry),
            onNicknameClicked = navController.getSafeNavigatorCallback(
                backStackEntry = backStackEntry,
                route = "$route/nickname",
            ),
            onEmailClicked = navController.getSafeNavigatorCallback(
                backStackEntry = backStackEntry,
                route = "$route/email",
            ),
        )
    }

    composable("$route/nickname") { backStackEntry ->
        val viewModel: UpdateProfileNicknameViewModel = hiltViewModel(backStackEntry)
        val context = LocalContext.current

        val nickname by viewModel.newNickname.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val isNicknameValid = nickname.length in 1..8

        LaunchedEffect(Unit) {
            viewModel.loadInitialNicknameIfNeeded()
        }

        val onNavigateToBack = navController.getSafeBackNavigatorCallback(backStackEntry)

        ProfileNicknameEditScreen(
            nickname = nickname,
            isDoneButtonEnabled = isNicknameValid,
            onBackButtonClicked = onNavigateToBack,
            onNicknameChanged = viewModel::onNicknameChanged,
            onDoneButtonClicked = {
                viewModel.runWithScope {
                    runCatching { viewModel.updateNickname() }
                        .onSuccess { onNavigateToBack() }
                        .onFailure { context.showToast("닉네임을 수정하지 못했습니다.") }
                }
            },
        )

        if (isLoading) LoadingModal()
    }

    composable("$route/email") { backStackEntry ->
        val viewModel: UpdateProfileEmailViewModel = hiltViewModel(backStackEntry)
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.loadInitialEmailIfNeeded()
        }

        val previousEmail by viewModel.previousEmail.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val email by viewModel.newEmail.collectAsState()
        val code by viewModel.emailVerificationCode.collectAsState()
        val isCodeSent by viewModel.isEmailCodeSent.collectAsState()
        val isTimeOut by viewModel.isEmailVerificationTimedOut.collectAsState()
        val isEmailDuplicated by viewModel.isEmailDuplicated.collectAsState()
        val remainTime by viewModel.emailVerificationRemainTime.collectAsState()
        val isEmailValid by viewModel.isEmailValid.collectAsState()
        val isDoneButtonEnabled by viewModel.isEmailDoneButtonEnabled.collectAsState()

        val onNavigateToBack = navController.getSafeBackNavigatorCallback(backStackEntry)

        ProfileEmailEditScreen(
            previousEmail = previousEmail,
            newEmail = email,
            code = code,
            codeSendingErrorMessage = if (isEmailDuplicated) "이미 가입된 이메일입니다." else null,
            remainTime = remainTime,
            showSendCodeButtonAsResend = isCodeSent,
            showTimeOutMessage = isTimeOut,
            isSendCodeButtonEnabled = isEmailValid && !isEmailDuplicated,
            isCodeFieldEditable = isCodeSent,
            isDoneButtonEnabled = isDoneButtonEnabled,
            onBackButtonClicked = onNavigateToBack,
            onNewEmailChanged = viewModel::onNewEmailChanged,
            onCodeChanged = viewModel::onEmailCodeChanged,
            onSendCodeButtonClicked = {
                viewModel.runWithScope {
                    runCatching { viewModel.requestVerificationCodeForUpdateEmail() }
                }
            },
            onDoneButtonClicked = {
                viewModel.runWithScope {
                    runCatching { verifyCodeForUpdateEmail() }
                        .onSuccess { isCorrect ->
                            if (isCorrect) onNavigateToBack()
                            else context.showToast("인증 코드가 일치하지 않습니다.")
                        }
                        .onFailure {
                            context.showToast("인증 코드를 전송하는 데 실패했습니다.")
                        }
                }
            },
        )

        if (isLoading) LoadingModal()
    }
}
