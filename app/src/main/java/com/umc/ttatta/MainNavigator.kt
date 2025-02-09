package com.umc.ttatta

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umc.category.CategoryApp
import com.umc.challenge.ChallengeApp
import com.umc.footprint.FootprintApp
import com.umc.home.HomeApp
import com.umc.login.LoginApp

@Composable
fun MainNavigator(
    viewModel: MainViewModel,
    globalNavigator: NavHostController,
) {
    LaunchedEffect(key1 = viewModel.isLoggedIn) {
        viewModel.isLoggedIn?.let {
            if (it)
                globalNavigator.navigate(HomeRoute.route)
            else
                globalNavigator.navigate(LoginRoute.route)
        } ?: run {
            globalNavigator.navigate(SplashRoute.route)
        }
    }

    NavHost(
        navController = globalNavigator,
        startDestination = SplashRoute.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(
            route = SplashRoute.route,
            arguments = SplashRoute.arguments,
        ) {
            // TODO: 여기에 스플래시 화면 구현
        }

        composable(
            route = LoginRoute.route,
            arguments = LoginRoute.arguments,
        ) {
            LoginApp(
                viewModel = hiltViewModel()
            )
        }

        composable(
            route = MainRoute.route,
        ) {
            val mainNavigator = rememberNavController()

            var currentRoute by remember { mutableStateOf<NavigationRoute>(HomeRoute) }
            var showNavBar by remember { mutableStateOf(true) }
            var showRecordDialog by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                NavHost(
                    navController = mainNavigator,
                    startDestination = HomeRoute.route,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(
                        route = HomeRoute.route,
                        arguments = HomeRoute.arguments,
                    ) {
                        val homeNavigator = rememberNavController()

                        homeNavigator.addOnDestinationChangedListener { _, destination, _ ->
                            showNavBar = destination.route == "home"
                        }

                        HomeApp(
                            viewModel = hiltViewModel(),
                            navController = homeNavigator
                        )
                    }

                    composable(
                        route = FootprintRoute.route,
                        arguments = FootprintRoute.arguments,
                    ) {
                        FootprintApp(
                            viewModel = hiltViewModel(),
                            onNavigateToCategoryApp = {
                                mainNavigator.navigate(CategoryRoute.route)
                            }
                        )
                    }

                    composable(
                        route = ChallengeRoute.route,
                        arguments = ChallengeRoute.arguments,
                    ) {
                        ChallengeApp(
                            viewModel = hiltViewModel()
                        )
                    }

                    composable(
                        route = MyPageRoute.route,
                        arguments = MyPageRoute.arguments,
                    ) {
                        // MyPageApp()
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
                }

                if (showNavBar) MainNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { dest ->
                        when (dest) {
                            HomeRoute,
                            FootprintRoute,
                            ChallengeRoute,
                            MyPageRoute -> {
                                currentRoute = dest
                                mainNavigator.navigate(HomeRoute.route) {
                                    popUpTo(mainNavigator.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            }
                            RecordRoute -> { showRecordDialog = true }
                            else -> throw Exception("Unknown destination: $dest")
                        }
                    }
                )

                if (showRecordDialog) RecordDialog(
                    userName = viewModel.userName,
                    onDismissed = { showRecordDialog = false },
                    onCameraOptionClicked = {
                        RecordRoute.getRoute(
                            option = RecordRouteOption(entryMode = "camera")
                        ).let { globalNavigator.navigate(it) }
                    },
                    onGalleryOptionClicked = {
                        RecordRoute.getRoute(
                            option = RecordRouteOption(entryMode = "gallery")
                        ).let { globalNavigator.navigate(it) }
                    }
                )
            }
        }

        composable(
            route = RecordRoute.route,
            arguments = RecordRoute.arguments,
        ) { backStackEntry ->
            val option = RecordRoute.getOption(backStackEntry.arguments!!) as RecordRouteOption

            // RecordApp(
            //     viewModel = hiltViewModel()
            // )
        }

        composable(
            route = CategoryRoute.route,
            arguments = CategoryRoute.arguments,
        ) {
            CategoryApp(
                viewModel = hiltViewModel(),
                showTopBar = false,
            )
        }
    }
}