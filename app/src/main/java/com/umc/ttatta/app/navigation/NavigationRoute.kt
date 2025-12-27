package com.umc.ttatta.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationRoute {

    @Serializable
    data object Splash : NavigationRoute()

    @Serializable
    data object Lock : NavigationRoute()

    @Serializable
    data object Login : NavigationRoute()

    @Serializable
    open class Home : NavigationRoute() {

        @Serializable
        data object Home : NavigationRoute.Home()

        @Serializable
        data object Category : NavigationRoute.Home()
    }

    @Serializable
    open class Footprint : NavigationRoute() {

        @Serializable
        data object Footprint : NavigationRoute.Footprint()

        @Serializable
        data object Category : NavigationRoute.Footprint()
    }

    @Serializable
    data object Challenge : NavigationRoute()

    @Serializable
    data object MyPage : NavigationRoute()

    @Serializable
    open class Record : NavigationRoute() {

        @Serializable
        data object Nothing: NavigationRoute.Record()

        @Serializable
        data object Record : NavigationRoute.Record()

        @Serializable
        data object Category : NavigationRoute.Record()
    }
}
