package com.umc.login.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.umc.login.LoginViewModel
import com.umc.login.screen.LoginScreen

fun NavGraphBuilder.addLoginNavGraph(
    viewModel: LoginViewModel,
    onNavigatingToHome: () -> Unit,
    onNavigatingToJoin: () -> Unit,
    onNavigatingToKakaoLogin: () -> Unit,
    onNavigatingToFindingId: () -> Unit,
    onNavigatingToFindingPassword: () -> Unit,
) {
    composable(
        route = "login"
    ) {
        var id by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }
        var isErrorOccurred by remember { mutableStateOf(false) }

        LoginScreen(
            id = id,
            password = password,
            isPasswordVisible = isPasswordVisible,
            isLoginErrorOccurred = isErrorOccurred,
            onIdChanged = { id = it },
            onPasswordChanged = { password = it },
            onPasswordVisibilityChanged = { isPasswordVisible = it },
            onLoginButtonClicked = {
                viewModel.login(
                    id = id,
                    password = password,
                    onSucceed = onNavigatingToHome,
                    onFailed = { isErrorOccurred = true }
                )
            },
            onKakaoLoginButtonClicked = onNavigatingToKakaoLogin,
            onFindIdButtonClicked = onNavigatingToFindingId,
            onFindPasswordButtonClicked = onNavigatingToFindingPassword,
            onJoinButtonClicked = onNavigatingToJoin,
        )
    }
}