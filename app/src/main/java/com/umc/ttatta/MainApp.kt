package com.umc.ttatta

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.umc.category.CategoryApp
import com.umc.challenge.ChallengeApp
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.FootprintApp
import com.umc.footprint.model.event.RemindEvent
import com.umc.home.HomeApp
import com.umc.home.HomeViewModel
import com.umc.login.LoginApp
import com.umc.mypage.MyPageApp
import com.umc.record.RecordApp
import com.umc.ttatta.component.FinishHandler
import com.umc.ttatta.component.NavigationItem
import com.umc.ttatta.component.Splash
import com.umc.ttatta.intent.IntentType
import com.umc.ttatta.model.event.IntentEvent
import com.umc.ttatta.model.prop.CenterButtonProp
import com.umc.ttatta.model.prop.NavigationBarProp
import com.umc.ttatta.model.prop.RecordOptionPickerProp
import com.umc.ttatta.model.routing.CategoryRoutingInfo
import com.umc.ttatta.model.routing.ChallengeRoutingInfo
import com.umc.ttatta.model.routing.RecordEntryInfo
import com.umc.ttatta.model.routing.RecordRoutingInfo
import com.umc.ttatta.navigation.NavigationManager
import com.umc.ttatta.navigation.NavigationManager.go
import com.umc.ttatta.navigation.NavigationManager.push
import com.umc.ttatta.navigation.NavigationManager.setNavGraph
import com.umc.ttatta.navigation.NavigationManager.setOnRouteChange
import com.umc.ttatta.navigation.NavigationRoute
import com.umc.ttatta.util.runWithScope
import java.io.File

@Composable
fun MainApp(
    viewModel: MainViewModel,
    imageFile: File?,
    intentEvent: IntentEvent?,
    onPermissionRequiredInitially: () -> Unit,
    onImagePickerCalled: () -> Unit,
    onCameraCalled: () -> Unit,
) {
    val navigator = rememberNavController()

    val isLoggedIn by viewModel.isLoggedInState.collectAsState()
    val isLocked by viewModel.isLockedState.collectAsState()

    var currentNavigationItem by remember { mutableStateOf<NavigationItem?>(null) }
    var showNavBar by remember { mutableStateOf(false) }
    var isCenterButtonActivated by remember { mutableStateOf(false) }
    var recordingDiaryContent by remember { mutableStateOf("") }

    // 3개의 화면은 라우팅 시 인자가 필요함 (챌린지, 기록하기, 카테고리 관리)
    var challengeRoutingInfo by remember { mutableStateOf<ChallengeRoutingInfo?>(null) }
    var recordRoutingInfo by remember { mutableStateOf<RecordRoutingInfo?>(null) }
    var categoryRoutingInfo by remember { mutableStateOf<CategoryRoutingInfo?>(null) }

    val onNavigateInitiallyToMain: () -> Unit = remember(
        intentEvent,
        onPermissionRequiredInitially,
        viewModel,
        navigator,
    ) {
        {
            when (intentEvent?.intentType) {
                is IntentType.LocationMemory -> navigator.go(route = NavigationRoute.FOOTPRINT)

                is IntentType.DailySummary, is IntentType.DiaryWritingReminder, null -> navigator.go(
                    route = NavigationRoute.HOME
                )

                is IntentType.ChallengeReminder -> challengeRoutingInfo = ChallengeRoutingInfo(
                    isPointGranted = false,
                    isPoppedFromRecord = false,
                )
            }

            onPermissionRequiredInitially()  // 권한 획득 요청
            viewModel.runWithScope { enableLock() }  // 핀 번호 잠금 화면 활성화
        }
    }

    // 3개의 화면으로 라우팅 시도를 할 시에는 반드시 info 계열의 상태값을 초기화하는 방식으로 작동해야 함
    LaunchedEffect(key1 = challengeRoutingInfo) {
        challengeRoutingInfo?.let { info ->
            if (info.isPoppedFromRecord) navigator.popBackStack()
            else navigator.push(route = NavigationRoute.CHALLENGE)
        }
    }

    LaunchedEffect(key1 = recordRoutingInfo) {
        recordRoutingInfo?.let {
            navigator.push(route = NavigationRoute.RECORD)
        }
    }

    LaunchedEffect(key1 = categoryRoutingInfo) {
        categoryRoutingInfo?.let {
            navigator.push(route = NavigationRoute.CATEGORY)
        }
    }

    // 특히, 기록하기 화면으로 라우팅할 시, 아래 상태값 변경을 통해 수행하여야 함
    var recordEntryInfo by remember { mutableStateOf<RecordEntryInfo?>(null) }

    LaunchedEffect(key1 = recordEntryInfo) {
        recordEntryInfo?.let { info ->
            when (info.mode) {
                RecordEntryInfo.RecordRoutingOption.CAMERA -> onCameraCalled()
                RecordEntryInfo.RecordRoutingOption.GALLERY -> onImagePickerCalled()
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
        navigator.setOnRouteChange {
            currentNavigationItem = when (it) {
                NavigationRoute.HOME -> NavigationItem.DIARY
                NavigationRoute.FOOTPRINT -> NavigationItem.FOOTPRINT
                NavigationRoute.CHALLENGE -> NavigationItem.CHALLENGE
                NavigationRoute.MY_PAGE -> NavigationItem.MY_PAGE
                else -> null
            }
        }
    }

    ThemeProvider {
        MainScreen(
            navigationBarProp = if (showNavBar) NavigationBarProp(
                currentNavigationItem = currentNavigationItem,
                onNavigate = {
                    if (it != currentNavigationItem) when (it) {
                        NavigationItem.CHALLENGE -> challengeRoutingInfo = ChallengeRoutingInfo(
                            isPointGranted = false,
                            isPoppedFromRecord = false,
                        )

                        else -> navigator.go(
                            route = when (it) {
                                NavigationItem.DIARY -> NavigationRoute.HOME
                                NavigationItem.FOOTPRINT -> NavigationRoute.FOOTPRINT
                                NavigationItem.MY_PAGE -> NavigationRoute.MY_PAGE
                                else -> throw Exception("wrong route")
                            }
                        )
                    }
                },
                onCenterButtonClicked = { isCenterButtonActivated = true },
            ) else null,
            centerButtonProp = if (isCenterButtonActivated) {
                CenterButtonProp(
                    recordOptionPickerProp = RecordOptionPickerProp(
                        userName = viewModel.userName,
                        accessories = viewModel.equippedAccessories,
                        onCameraOptionClicked = {
                            recordEntryInfo = RecordEntryInfo(
                                mode = RecordEntryInfo.RecordRoutingOption.CAMERA,
                                challengeId = null,
                            )
                            isCenterButtonActivated = false
                        },
                        onGalleryOptionClicked = {
                            recordEntryInfo = RecordEntryInfo(
                                mode = RecordEntryInfo.RecordRoutingOption.GALLERY,
                                challengeId = null,
                            )
                            isCenterButtonActivated = false
                        },
                    ),
                    onDismissed = { isCenterButtonActivated = false },
                )
            } else null,
        ) {
            NavHost(
                navController = navigator,
                startDestination = NavigationManager.initialRoute,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
                modifier = Modifier.fillMaxSize()
            ) {
                setNavGraph(NavigationRoute.SPLASH) {
                    LaunchedEffect(Unit) {
                        showNavBar = false
                        viewModel.runWithScope { checkLogin() }
                    }

                    LaunchedEffect(key1 = isLoggedIn) {
                        when (isLoggedIn) {
                            true -> {
                                if (isLocked) navigator.go(route = NavigationRoute.LOCK)
                                else onNavigateInitiallyToMain()
                            }

                            false -> navigator.go(route = NavigationRoute.LOGIN)
                            else -> Unit
                        }
                    }

                    Splash()
                }

                setNavGraph(NavigationRoute.LOCK) {
                    LaunchedEffect(Unit) { showNavBar = false }
                    FinishHandler()

                    // FIXME: 잠금화면 연결 시 제거
                    LaunchedEffect(Unit) {
                        onNavigateInitiallyToMain()
                    }

                    // TODO: 잠금하면 구현 시 연결
                    // LockPasswordScreen(
                    //     isChangingPassword = false,
                    //     isCheckingMode = true,
                    //     onComplete = { pinString ->
                    //         viewModel.runWithScope {
                    //             val isCorrect = checkIsPinCorrect(pin = pinString.toInt())
                    //             if (isCorrect) onNavigateInitiallyToMain()
                    //         }
                    //     },
                    // )
                }

                setNavGraph(NavigationRoute.LOGIN) {
                    LaunchedEffect(Unit) { showNavBar = false }
                    FinishHandler()

                    LoginApp(
                        viewModel = hiltViewModel(),
                        onNavigatingToHome = {
                            viewModel.runWithScope {
                                checkLogin()
                                onNavigateInitiallyToMain()
                            }
                        },
                    )
                }

                setNavGraph(NavigationRoute.HOME) {
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    LaunchedEffect(Unit) {
                        showNavBar = true
                        homeViewModel.loadAllDiaries()
                    }
                    FinishHandler()

                    Column {
                        Spacer(
                            modifier = Modifier.height(
                                WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
                            )
                        )
                        HomeApp(
                            viewModel = homeViewModel,
                        )
                    }
                }

                setNavGraph(NavigationRoute.FOOTPRINT) {
                    LaunchedEffect(Unit) { showNavBar = true }
                    FinishHandler()

                    FootprintApp(
                        viewModel = hiltViewModel(),
                        isMapBlurApplied = isCenterButtonActivated,
                        remindEvent = remember(intentEvent) {
                            intentEvent?.let { event ->
                                if (event.intentType is IntentType.LocationMemory) RemindEvent(
                                    diaryId = event.intentType.diaryId,
                                    description = "오래전 이 곳을 방문했어요!",
                                    onDismissed = event.onDismissed
                                ) else null
                            }
                        },
                        onNavigateToCategoryApp = {
                            categoryRoutingInfo = CategoryRoutingInfo(
                                showTopBar = true
                            )
                        },
                    )
                }

                setNavGraph(NavigationRoute.CHALLENGE) {
                    val routingInfo = remember { challengeRoutingInfo!! }
                    LaunchedEffect(Unit) { showNavBar = true }
                    DisposableEffect(Unit) { onDispose { challengeRoutingInfo = null } }
                    FinishHandler()

                    ChallengeApp(
                        viewModel = hiltViewModel(),
                        showPointGrantedPopup = routingInfo.isPointGranted,
                        onNavigationBarVisibilityChanged = { showNavBar = it },
                        onChallengeCompletionRequired = { challengeId ->
                            recordEntryInfo = RecordEntryInfo(
                                mode = RecordEntryInfo.RecordRoutingOption.CAMERA,
                                challengeId = challengeId,
                            )
                        },
                    )
                }

                setNavGraph(NavigationRoute.MY_PAGE) {
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
                            onLoginCanceled = {
                                navigator.popBackStack(
                                    destinationId = navigator.graph.startDestinationId,
                                    inclusive = false,
                                )
                            },
                        )
                    }
                }

                setNavGraph(NavigationRoute.CATEGORY) {
                    val routingInfo = remember { categoryRoutingInfo!! }
                    DisposableEffect(Unit) { onDispose { categoryRoutingInfo = null } }

                    CategoryApp(
                        viewModel = hiltViewModel(),
                        showTopBar = routingInfo.showTopBar,
                    )
                }

                setNavGraph(NavigationRoute.RECORD) {
                    val routingInfo = remember { recordRoutingInfo!! }
                    LaunchedEffect(Unit) { showNavBar = false }
                    DisposableEffect(Unit) { onDispose { recordRoutingInfo = null } }

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
                                viewModel.runWithScope {
                                    runCatching {
                                        makeChallengeComplete(challengeId = challengeId)
                                    }.onSuccess {
                                        challengeRoutingInfo = ChallengeRoutingInfo(
                                            isPointGranted = true,
                                            isPoppedFromRecord = true,
                                        )
                                    }
                                }
                            } ?: navigator.go(route = NavigationRoute.HOME)

                            recordingDiaryContent = ""
                        },
                    )
                }
            }
        }
    }
}
