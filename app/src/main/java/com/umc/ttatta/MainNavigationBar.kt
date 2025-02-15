package com.umc.ttatta

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.umc.design.BottomNavigationBar
import com.umc.design.NavigationItem

@Composable
fun MainNavigationBar(
    currentRoute: NavigationRoute,
    onNavigate: (NavigationRoute) -> Unit,
) {
    BottomNavigationBar(
        selectedTab = when (currentRoute) {
            HomeRoute -> NavigationItem.DIARY
            FootprintRoute -> NavigationItem.FOOTPRINT
            ChallengeRoute -> NavigationItem.CHALLENGE
            MyPageRoute -> NavigationItem.MY_PAGE
            else -> throw Exception("Unknown route: $currentRoute")
        },
        onTabSelected = {
            val route = when (it) {
                NavigationItem.DIARY -> HomeRoute
                NavigationItem.FOOTPRINT -> FootprintRoute
                NavigationItem.CHALLENGE -> ChallengeRoute
                NavigationItem.MY_PAGE -> MyPageRoute
            }
            onNavigate(route)
        },
        onFabClick = {
            onNavigate(RecordRoute)
        }
    )
}

@Preview
@Composable
fun PreviewBottomNavigationBar() {
    MainNavigationBar(
        currentRoute = HomeRoute,
        onNavigate = {}
    )
}