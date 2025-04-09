package com.umc.ttatta

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import java.io.File

sealed class NavigationRoute(
    val route: String,
) {
    fun NavGraphBuilder.setNavGraph(
        content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit),
    ) {
        composable(
            route = this@NavigationRoute.route, content = content
        )
    }

    data object Splash: NavigationRoute("splash")
    data object Login: NavigationRoute("login")
    data object Home: NavigationRoute("home")
    data object Footprint: NavigationRoute("footprint")
    data object Challenge: NavigationRoute("challenge")
    data object MyPage: NavigationRoute("my_page")
    data object Category: NavigationRoute("category")
    data object Record: NavigationRoute("record")
}

enum class RecordRoutingOption {
    CAMERA,
    GALLERY,
}

class LoginRoutingInfo(val empty: Any? = null)
class HomeRoutingInfo(val empty: Any? = null)
class FootprintRoutingInfo(val empty: Any? = null)
class MyPageRoutingInfo(val empty: Any? = null)

class ChallengeRoutingInfo(
    val isPointGranted: Boolean,
    val isPoppedFromRecord: Boolean,
)

class RecordRoutingInfo(
    val image: File,
    val challengeId: Long?,
)

class CategoryRoutingInfo(
    val showTopBar: Boolean,
)

class RecordEntryInfo(
    val mode: RecordRoutingOption,
    val challengeId: Long?,
)

