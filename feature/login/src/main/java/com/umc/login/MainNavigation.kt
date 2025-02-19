package com.umc.login

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.umc.login.FindId.FindIdScreen
import com.umc.login.FindPw.FindPwScreen1
import com.umc.login.FindPw.FindPwScreen2
import com.umc.login.Join.JoinFinalScreen
import com.umc.login.Join.JoinParentScreen
import com.umc.login.Join.JoinViewModel

@Composable
fun MainNavigation(navController: NavHostController,
                   loginViewModel: LoginViewModel,
                   joinViewModel: JoinViewModel,
                   onNavigatingToHome: () -> Unit) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController, loginViewModel, onNavigatingToHome)
        }
        composable("join") {
            JoinParentScreen(navController, joinViewModel)
        }
        composable("join_end") {
            JoinFinalScreen(navController, joinViewModel)
        }
        composable("find_id") {
            FindIdScreen(navController)
        }
        composable("find_pw") {
            FindPwScreen1(navController)
        }
        composable("find_pw2") {
            FindPwScreen2(navController)
        }
    }
}
