package com.umc.login.navigation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.umc.core.util.runWithScope
import com.umc.design.component.LoadingModal
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
        var showLoading by remember { mutableStateOf(false) }

        LoginScreen(
            id = id,
            password = password,
            isPasswordVisible = isPasswordVisible,
            isLoginErrorOccurred = isErrorOccurred,
            onIdChanged = { id = it },
            onPasswordChanged = { password = it },
            onPasswordVisibilityChanged = { isPasswordVisible = it },
            onLoginButtonClicked = {
                viewModel.runWithScope {
                    showLoading = true

                    runCatching {
                        login(id = id, password = password)
                    }.onSuccess {
                        onNavigatingToHome()
                    }.onFailure {
                        Log.e("LoginApp", "error: $it")
                        isErrorOccurred = true
                    }

                    showLoading = false
                }
            },
            onKakaoLoginButtonClicked = onNavigatingToKakaoLogin,
            onFindIdButtonClicked = onNavigatingToFindingId,
            onFindPasswordButtonClicked = onNavigatingToFindingPassword,
            onJoinButtonClicked = onNavigatingToJoin,
        )

        if (showLoading) LoadingModal()
    }
}