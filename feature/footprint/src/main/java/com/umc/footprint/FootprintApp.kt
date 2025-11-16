package com.umc.footprint

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.toSize
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.umc.design.CategoryColor
import com.umc.footprint.core.DesignConstant
import com.umc.footprint.model.event.DiaryModificationBarOpenEvent
import com.umc.footprint.model.event.ModifiedMapMarkerClickedEvent
import com.umc.footprint.model.event.RemindEvent
import com.umc.footprint.model.event.RemindLoadedEvent
import com.umc.footprint.model.prop.CategoryItemProp
import com.umc.footprint.model.prop.CategorySelectionBarProp
import com.umc.footprint.model.prop.DiaryCardLoadedProp
import com.umc.footprint.model.prop.DiaryCardProp
import com.umc.footprint.model.prop.DiaryModificationBarProp
import com.umc.footprint.model.prop.DiaryModificationModeProp
import com.umc.footprint.model.prop.PositionedDiaryCardProp
import com.umc.footprint.model.prop.VisibleCategorySelectionBarProp
import com.umc.footprint.util.calculateInclusion
import com.umc.footprint.util.getDiaryCardTopLeftOffset
import com.umc.footprint.util.runWithScope

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FootprintApp(
    viewModel: FootprintViewModel,
    isMapBlurApplied: Boolean,
    remindEvent: RemindEvent? = null,
    onNavigateToCategoryApp: () -> Unit,
) {
    val density = LocalDensity.current
    val keyboard = LocalSoftwareKeyboardController.current

    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )

    val topPadding = WindowInsets.systemBars
        .asPaddingValues()
        .calculateTopPadding()

    val centralFootprintOffset = remember {
        Offset(
            x = 0f,
            y = with(density) {
                (DesignConstant.DiaryCardSize.height.toPx() / 2)
                    .plus(DesignConstant.MarkerSize.height.toPx() / 4)
                    .plus(topPadding.toPx() / 2)
            },
        )
    }

    var mapViewSize by remember { mutableStateOf(Size.Zero) }

    var isCategorySelectionBarVisible by remember { mutableStateOf(false) }
    var isRemindDiaryCardFlipped by remember { mutableStateOf(false) }

    var barOpenEvent: DiaryModificationBarOpenEvent? by remember { mutableStateOf(null) }
    var markerEvent: ModifiedMapMarkerClickedEvent? by remember { mutableStateOf(null) }
    var remindLoadedEvent: RemindLoadedEvent? by remember { mutableStateOf(null) }

    val diaryCardLoadedPropMap = remember { mutableStateMapOf<Long, DiaryCardLoadedProp>() }

    LaunchedEffect(key1 = Unit) {
        viewModel.runWithScope {
            // 뷰모델 정보 초기화
            loadInitialData()
            // 보여질 발자국 카테고리 초기화
            selectShowingCategory(categoryId = null)
            // 지도를 내 위치로 이동
            if (remindEvent == null) moveMapToCurrentPosition()
        }

        // 위치 권한 획득 시도
        locationPermissionState.launchMultiplePermissionRequest()
    }

    // 일기가 새로 로드되었을 때마다 실행
    LaunchedEffect(key1 = viewModel.diaryMap) {
        // 로딩에서 지워진 일기는 삭제
        viewModel.diaryMap.values
            .map { diary -> diary.id }
            .toSet()
            .let { ids ->
                diaryCardLoadedPropMap.keys
                    .toList()
                    .forEach { id ->
                        if (id !in ids) diaryCardLoadedPropMap.remove(id)
                    }
            }

        // 로딩된 일기의 변경사항을 반영
        viewModel.diaryMap.values.forEach { diary ->
            diaryCardLoadedPropMap[diary.id]?.let { diaryProp ->
                // 일기가 사전에 로드된 적이 있을 때
                diaryCardLoadedPropMap[diary.id] = diaryProp.copy(
                    content = diary.content,
                    diaryModificationModeProp = null,
                )
            } ?: run {
                // 일기가 처음 로드되었을 때
                diaryCardLoadedPropMap[diary.id] = DiaryCardLoadedProp(
                    key = diary.id,
                    date = diary.date,
                    categoryColor = diary.color,
                    imageUrl = diary.imageUrl,
                    content = diary.content,
                    isFlipped = false,
                    diaryModificationModeProp = null,
                    onCardClicked = {
                        diaryCardLoadedPropMap[diary.id]?.apply {
                            diaryCardLoadedPropMap[diary.id] = copy(isFlipped = !isFlipped)
                        }
                    },
                    onModifyButtonClicked = {
                        barOpenEvent = DiaryModificationBarOpenEvent(
                            targetDiaryId = diary.id
                        )
                    },
                )
            }
        }
    }

    // 발자국 마커가 클릭되었을 때의 처리
    LaunchedEffect(key1 = viewModel.footprintMarkerClickedEvent) {
        markerEvent = null

        val event = viewModel.footprintMarkerClickedEvent ?: return@LaunchedEffect

        val diaryCardTopLeft = getDiaryCardTopLeftOffset(
            density = density,
            markerOffset = event.offset,
            includeArrowArea = event.isBook,
        )

        val isIncluded = calculateInclusion(
            innerOffset = diaryCardTopLeft,
            innerSize = with(density) {
                (if (event.isBook) DesignConstant.DiaryCardSizeWithArrowArea
                else DesignConstant.DiaryCardSizeWithShadowArea).toSize()
            },
            outerOffset = Offset.Zero,
            outerSize = mapViewSize,
        )

        // 카드를 띄울 공간이 화면을 벗어났는지를 확인
        if (isIncluded) {
            markerEvent = ModifiedMapMarkerClickedEvent(
                offset = event.offset,
                clusterId = event.clusterId,
                color = event.color,
            )
        } else {
            // 화면을 벗어난 경우 지도를 옮긴 후 카드를 띄움
            runCatching {
                viewModel.moveMapToPosition(
                    latitude = event.latitude,
                    longitude = event.longitude,
                    pivot = centralFootprintOffset,
                    zoom = false,
                )
            }.onSuccess {
                markerEvent = ModifiedMapMarkerClickedEvent(
                    offset = mapViewSize.center + centralFootprintOffset,
                    clusterId = event.clusterId,
                    color = event.color,
                )
            }
        }
    }

    // 리마인드 이벤트 발생 시 실행
    LaunchedEffect(key1 = remindEvent) {
        if (remindEvent == null) return@LaunchedEffect

        runCatching {
            viewModel.getDiaryForRemindFromServer(id = remindEvent.diaryId)
        }.onSuccess { diary ->
            viewModel.moveMapToPosition(
                latitude = diary.latitude,
                longitude = diary.longitude,
                pivot = centralFootprintOffset,
                zoom = true,
            )

            remindLoadedEvent = RemindLoadedEvent(
                description = remindEvent.description,
                date = diary.date,
                isBook = diary.isClustered,
                color = diary.color ?: CategoryColor.RED,  // TODO: 카테고리 컬러는 추후 Nullable 특성을 잃음
                imageUrl = diary.imageUrl,
                content = remindEvent.description,
                onDismissed = {
                    remindEvent.onDismissed()
                    remindLoadedEvent = null
                    isRemindDiaryCardFlipped = false
                }
            )
        }
    }

    // 선택한 카테고리가 있을 경우에는 뒤로가기 버튼으로 카테고리 선택 해제
    BackHandler(
        enabled = viewModel.selectedCategoryId != null,
        onBack = { viewModel.runWithScope { selectShowingCategory(categoryId = null) } },
    )

    // 카테고리 선택 바텀시트가 올라온 경우에는 뒤로가기 버튼으로 바텀시트 닫기
    BackHandler(
        enabled = isCategorySelectionBarVisible,
        onBack = { isCategorySelectionBarVisible = false },
    )

    // 리마인드 이벤트가 있는 경우에는 리마인드 이벤트를 취소
    BackHandler(
        enabled = remindLoadedEvent != null,
        onBack = { remindLoadedEvent?.onDismissed?.invoke() },
    )

    FootprintScreen(
        mapView = {
            Box(
                modifier = Modifier.onGloballyPositioned { mapViewSize = it.size.toSize() },
            ) {
                viewModel.MapView(
                    isBlurApplied = isMapBlurApplied,
                    isLocationMarkingEnabled = locationPermissionState.allPermissionsGranted,
                )
                AnimatedVisibility(
                    visible = remindLoadedEvent != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = Color.White.copy(alpha = 0.6f))
                            .fillMaxSize()
                    )
                }
            }
        },
        isCategorySelected = viewModel.selectedCategoryId != null,
        diaryCardProp = remindLoadedEvent?.let { event ->
            // 리마인드 이벤트에 의해서 카드가 띄워지는 경우
            PositionedDiaryCardProp(
                offset = mapViewSize.center + centralFootprintOffset,
                prop = DiaryCardProp(
                    key = -1L,
                    description = remindLoadedEvent?.description,
                    defaultCategoryColor = event.color,
                    diaryCardLoadedPropMap = mapOf(
                        0 to DiaryCardLoadedProp(
                            key = -1L,
                            date = event.date,
                            categoryColor = event.color,
                            imageUrl = event.imageUrl,
                            content = event.content,
                            isFlipped = isRemindDiaryCardFlipped,
                            diaryModificationModeProp = null,
                            onCardClicked = {
                                isRemindDiaryCardFlipped = !isRemindDiaryCardFlipped
                            },
                            onModifyButtonClicked = null,
                        ),
                    ),
                    onNewDiaryRequested = { /* DO NOTHING */ }
                ),
            )
        } ?: markerEvent?.let { event ->
            // 발자국 마커 클릭에 의해서 카드가 띄워지는 경우
            PositionedDiaryCardProp(
                offset = event.offset,
                prop = DiaryCardProp(
                    key = event.clusterId,
                    description = null,
                    defaultCategoryColor = event.color,
                    diaryCardLoadedPropMap = viewModel.diaryMap.mapValues { (_, value) ->
                        diaryCardLoadedPropMap[value.id]
                    },
                    onNewDiaryRequested = { page ->
                        viewModel.runWithScope { getDiaryFromServer(page = page) }
                    },
                ),
            )
        },
        diaryModificationBarProp = barOpenEvent?.let { event ->
            DiaryModificationBarProp(
                onModifyOptionClicked = {
                    diaryCardLoadedPropMap[event.targetDiaryId]?.apply {
                        diaryCardLoadedPropMap[key] = copy(
                            isFlipped = true,
                            diaryModificationModeProp = DiaryModificationModeProp(
                                contentValue = content,
                                onContentValueChanged = {
                                    diaryCardLoadedPropMap[key]?.apply {
                                        if (diaryModificationModeProp != null) {
                                            diaryCardLoadedPropMap[key] = copy(
                                                diaryModificationModeProp = diaryModificationModeProp.copy(
                                                    contentValue = it
                                                )
                                            )
                                        }
                                    }
                                },
                                onModificationDone = {
                                    diaryCardLoadedPropMap[key]?.apply {
                                        viewModel.runWithScope {
                                            if (diaryModificationModeProp != null) modifyDiary(
                                                diaryId = key,
                                                content = diaryModificationModeProp.contentValue,
                                            )
                                        }
                                        keyboard?.hide()
                                    }
                                },
                            ),
                        )
                    }
                    barOpenEvent = null
                },
                onDeleteOptionClicked = {
                    viewModel.runWithScope {
                        runCatching { deleteDiary(diaryId = event.targetDiaryId) }
                        barOpenEvent = null
                    }
                },
                onDismissed = { barOpenEvent = null },
            )
        },
        categorySelectionBarProp = VisibleCategorySelectionBarProp(
            isVisible = isCategorySelectionBarVisible,
            prop = CategorySelectionBarProp(
                userName = viewModel.userName,
                itemProps = viewModel.categoryList.map { categoryInfo ->
                    CategoryItemProp(
                        name = categoryInfo.name,
                        color = categoryInfo.color,
                        count = categoryInfo.count,
                        onClicked = {
                            viewModel.runWithScope { selectShowingCategory(categoryId = categoryInfo.id) }
                            isCategorySelectionBarVisible = false
                        },
                    )
                },
                onNewCategoryButtonClicked = { onNavigateToCategoryApp() },
            ),
            onDismissed = { isCategorySelectionBarVisible = false },
        ),
        onBackScreenClicked = remindLoadedEvent?.onDismissed,
        onCategoryButtonClicked = {
            viewModel.runWithScope {
                if (viewModel.selectedCategoryId != null) selectShowingCategory(categoryId = null)
                else isCategorySelectionBarVisible = !isCategorySelectionBarVisible
            }
        },
        onLocationButtonClicked = {
            if (locationPermissionState.allPermissionsGranted) {
                viewModel.runWithScope { runCatching { moveMapToCurrentPosition() } }
            } else {
                locationPermissionState.launchMultiplePermissionRequest()
            }
        },
    )
}
