package com.umc.mypage.app.mypage.signout

import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.umc.core.util.runWithScope
import com.umc.mypage.screen.SignOutScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.addSignOutNavGraph(
    route: String,
    navController: NavController,
    onLoginCanceled: () -> Unit
) = navigation(
    startDestination = "${route}/form",
    route = route,
) {
    composable("${route}/form") { backStackEntry ->
        val viewModel: SignOutViewModel = hiltViewModel(backStackEntry)

        SignOutScreen(
            onLeaveUser = { reason ->
                viewModel.runWithScope {
                    runCatching { leaveUser(reason = reason) }
                        .onSuccess { onLoginCanceled() }
                        .onFailure { e -> Log.e("SignOut", "❌ 탈퇴 실패: $e") }
                }
            },
            onCancel = {
                MainScope().launch { navController.popBackStack() }
            },
        )
    }
}