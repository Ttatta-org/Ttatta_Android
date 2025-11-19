package com.umc.ttatta.app

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.umc.category.CategoryApp
import com.umc.challenge.ChallengeApp
import com.umc.challenge.PointGrantEvent
import com.umc.core.util.runWithScope
import com.umc.design.component.LocationAccessPopup
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.FootprintApp
import com.umc.footprint.model.event.RemindEvent
import com.umc.home.HomeApp
import com.umc.login.LoginApp
import com.umc.mypage.LockApp
import com.umc.mypage.MyPageApp
import com.umc.record.RecordApp
import com.umc.ttatta.app.component.FinishHandler
import com.umc.ttatta.app.component.NavigationItem
import com.umc.ttatta.app.component.Splash
import com.umc.ttatta.app.intent.IntentType
import com.umc.ttatta.app.model.event.IntentEvent
import com.umc.ttatta.app.model.prop.CenterButtonProp
import com.umc.ttatta.app.model.prop.NavigationBarProp
import com.umc.ttatta.app.model.prop.RecordOptionPickerProp
import com.umc.ttatta.app.navigation.NavigationRoute
import com.umc.ttatta.app.tracking.LocationTrackingService.Companion.finishLocationTrackingService
import com.umc.ttatta.app.tracking.LocationTrackingService.Companion.startLocationTrackingService
import com.umc.ttatta.app.util.FileManager.uriToFile
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainApp(
    viewModel: MainViewModel,
    intentEvent: IntentEvent?,
    requestImagePicker: (callback: (Uri?) -> Unit) -> Unit,
    requestCamera: (callback: (Uri?) -> Unit) -> Unit,
) {
    val navigator = rememberNavController()
    val context = LocalContext.current as ComponentActivity

    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    val isLoggedIn by viewModel.isLoggedInState.collectAsState()
    val isLocked by viewModel.isLockedState.collectAsState()
    val isLocationBasedRemindEnabled by viewModel.isLocationBasedRemindEnabledState.collectAsState()

    var currentNavigationItem by remember { mutableStateOf<NavigationItem?>(null) }
    var showNavBar by remember { mutableStateOf(false) }
    var isCenterButtonActivated by remember { mutableStateOf(false) }
    var showLocationPermissionPopup by remember { mutableStateOf(false) }

    var recordingDiaryImage: Uri? by remember { mutableStateOf(null) }
    var recordingDiaryContent by remember { mutableStateOf("") }
    var recordingChallengeId: Long? by remember { mutableStateOf(null) }
    var earnedPoint: Int? by remember { mutableStateOf(null) }

    val onNavigateInitiallyToMain: () -> Unit = remember(
        intentEvent,
        viewModel,
        navigator,
    ) {
        {
            when (intentEvent?.intentType) {
                is IntentType.LocationMemory -> {
                    MainScope().launch {
                        navigator.navigate(route = NavigationRoute.Footprint.Footprint) {
                            popUpTo(navigator.graph.startDestinationId) {
                                inclusive = false
                            }
                        }
                    }
                }

                is IntentType.DailySummary, is IntentType.DiaryWritingReminder, null -> {
                    MainScope().launch {
                        navigator.navigate(route = NavigationRoute.Home) {
                            popUpTo(navigator.graph.startDestinationId) {
                                inclusive = false
                            }
                        }
                    }
                }

                is IntentType.ChallengeReminder -> {
                    MainScope().launch {
                        navigator.navigate(route = NavigationRoute.Challenge) {
                            popUpTo(navigator.graph.startDestinationId) {
                                inclusive = false
                            }
                        }
                    }
                }
            }
        }
    }

    val onRecordingImageUriLoaded: (Uri) -> Unit = remember {
        { uri ->
            recordingDiaryImage = uri
            navigator.navigate(route = NavigationRoute.Record.Record)
        }
    }

    LaunchedEffect(key1 = Unit) {
        navigator.addOnDestinationChangedListener { _, destination, _ ->
            val route = NavigationRoute::class.sealedSubclasses.find {
                it.simpleName == destination.route
                    ?.split(".")
                    ?.lastOrNull()
            }

            currentNavigationItem = when (route) {
                NavigationRoute.Home::class -> NavigationItem.DIARY
                NavigationRoute.Footprint::class -> NavigationItem.FOOTPRINT
                NavigationRoute.Challenge::class -> NavigationItem.CHALLENGE
                NavigationRoute.MyPage::class -> NavigationItem.MY_PAGE
                else -> null
            }
        }
    }

    // 위치 트래킹 서비스 활성화 여부 결정
    LaunchedEffect(isLocationBasedRemindEnabled) {
        runCatching {
            if (isLocationBasedRemindEnabled) {
                context.startLocationTrackingService()
            } else {
                context.finishLocationTrackingService()
            }
        }
    }

    ThemeProvider {
        MainScreen(
            navigationBarProp = if (showNavBar) NavigationBarProp(
                currentNavigationItem = currentNavigationItem,
                onNavigate = {
                    val route = when (it) {
                        NavigationItem.DIARY -> NavigationRoute.Home
                        NavigationItem.FOOTPRINT -> NavigationRoute.Footprint.Footprint
                        NavigationItem.CHALLENGE -> NavigationRoute.Challenge
                        NavigationItem.MY_PAGE -> NavigationRoute.MyPage
                    }

                    navigator.navigate(route = route) {
                        popUpTo(navigator.graph.startDestinationId) {
                            inclusive = false
                        }
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
                            isCenterButtonActivated = false
                            requestCamera { uri ->
                                uri?.let { onRecordingImageUriLoaded(it) }
                            }
                        },
                        onGalleryOptionClicked = {
                            isCenterButtonActivated = false
                            requestImagePicker { uri ->
                                uri?.let { onRecordingImageUriLoaded(it) }
                            }
                        },
                    ),
                    onDismissed = { isCenterButtonActivated = false },
                )
            } else null,
        ) {
            NavHost(
                navController = navigator,
                startDestination = NavigationRoute.Splash,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
                modifier = Modifier.fillMaxSize()
            ) {
                composable<NavigationRoute.Splash> {
                    LaunchedEffect(Unit) {
                        showNavBar = false
                        viewModel.runWithScope { refresh() }
                    }

                    LaunchedEffect(key1 = isLoggedIn) {
                        when (isLoggedIn) {
                            true -> {
                                if (isLocked) navigator.navigate(route = NavigationRoute.Lock)
                                else onNavigateInitiallyToMain()
                            }

                            false -> {
                                navigator.navigate(route = NavigationRoute.Login)
                            }

                            else -> Unit
                        }
                    }

                    Splash()
                }

                composable<NavigationRoute.Lock> {
                    LaunchedEffect(Unit) { showNavBar = false }
                    FinishHandler()

                    LockApp(
                        viewModel = hiltViewModel(),
                        onPinCorrect = onNavigateInitiallyToMain,
                    )
                }

                composable<NavigationRoute.Login> {
                    LaunchedEffect(Unit) { showNavBar = false }
                    FinishHandler()

                    LoginApp(
                        viewModel = hiltViewModel(),
                        onNavigatingToHome = {
                            viewModel.runWithScope {
                                refresh()
                                onNavigateInitiallyToMain()
                            }
                        },
                    )
                }

                composable<NavigationRoute.Home> {
                    LaunchedEffect(Unit) { showNavBar = true }
                    FinishHandler()

                    HomeApp(
                        viewModel = hiltViewModel(),
                    )
                }

                navigation<NavigationRoute.Footprint>(
                    startDestination = NavigationRoute.Footprint.Footprint,
                ) {
                    composable<NavigationRoute.Footprint.Footprint> {
                        LaunchedEffect(Unit) { showNavBar = true }
                        FinishHandler()

                        FootprintApp(
                            viewModel = hiltViewModel(),
                            isMapBlurApplied = isCenterButtonActivated,
                            remindEvent = remember(intentEvent) {
                                intentEvent?.let { event ->
                                    if (event.intentType is IntentType.LocationMemory) RemindEvent(
                                        diaryId = event.intentType.diaryId,
                                        description = event.intentType.description,
                                        onDismissed = event.onDismissed
                                    ) else null
                                }
                            },
                            onNavigateToCategoryApp = {
                                MainScope().launch {
                                    navigator.navigate(
                                        route = NavigationRoute.Footprint.Category
                                    )
                                }
                            },
                        )
                    }

                    composable<NavigationRoute.Footprint.Category> {
                        CategoryApp(
                            viewModel = hiltViewModel(),
                            topBarTitle = "발자국 새로 만들기 및 수정",
                            onBackButtonClicked = {
                                MainScope().launch { navigator.popBackStack() }
                            },
                        )
                    }
                }

                composable<NavigationRoute.Challenge> {
                    LaunchedEffect(Unit) { showNavBar = true }
                    FinishHandler()

                    ChallengeApp(
                        viewModel = hiltViewModel(),
                        pointGrantEvent = earnedPoint?.let {
                            PointGrantEvent(
                                point = it,
                                onDismiss = { earnedPoint = null },
                            )
                        },
                        onNavigationBarVisibilityChanged = { showNavBar = it },
                        onChallengeCompletionRequired = { challengeId ->
                            requestCamera { uri ->
                                uri?.let {
                                    recordingChallengeId = challengeId
                                    onRecordingImageUriLoaded(it)
                                }
                            }
                        },
                    )
                }

                composable<NavigationRoute.MyPage> {
                    LaunchedEffect(Unit) { showNavBar = true }
                    FinishHandler()

                    MyPageApp(
                        viewModel = hiltViewModel(),
                        onLoginCanceled = {
                            MainScope().launch {
                                navigator.popBackStack(
                                    destinationId = navigator.graph.startDestinationId,
                                    inclusive = false,
                                )
                            }
                        },
                        onBackgroundLocationRequirementChanged = { isRequired ->
                            if (isRequired) {
                                runCatching {
                                    context.startLocationTrackingService()
                                }.onFailure {
                                    val toast = Toast.makeText(
                                        context,
                                        "오류: 위치 트래킹 서비스를 시작할 수 없습니다.",
                                        Toast.LENGTH_SHORT,
                                    )

                                    toast.show()
                                }.isSuccess
                            } else {
                                runCatching { context.finishLocationTrackingService() }.isSuccess
                            }
                        },
                    )
                }

                navigation<NavigationRoute.Record>(
                    startDestination = NavigationRoute.Record.Nothing,
                ) {
                    composable<NavigationRoute.Record.Nothing> {
                        // NOTHING
                    }

                    composable<NavigationRoute.Record.Record> {
                        val image: File? = remember(recordingDiaryImage) {
                            recordingDiaryImage?.let { uri -> context.uriToFile(uri) }
                        }

                        val onBack: (route: NavigationRoute?) -> Unit = remember {
                            { route ->
                                recordingChallengeId = null
                                recordingDiaryImage = null
                                recordingDiaryContent = ""
                                MainScope().launch {
                                    route?.let {
                                        navigator.navigate(route = route) {
                                            popUpTo(navigator.graph.startDestinationId) {
                                                inclusive = false
                                            }
                                        }
                                    } ?: run {
                                        navigator.popBackStack()
                                    }
                                }
                            }
                        }

                        LaunchedEffect(Unit) { showNavBar = false }
                        BackHandler { onBack(null) }

                        RecordApp(
                            viewModel = hiltViewModel(),
                            image = image,
                            diaryContent = recordingDiaryContent,
                            onDiaryContentChanged = { recordingDiaryContent = it },
                            onNavigateToCategoryApp = {
                                MainScope().launch {
                                    navigator.navigate(route = NavigationRoute.Record.Category)
                                }
                            },
                            onDone = {
                                recordingChallengeId?.let { challengeId ->
                                    viewModel.runWithScope {
                                        runCatching {
                                            makeChallengeComplete(challengeId = challengeId)
                                        }.onSuccess { point ->
                                            earnedPoint = point
                                            onBack(NavigationRoute.Challenge)
                                        }
                                    }
                                } ?: onBack(NavigationRoute.Home)
                            },
                        )
                    }

                    composable<NavigationRoute.Record.Category> {
                        CategoryApp(
                            viewModel = hiltViewModel(),
                            topBarTitle = "발자국 새로 만들기",
                            onBackButtonClicked = {
                                MainScope().launch { navigator.popBackStack() }
                            })
                    }
                }
            }
        }

        if (showLocationPermissionPopup) LocationAccessPopup(onDismiss = {
            showLocationPermissionPopup = false
        }, onConfirm = {
            showLocationPermissionPopup = false
            locationPermissionState.launchMultiplePermissionRequest()
        })
    }
}
