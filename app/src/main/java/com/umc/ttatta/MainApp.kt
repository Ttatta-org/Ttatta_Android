package com.umc.ttatta

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umc.category.CategoryApp
import com.umc.challenge.ChallengeApp
import com.umc.footprint.FootprintApp
import com.umc.home.HomeApp
import com.umc.mypage.MyPageApp
import com.umc.ttatta.component.NavigationItem
import com.umc.ttatta.component.RecordOptionPickerProp

@Composable
fun MainApp(
    viewModel: MainViewModel
) {
    val navigator = rememberNavController()

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    var currentNavigationItem by remember { mutableStateOf(NavigationItem.DIARY) }
    var showNavBar by remember { mutableStateOf(false) }
    var isCenterButtonActivated by remember { mutableStateOf(false) }

    MainScreen(
        navigationBarProp = if (showNavBar) NavigationBarProp(
            currentNavigationItem = currentNavigationItem,
            onNavigate = {
                val route = when (it) {
                    NavigationItem.DIARY -> HomeRoute
                    NavigationItem.FOOTPRINT -> FootprintRoute
                    NavigationItem.CHALLENGE -> ChallengeRoute
                    NavigationItem.MY_PAGE -> MyPageRoute
                }
                navigator.navigate(route = route.route) {
                    popUpTo(id = navigator.graph.startDestinationId) {
                        inclusive = true
                    }
                }
                currentNavigationItem = it
            },
            onCenterButtonClicked = { isCenterButtonActivated = true }
        ) else null,
        centerButtonProp = if (isCenterButtonActivated) CenterButtonProp(
            recordOptionPickerProp = RecordOptionPickerProp(
                userName = viewModel.userName,
                onCameraOptionClicked = {
                    navigator.navigate(
                        route = RecordRoute.getRoute(
                            option = RecordRouteOption(entryMode = "camera")
                        )
                    )
                },
                onGalleryOptionClicked = {
                    navigator.navigate(
                        route = RecordRoute.getRoute(
                            option = RecordRouteOption(entryMode = "gallery")
                        )
                    )
                }
            ),
            onDismissed = { isCenterButtonActivated = false }
        ) else null,
    ) {
        NavHost(
            navController = navigator,
            startDestination = SplashRoute.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(
                route = SplashRoute.route,
                arguments = SplashRoute.arguments,
            ) {
                LaunchedEffect(Unit) { showNavBar = false }

                // TODO: 여기에 스플래시 화면 구현
            }

            composable(
                route = LoginRoute.route,
                arguments = LoginRoute.arguments,
            ) {
                LaunchedEffect(Unit) { showNavBar = false }

                // LoginApp(
                //     viewModel = hiltViewModel()
                // )
            }

            composable(
                route = HomeRoute.route,
                arguments = HomeRoute.arguments,
            ) {
                LaunchedEffect(Unit) { showNavBar = true }

                HomeApp(
                    viewModel = hiltViewModel(),
                )
            }

            composable(
                route = FootprintRoute.route,
                arguments = FootprintRoute.arguments,
            ) {
                LaunchedEffect(Unit) { showNavBar = true }

                FootprintApp(
                    viewModel = hiltViewModel(),
                    onNavigateToCategoryApp = {
                        navigator.navigate(
                            route = CategoryRoute.getRoute(
                                option = CategoryRouteOption(showTopBar = true)
                            )
                        )
                    }
                )
            }

            composable(
                route = ChallengeRoute.route,
                arguments = ChallengeRoute.arguments,
            ) {
                LaunchedEffect(Unit) { showNavBar = true }

                ChallengeApp(
                    viewModel = hiltViewModel()
                )
            }

            composable(
                route = MyPageRoute.route,
                arguments = MyPageRoute.arguments,
            ) {
                LaunchedEffect(Unit) { showNavBar = true }

                MyPageApp(
                    viewModel = hiltViewModel()
                )
            }

            composable(
                route = CategoryRoute.route,
                arguments = CategoryRoute.arguments,
            ) {
                CategoryApp(
                    viewModel = hiltViewModel(),
                    showTopBar = true,
                )
            }

            composable(
                route = RecordRoute.route,
                arguments = RecordRoute.arguments,
            ) { backStackEntry ->
                LaunchedEffect(Unit) { showNavBar = false }
                val option = RecordRoute.getOption(backStackEntry.arguments!!) as RecordRouteOption

                // RecordApp(
                //     viewModel = hiltViewModel()
                // )
            }
        }
    }

    LaunchedEffect(key1 = isLoggedIn) {
        when (isLoggedIn) {
            true -> navigator.navigate(HomeRoute.route)
            false -> navigator.navigate(LoginRoute.route)
            null -> navigator.navigate(SplashRoute.route)
        }
    }
}