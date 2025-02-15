package com.umc.ttatta

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
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
    var currentNavigationItem by remember { mutableStateOf<NavigationItem?>(null) }
    var showNavBar by remember { mutableStateOf(false) }
    var isCenterButtonActivated by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        navigator.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.route) {
                NavigationRoute.Home.route -> NavigationItem.DIARY
                NavigationRoute.Footprint.route -> NavigationItem.FOOTPRINT
                NavigationRoute.Challenge.route -> NavigationItem.CHALLENGE
                NavigationRoute.MyPage.route -> NavigationItem.MY_PAGE
                else -> null
            }?.let {
                currentNavigationItem = it
            }
        }
    }

    MainScreen(
        navigationBarProp = if (showNavBar) NavigationBarProp(
            currentNavigationItem = currentNavigationItem,
            onNavigate = {
                navigator.navigate(
                    route = when (it) {
                        NavigationItem.DIARY -> NavigationRoute.Home
                        NavigationItem.FOOTPRINT -> NavigationRoute.Footprint
                        NavigationItem.CHALLENGE -> NavigationRoute.Challenge
                        NavigationItem.MY_PAGE -> NavigationRoute.MyPage
                    }.route
                ) {
                    popUpTo(id = navigator.graph.startDestinationId) { inclusive = false }
                }
            },
            onCenterButtonClicked = { isCenterButtonActivated = true }
        ) else null,
        centerButtonProp = if (isCenterButtonActivated) {
            val routeToRecordApp = { mode: String ->
                isCenterButtonActivated = false
                navigator.navigate(
                    route = NavigationRoute.Record.getRoute(
                        option = RecordRouteOption(entryMode = mode)
                    )
                )
            }

            CenterButtonProp(
                recordOptionPickerProp = RecordOptionPickerProp(
                    userName = viewModel.userName,
                    onCameraOptionClicked = { routeToRecordApp("camera") },
                    onGalleryOptionClicked = { routeToRecordApp("gallery") }
                ),
                onDismissed = { isCenterButtonActivated = false }
            )
        } else null,
    ) {
        NavHost(
            navController = navigator,
            startDestination = NavigationRoute.Splash.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
            modifier = Modifier.fillMaxSize()
        ) {
            with(NavigationRoute.Splash) {
                setNavGraph {
                    LaunchedEffect(Unit) { showNavBar = false }

                    // TODO: 여기에 스플래시 화면 구현
                }
            }

            with(NavigationRoute.Login) {
                setNavGraph {
                    LaunchedEffect(Unit) { showNavBar = false }

                    // LoginApp(
                    //     viewModel = hiltViewModel()
                    // )
                }
            }

            with(NavigationRoute.Home) {
                setNavGraph {
                    LaunchedEffect(Unit) { showNavBar = true }
                    FinishHandler()

                    HomeApp(
                        viewModel = hiltViewModel(),
                    )
                }
            }

            with(NavigationRoute.Footprint) {
                setNavGraph {
                    LaunchedEffect(Unit) { showNavBar = true }
                    FinishHandler()

                    FootprintApp(
                        viewModel = hiltViewModel(),
                        isMapBlurApplied = isCenterButtonActivated,
                        onNavigateToCategoryApp = {
                            navigator.navigate(
                                route = NavigationRoute.Category.getRoute(
                                    option = CategoryRouteOption(showTopBar = true)
                                )
                            )
                        }
                    )
                }
            }

            with(NavigationRoute.Challenge) {
                setNavGraph {
                    LaunchedEffect(Unit) { showNavBar = true }
                    FinishHandler()

                    ChallengeApp(
                        viewModel = hiltViewModel()
                    )
                }
            }

            with(NavigationRoute.MyPage) {
                setNavGraph {
                    LaunchedEffect(Unit) { showNavBar = true }
                    FinishHandler()

                    MyPageApp(
                        viewModel = hiltViewModel()
                    )
                }
            }

            with(NavigationRoute.Category) {
                setNavGraph { backStackEntry ->
                    val option = getOption(backStackEntry.arguments!!) as CategoryRouteOption

                    CategoryApp(
                        viewModel = hiltViewModel(),
                        showTopBar = option.showTopBar,
                    )
                }
            }

            with(NavigationRoute.Record) {
                setNavGraph { backStackEntry ->
                    LaunchedEffect(Unit) { showNavBar = false }
                    val option = getOption(backStackEntry.arguments!!) as RecordRouteOption

                    // RecordApp(
                    //     viewModel = hiltViewModel()
                    // )
                }
            }
        }
    }

    LaunchedEffect(key1 = isLoggedIn) {
        isLoggedIn?.let { isLoggedIn ->
            navigator.navigate(
                route = when (isLoggedIn) {
                    true -> NavigationRoute.Home
                    false -> NavigationRoute.Login
                }.route
            ) {
                popUpTo(id = navigator.graph.startDestinationId) { inclusive = false }
            }
        } ?: run {
            navigator.popBackStack(
                destinationId = navigator.graph.startDestinationId,
                inclusive = false,
            )
        }
    }
}

@Composable
private fun FinishHandler() {
    val context = LocalContext.current as Activity
    var backPressedTime by remember { mutableLongStateOf(0L) }

    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000L)
            context.finish()
        else
            Toast.makeText(context, "뒤로 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        backPressedTime = currentTime
    }
}