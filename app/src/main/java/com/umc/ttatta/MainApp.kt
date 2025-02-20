package com.umc.ttatta

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
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
import com.umc.record.RecordApp
import com.umc.ttatta.component.NavigationItem
import com.umc.ttatta.component.RecordOptionPickerProp
import java.io.File

@Composable
fun MainApp(
    viewModel: MainViewModel,
    imageFile: File?,
    onImagePickerCalled: () -> Unit,
    onCameraCalled: () -> Unit,
) {
    val navigator = rememberNavController()

    val isLoggedIn by viewModel.isLoggedInState.collectAsState()
    var currentNavigationItem by remember { mutableStateOf<NavigationItem?>(null) }
    var showNavBar by remember { mutableStateOf(false) }
    var isCenterButtonActivated by remember { mutableStateOf(false) }
    var recordingDiaryContent by remember { mutableStateOf("") }

    // 3개의 화면은 라우팅 시 인자가 필요함 (챌린지, 기록하기, 카테고리 관리)
    var challengeRoutingInfo by remember { mutableStateOf<ChallengeRoutingInfo?>(null) }
    var recordRoutingInfo by remember { mutableStateOf<RecordRoutingInfo?>(null) }
    var categoryRoutingInfo by remember { mutableStateOf<CategoryRoutingInfo?>(null) }
    
    // 3개의 화면으로 라우팅 시도를 할 시에는 반드시 info 계열의 상태값을 초기화하는 방식으로 작동해야 함
    LaunchedEffect(key1 = challengeRoutingInfo) {
        challengeRoutingInfo?.let { info ->
            if (info.isPoppedFromRecord) navigator.popBackStack()
            else navigator.navigate(route = NavigationRoute.Challenge.route)
        }
    }

    LaunchedEffect(key1 = recordRoutingInfo) {
        recordRoutingInfo?.let {
            navigator.navigate(route = NavigationRoute.Record.route)
        }
    }

    LaunchedEffect(key1 = categoryRoutingInfo) {
        categoryRoutingInfo?.let {
            navigator.navigate(route = NavigationRoute.Category.route)
        }
    }

    // 특히, 기록하기 화면으로 라우팅할 시, 아래 상태값 변경을 통해 수행하여야 함
    var recordEntryInfo by remember { mutableStateOf<RecordEntryInfo?>(null) }

    LaunchedEffect(key1 = recordEntryInfo) {
        recordEntryInfo?.let { info ->
            when (info.mode) {
                RecordRoutingOption.CAMERA -> onCameraCalled()
                RecordRoutingOption.GALLERY -> onImagePickerCalled()
            }
        }
    }

    LaunchedEffect(key1 = imageFile) {
        imageFile?.let {
            recordRoutingInfo = RecordRoutingInfo(
                image = it,
                challengeId = recordEntryInfo?.challengeId,
            )
        }
    }

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
                if (it != currentNavigationItem) when (it) {
                    NavigationItem.DIARY,
                    NavigationItem.FOOTPRINT,
                    NavigationItem.MY_PAGE -> navigator.navigate(
                        route = when (it) {
                            NavigationItem.DIARY -> NavigationRoute.Home
                            NavigationItem.FOOTPRINT -> NavigationRoute.Footprint
                            NavigationItem.MY_PAGE -> NavigationRoute.MyPage
                            else -> throw Exception("wrong route")
                        }.route
                    ) {
                        popUpTo(id = navigator.graph.startDestinationId) { inclusive = false }
                    }
                    NavigationItem.CHALLENGE -> challengeRoutingInfo = ChallengeRoutingInfo(
                        isPointGranted = false,
                        isPoppedFromRecord = false,
                    )
                }
            },
            onCenterButtonClicked = { isCenterButtonActivated = true }
        ) else null,
        centerButtonProp = if (isCenterButtonActivated) {
            CenterButtonProp(
                recordOptionPickerProp = RecordOptionPickerProp(
                    userName = viewModel.userName,
                    onCameraOptionClicked = {
                        recordEntryInfo = RecordEntryInfo(
                            mode = RecordRoutingOption.CAMERA,
                            challengeId = null
                        )
                        isCenterButtonActivated = false
                    },
                    onGalleryOptionClicked = {
                        recordEntryInfo = RecordEntryInfo(
                            mode = RecordRoutingOption.GALLERY,
                            challengeId = null
                        )
                        isCenterButtonActivated = false
                    }
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
                    FinishHandler()

                    // LoginApp(
                    //     loginviewModel = hiltViewModel(),
                    //     joinviewModel = hiltViewModel(),
                    //     onNavigatingToHome = { viewModel.checkLogin() }
                    // )
                }
            }

            with(NavigationRoute.Home) {
                setNavGraph {
                    LaunchedEffect(Unit) { showNavBar = true }
                    FinishHandler()

                    Column {
                        Spacer(
                            modifier = Modifier.height(
                                WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
                            )
                        )
                        HomeApp(
                            viewModel = hiltViewModel(),
                        )
                    }
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
                            categoryRoutingInfo = CategoryRoutingInfo(
                                showTopBar = true
                            )
                        }
                    )
                }
            }

            with(NavigationRoute.Challenge) {
                setNavGraph {
                    val routingInfo = remember { challengeRoutingInfo!! }

                    LaunchedEffect(Unit) { showNavBar = true }

                    FinishHandler()

                    ChallengeApp(
                        viewModel = hiltViewModel(),
                        showPointGrantedPopup = routingInfo.isPointGranted,
                        onNavigationBarVisibilityChanged = { showNavBar = it },
                        onChallengeCompletionRequired = { challengeId ->
                            recordEntryInfo = RecordEntryInfo(
                                mode = RecordRoutingOption.GALLERY,
                                challengeId = challengeId
                            )
                        },
                    )
                }
            }

            with(NavigationRoute.MyPage) {
                setNavGraph {
                    LaunchedEffect(Unit) { showNavBar = true }
                    FinishHandler()

                    Column {
                        Spacer(
                            modifier = Modifier.height(
                                WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
                            )
                        )
                        MyPageApp(
                            viewModel = hiltViewModel(),
                            onLoginCanceled = { viewModel.checkLogin() }
                        )
                    }
                }
            }

            with(NavigationRoute.Category) {
                setNavGraph {
                    val routingInfo = remember { categoryRoutingInfo!! }

                    CategoryApp(
                        viewModel = hiltViewModel(),
                        showTopBar = routingInfo.showTopBar,
                    )
                }
            }

            with(NavigationRoute.Record) {
                setNavGraph {
                    val routingInfo = remember { recordRoutingInfo!! }
                    LaunchedEffect(Unit) { showNavBar = false }

                    RecordApp(
                        viewModel = hiltViewModel(),
                        image = routingInfo.image,
                        diaryContent = recordingDiaryContent,
                        onDiaryContentChanged = { recordingDiaryContent = it },
                        onNavigateToCategoryApp = {
                            categoryRoutingInfo = CategoryRoutingInfo(
                                showTopBar = false
                            )
                        },
                        onDone = {
                            routingInfo.challengeId?.let { challengeId ->
                                viewModel.makeChallengeComplete(
                                    challengeId = challengeId,
                                    onSucceed = {
                                        challengeRoutingInfo = ChallengeRoutingInfo(
                                            isPointGranted = true,
                                            isPoppedFromRecord = true
                                        )
                                    },
                                    onFailed = { /* TODO */ },
                                )
                            } ?: run { navigator.popBackStack() }
                        },
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = isLoggedIn) {
        isLoggedIn?.let { isLoggedIn ->
            // 로그인 상태에 따라 화면 분기
            navigator.navigate(
                route = when (isLoggedIn) {
                    true -> NavigationRoute.Home
                    false -> NavigationRoute.Login
                }.route
            ) {
                popUpTo(id = navigator.graph.startDestinationId) { inclusive = false }
            }
        } ?: run {
            // 스플래시로 분기
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