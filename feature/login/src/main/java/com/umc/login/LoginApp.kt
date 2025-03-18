package com.umc.login

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.umc.login.navigation.addFindingIdNavGraph
import com.umc.login.navigation.addFindingPasswordNavGraph
import com.umc.login.navigation.addJoinNavGraph
import com.umc.login.navigation.addKakaoLoginNavGraph
import com.umc.login.navigation.addLoginNavGraph

@Composable
fun LoginApp(
    viewModel: LoginViewModel,
    onNavigatingToHome: () -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.background(color = Color.White)
    ) {
        addLoginNavGraph(
            viewModel = viewModel,
            onNavigatingToHome = onNavigatingToHome,
            onNavigatingToJoin = { navController.navigate("join") },
            onNavigatingToFindingId = { navController.navigate("find_id") },
            onNavigatingToFindingPassword = { navController.navigate("find_password") },
            onNavigatingToKakaoLogin = { navController.navigate("kakao_login") },
        )

        addJoinNavGraph(
            viewModel = viewModel,
            onNavigatingBackToLogin = {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            },
            onNavigatingToJoinDone = { name ->
                navController.navigate("join_done?name=$name") {
                    popUpTo("login") { inclusive = true }
                }
            },
        )

        addFindingIdNavGraph(
            viewModel = viewModel,
            onNavigatingBackToLogin = {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            },
            onNavigatingToFindingIdDone = { id, name ->
                navController.navigate("find_id_done?id=$id&name=$name") {
                    popUpTo("login") { inclusive = true }
                }
            },
            onNavigatingToFindingPassword = {
                navController.navigate("find_password") {
                    popUpTo("login") { inclusive = true }
                }
            }
        )

        addFindingPasswordNavGraph(
            viewModel = viewModel,
            onNavigatingBackToLogin = {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            },
        )

        addKakaoLoginNavGraph(
            viewModel = viewModel,
            onNavigatingToHome = onNavigatingToHome,
            onNavigatingBackToLogin = {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            },
        )
    }
}
