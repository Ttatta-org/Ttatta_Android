package com.umc.ttatta

import android.os.Bundle
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

sealed class NavigationRouteOption

sealed class NavigationRoute private constructor(
    val route: String,
    private val arguments: List<NamedNavArgument> = listOf()
) {
    open fun getRoute(option: NavigationRouteOption): String = route
    open fun getOption(bundle: Bundle): NavigationRouteOption? = null

    fun NavGraphBuilder.setNavGraph(
        content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit)
    ) {
        composable(
            route = this@NavigationRoute.route,
            arguments = arguments,
            content = content
        )
    }

    data object Splash : NavigationRoute("splash")
    data object Login : NavigationRoute("login")
    data object Home : NavigationRoute("home")
    data object Footprint : NavigationRoute("footprint")
    data object Challenge : NavigationRoute("challenge")
    data object MyPage : NavigationRoute("my_page")

    data object Category : NavigationRoute(
        route = "category?show_top_bar={show_top_bar}",
        arguments = listOf(navArgument("show_top_bar") { type = NavType.BoolType })
    ) {
        override fun getRoute(option: NavigationRouteOption): String {
            option as CategoryRouteOption
            return "category?show_top_bar=${option.showTopBar}"
        }

        override fun getOption(bundle: Bundle): NavigationRouteOption {
            return CategoryRouteOption(
                showTopBar = bundle.getBoolean("show_top_bar")
            )
        }
    }

    data object Record : NavigationRoute(
        route = "record?entry_mode={entry_mode}",
        arguments = listOf(navArgument("entry_mode") { type = NavType.StringType })
    ) {
        override fun getRoute(option: NavigationRouteOption): String {
            option as RecordRouteOption
            return "record?entry_mode=${option.entryMode}"
        }

        override fun getOption(bundle: Bundle): NavigationRouteOption {
            return RecordRouteOption(
                entryMode = bundle.getString("entry_mode") ?: ""
            )
        }
    }
}

data class CategoryRouteOption(
    val showTopBar: Boolean,
) : NavigationRouteOption()

data class RecordRouteOption(
    val entryMode: String
) : NavigationRouteOption()
