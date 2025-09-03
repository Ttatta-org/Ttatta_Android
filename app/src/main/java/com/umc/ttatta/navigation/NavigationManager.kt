package com.umc.ttatta.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object NavigationManager {
    val initialRoute get() = NavigationRoute.SPLASH.name

    fun <T : NavigationRoute> NavGraphBuilder.setNavGraph(
        route: T,
        content: @Composable (T) -> Unit,
    ) {
        composable(
            route = route.name,
            content = { navBackStackEntry ->
                content.invoke(route)
            },
        )
    }

    fun NavController.push(route: NavigationRoute) {
        navigate(route.name)
    }

    fun NavController.go(route: NavigationRoute) {
        navigate(route.name) {
            popUpTo(id = graph.startDestinationId) { inclusive = false }
        }
    }

    fun NavController.setOnRouteChange(listener: (NavigationRoute) -> Unit) {
        addOnDestinationChangedListener { _, destination, _ ->
            val route = NavigationRoute.valueOf(destination.route ?: return@addOnDestinationChangedListener)
            listener(route)
        }
    }
}