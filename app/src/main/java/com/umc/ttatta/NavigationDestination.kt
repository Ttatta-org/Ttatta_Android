package com.umc.ttatta

import android.os.Bundle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class NavigationRouteOption

sealed class NavigationRoute(
    val route: String,
    val arguments: List<NamedNavArgument> = listOf()
) {
    open fun getRoute(option: NavigationRouteOption? = null): String = route
    open fun getOption(bundle: Bundle): NavigationRouteOption? = null
}

data object SplashRoute: NavigationRoute("splash")
data object LoginRoute: NavigationRoute("login")
data object HomeRoute: NavigationRoute("home")
data object FootprintRoute: NavigationRoute("footprint")
data object ChallengeRoute: NavigationRoute("challenge")
data object MyPageRoute: NavigationRoute("my_page")

data object CategoryRoute: NavigationRoute(
    route = "category?show_top_bar={show_top_bar}",
    arguments = listOf(navArgument("show_top_bar") { type = NavType.BoolType })
) {
    override fun getRoute(option: NavigationRouteOption?): String {
        option as CategoryRouteOption
        return "category?show_top_bar=${option.showTopBar}"
    }

    override fun getOption(bundle: Bundle): NavigationRouteOption {
        return CategoryRouteOption(
            showTopBar = bundle.getBoolean("show_top_bar")
        )
    }
}

data object RecordRoute: NavigationRoute(
    route = "record?entry_mode={entry_mode}",
    arguments = listOf(navArgument("entry_mode") { type = NavType.StringType })
) {
    override fun getRoute(option: NavigationRouteOption?): String {
        option as RecordRouteOption
        return "record?entry_mode=${option.entryMode}"
    }

    override fun getOption(bundle: Bundle): NavigationRouteOption {
        return RecordRouteOption(
            entryMode = bundle.getString("entry_mode") ?: ""
        )
    }
}

data class CategoryRouteOption(
    val showTopBar: Boolean,
): NavigationRouteOption()

data class RecordRouteOption(
    val entryMode: String
): NavigationRouteOption()
