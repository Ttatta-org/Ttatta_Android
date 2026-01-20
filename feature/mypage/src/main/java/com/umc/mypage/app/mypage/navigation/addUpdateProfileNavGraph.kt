package com.umc.mypage.app.mypage.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.umc.mypage.app.mypage.MyPageViewModel
import com.umc.mypage.screen.ProfileEditScreen
import com.umc.mypage.screen.ProfileEmailEditScreen
import com.umc.mypage.screen.ProfileNicknameEditScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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
        var nickname by remember { mutableStateOf("") }

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
                // TODO
            },
        )
    }

    composable("$route/email") {
        val userInfo by viewModel.userInfoState.collectAsState()

        var email by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var isCodeSent by remember { mutableStateOf(false) }
        var isEmailDuplicated by remember { mutableStateOf(false) }

        val isEmailValid = remember(email) {
            val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
            emailRegex.matches(email)
        }

        ProfileEmailEditScreen(
            previousEmail = userInfo?.email ?: "",
            newEmail = email,
            code = code,
            codeSendingErrorMessage = if (isEmailDuplicated) "이미 가입된 이메일입니다." else null,
            remainTime = null,
            showSendCodeButtonAsResend = isCodeSent,
            isSendCodeButtonEnabled = isEmailValid && !isEmailDuplicated,
            isCodeFieldEditable = isCodeSent,
            isDoneButtonEnabled = code.length == 6,
            onBackButtonClicked = {
                MainScope().launch { navController.popBackStack() }
            },
            onNewEmailChanged = { email = it },
            onCodeChanged = { code = it },
            onSendCodeButtonClicked = {
                // TODO
            },
            onDoneButtonClicked = {
                // TODO
            },
        )
    }
}