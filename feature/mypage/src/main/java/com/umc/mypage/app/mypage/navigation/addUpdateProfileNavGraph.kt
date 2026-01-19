package com.umc.mypage.app.mypage.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.umc.mypage.app.mypage.MyPageViewModel

fun NavGraphBuilder.addUpdateProfileNavGraph(
    route: String,
    viewModel: MyPageViewModel,
    navController: NavController,
) = navigation(
    startDestination = "$route/info",
    route = route,
) {
    composable("$route/info") {

    }

    composable("$route/nickname") {

    }

    composable("$route/email") {

    }
}