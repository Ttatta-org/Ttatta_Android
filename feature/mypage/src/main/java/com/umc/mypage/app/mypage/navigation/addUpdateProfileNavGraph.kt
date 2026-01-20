package com.umc.mypage.app.mypage.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.umc.core.util.runWithScope
import com.umc.core.util.showToast
import com.umc.design.component.LoadingModal
import com.umc.mypage.app.mypage.MyPageViewModel
import com.umc.mypage.screen.ProfileEditScreen
import com.umc.mypage.screen.ProfileEmailEditScreen
import com.umc.mypage.screen.ProfileNicknameEditScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

fun NavGraphBuilder.addUpdateProfileNavGraph(
    route: String,
    viewModel: MyPageViewModel,
    navController: NavController,
) = navigation(
    startDestination = "$route/info",
    route = route,
) {
    composable("$route/info") {
        val userInfo by viewModel.userInfoState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.loadUserInfo()
        }

        ProfileEditScreen(
            nickname = userInfo?.name,
            email = userInfo?.email,
            onBackButtonClicked = {
                MainScope().launch { navController.popBackStack() }
            },
            onNicknameClicked = {
                MainScope().launch { navController.navigate("$route/nickname") }
            },
            onEmailClicked = {
                MainScope().launch { navController.navigate("$route/email") }
            }
        )
    }

    composable("$route/nickname") {
        val context = LocalContext.current

        var nickname by remember { mutableStateOf("") }
        val isLoading by viewModel.isLoading.collectAsState()

        val isNicknameValid = remember(nickname) {
            nickname.isNotBlank()
        }

        ProfileNicknameEditScreen(
            nickname = nickname,
            isDoneButtonEnabled = isNicknameValid,
            onBackButtonClicked = {
                MainScope().launch { navController.popBackStack() }
            },
            onNicknameChanged = { nickname = it },
            onDoneButtonClicked = {
                viewModel.runWithScope {
                    runCatching { viewModel.updateNickname(nickname) }
                        .onSuccess { MainScope().launch { navController.popBackStack() } }
                        .onFailure { context.showToast("닉네임을 수정하지 못했습니다.") }
                }
            },
        )

        if (isLoading) LoadingModal()
    }

    composable("$route/email") {
        val context = LocalContext.current

        val userInfo by viewModel.userInfoState.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        var email by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var isCodeSent by remember { mutableStateOf(false) }
        var isTimeOut by remember { mutableStateOf(false) }
        var isEmailDuplicated by remember { mutableStateOf(false) }
        var emailSentTime: LocalDateTime? by remember { mutableStateOf(null) }
        var remainTime: Duration? by remember { mutableStateOf(null) }

        val isEmailValid = remember(email) {
            val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
            emailRegex.matches(email)
        }

        LaunchedEffect(email) {
            isCodeSent = false
            emailSentTime = null
            code = ""
        }

        LaunchedEffect(emailSentTime) {
            emailSentTime?.let {
                while (true) {
                    val remain = Duration.between(LocalDateTime.now(), it.plusMinutes(3))

                    if (remain.isNegative) {
                        isTimeOut = true
                        isCodeSent = false
                        emailSentTime = null
                        code = ""
                        remainTime = null
                        break
                    }

                    remainTime = remain
                    delay(100)
                }
            }
        }

        ProfileEmailEditScreen(
            previousEmail = userInfo?.email ?: "",
            newEmail = email,
            code = code,
            codeSendingErrorMessage = if (isEmailDuplicated) "이미 가입된 이메일입니다." else null,
            remainTime = remainTime,
            showSendCodeButtonAsResend = isCodeSent,
            showTimeOutMessage = isTimeOut,
            isSendCodeButtonEnabled = isEmailValid && !isEmailDuplicated,
            isCodeFieldEditable = isCodeSent,
            isDoneButtonEnabled = code.length == 6,
            onBackButtonClicked = {
                MainScope().launch { navController.popBackStack() }
            },
            onNewEmailChanged = { email = it },
            onCodeChanged = { code = it },
            onSendCodeButtonClicked = {
                viewModel.runWithScope {
                    runCatching { viewModel.requestVerificationCodeForUpdateEmail(email) }
                        .onSuccess {
                            isCodeSent = true
                            emailSentTime = LocalDateTime.now()
                            isEmailDuplicated = false
                        }
                        .onFailure {
                            // TODO: 이메일 중복 여부 및 실패 여부 판별
                            isEmailDuplicated = true
                        }
                }
            },
            onDoneButtonClicked = {
                viewModel.runWithScope {
                    runCatching { verifyCodeForUpdateEmail(email, code) }
                        .onSuccess { isCorrect ->
                            if (isCorrect) MainScope().launch { navController.popBackStack() }
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