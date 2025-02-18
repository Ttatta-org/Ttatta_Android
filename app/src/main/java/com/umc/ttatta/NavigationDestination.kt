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
import com.umc.record.RecordMode

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

    data object Challenge : NavigationRoute(
        route = "challenge?show_point_granted={show_point_granted}",
        arguments = listOf(navArgument("show_point_granted") { type = NavType.BoolType })
    ) {
        override fun getRoute(option: NavigationRouteOption): String {
            option as ChallengeRouteOption
            return "challenge?show_point_granted=${option.showPointGranted}"
        }

        override fun getOption(bundle: Bundle): NavigationRouteOption {
            return ChallengeRouteOption(
                showPointGranted = bundle.getBoolean("show_point_granted")
            )
        }
    }

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
        route = "record?entry_mode={entry_mode}&challenge_id={challenge_id}",
        arguments = listOf(
            navArgument("entry_mode") { type = NavType.StringType },
            navArgument("challenge_id") { type = NavType.LongType },
        )
    ) {
        override fun getRoute(option: NavigationRouteOption): String {
            option as RecordRouteOption
            return "record?entry_mode=${
                option.entryMode.name
            }&challenge_id=${
                option.challengeId ?: -1L
            }"
        }

        override fun getOption(bundle: Bundle): NavigationRouteOption {
            return RecordRouteOption(
                entryMode = when (bundle.getString("entry_mode") ?: "") {
                    RecordMode.CAMERA.name -> RecordMode.CAMERA
                    RecordMode.GALLERY.name -> RecordMode.GALLERY
                    else -> throw IllegalArgumentException()
                },
                challengeId = bundle.getLong("challenge_id").let {
                    if (it == -1L) null else it
                }
            )
        }
    }
}

data class ChallengeRouteOption(
    val showPointGranted: Boolean,
): NavigationRouteOption()

data class CategoryRouteOption(
    val showTopBar: Boolean,
) : NavigationRouteOption()

data class RecordRouteOption(
    val entryMode: RecordMode,
    val challengeId: Long?,
) : NavigationRouteOption()
