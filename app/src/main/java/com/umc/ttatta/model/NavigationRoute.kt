package com.umc.ttatta.model

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

sealed class NavigationRoute(
    val route: String,
) {
    fun NavGraphBuilder.setNavGraph(
        content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit),
    ) {
        composable(
            route = this@NavigationRoute.route,
            content = content,
        )
    }

    data object Splash : NavigationRoute("splash")
    data object Lock : NavigationRoute("lock")
    data object Login : NavigationRoute("login")
    data object Home : NavigationRoute("home")
    data object Footprint : NavigationRoute("footprint")
    data object Challenge : NavigationRoute("challenge")
    data object MyPage : NavigationRoute("my_page")
    data object Category : NavigationRoute("category")
    data object Record : NavigationRoute("record")
}
