package com.umc.login

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.umc.login.FindId.FindIdScreen
import com.umc.login.FindId.FindIdViewModel
import com.umc.login.FindPw.FindPwScreen1
import com.umc.login.FindPw.FindPwScreen2
import com.umc.login.Join.JoinFinalScreen
import com.umc.login.Join.JoinParentScreen
import com.umc.login.Join.JoinViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNavigation(navController: NavHostController,
                   loginViewModel: LoginViewModel,
                   joinViewModel: JoinViewModel,
                   findIdViewModel: FindIdViewModel,
                   onNavigatingToHome: () -> Unit) {
    NavHost(
        navController = navController,
        startDestination = "login",
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
            FindIdScreen(navController, findIdViewModel)
        }
        composable("find_pw") {
            FindPwScreen1(navController)
        }
        composable("find_pw2") {
            FindPwScreen2(navController)
        }
    }
}
