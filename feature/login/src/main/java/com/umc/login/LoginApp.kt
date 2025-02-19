package com.umc.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.umc.login.FindId.FindIdViewModel
import com.umc.login.Join.JoinViewModel

@Composable
fun LoginApp(
    loginviewModel: LoginViewModel,
    joinviewModel: JoinViewModel,
    findIdViewModel: FindIdViewModel,
    onNavigatingToHome: () -> Unit) {
    val navController = rememberNavController()
    MainNavigation(navController, loginviewModel, joinviewModel,findIdViewModel, onNavigatingToHome)
}
