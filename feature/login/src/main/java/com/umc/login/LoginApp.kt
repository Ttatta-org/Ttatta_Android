package com.umc.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginApp(viewModel: LoginViewModel) {
    val navController = rememberNavController()
    MainNavigation(navController)
}
