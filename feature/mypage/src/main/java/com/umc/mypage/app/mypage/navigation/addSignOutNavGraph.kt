package com.umc.mypage.app.mypage.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.umc.mypage.app.mypage.MyPageViewModel
import com.umc.mypage.screen.SignOutScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.addSignOutNavGraph(
    route: String,
    viewModel: MyPageViewModel,
    navController: NavController,
    onLoginCanceled: () -> Unit
) = navigation(
    startDestination = "${route}/form",
    route = route,
) {
    composable("${route}/form") {
        SignOutScreen(
            onLeaveUser = { reason ->
                viewModel.leaveUser(
                    reason = reason,
                    onSuccess = onLoginCanceled,
                    onError = { errorMsg ->
                        Log.e("SignOut", "❌ 탈퇴 실패: $errorMsg")
                    },
                )
            },
            onCancel = {
                MainScope().launch { navController.popBackStack() }
            },
        )
    }
}