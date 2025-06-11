package com.umc.footprint

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.toSize
import com.umc.footprint.core.DesignConstant
import com.umc.footprint.model.event.DiaryModificationBarOpenEvent
import com.umc.footprint.model.event.ModifiedMapMarkerClickedEvent
import com.umc.footprint.model.prop.CategoryItemProp
import com.umc.footprint.model.prop.CategorySelectionBarProp
import com.umc.footprint.model.prop.DiaryCardLoadedProp
import com.umc.footprint.model.prop.DiaryCardProp
import com.umc.footprint.model.prop.DiaryModificationBarProp
import com.umc.footprint.model.prop.DiaryModificationModeProp
import com.umc.footprint.model.prop.PositionedDiaryCardProp
import com.umc.footprint.model.prop.VisibleCategorySelectionBarProp
import com.umc.footprint.util.calculateInclusion
import com.umc.footprint.util.checkLocationPermission
import com.umc.footprint.util.getDiaryCardTopLeftOffset

@Composable
fun FootprintApp(
    viewModel: FootprintViewModel,
    isMapBlurApplied: Boolean,
    onNavigateToCategoryApp: () -> Unit,
) {
    val context = LocalContext.current as Activity
    val density = LocalDensity.current
    val keyboard = LocalSoftwareKeyboardController.current
    val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()

    var mapViewSize by remember { mutableStateOf(Size.Zero) }

    var isLocationMarkingEnabled by remember { mutableStateOf(false) }
    var isCategorySelectionBarVisible by remember { mutableStateOf(false) }
    var diaryModificationBarOpenEvent by remember {
        mutableStateOf<DiaryModificationBarOpenEvent?>(
            null
        )
    }
    var markerEvent by remember { mutableStateOf<ModifiedMapMarkerClickedEvent?>(null) }

    val diaryCardLoadedPropMap = remember { mutableStateMapOf<Long, DiaryCardLoadedProp>() }

    val permissionRequester = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) Toast.makeText(context, "위치 권한이 거부되었습니다", Toast.LENGTH_SHORT).show()
        isLocationMarkingEnabled = isGranted
    }

    LaunchedEffect(key1 = Unit) {
        // 뷰모델 정보 초기화
        viewModel.getAllCategoryInfoFromServer()
        viewModel.moveMapToCurrentPosition()
        viewModel.getUserNameFromServer()
        viewModel.selectShowingCategory(categoryId = null)
        // 위치 권한 확인
        isLocationMarkingEnabled = permissionRequester.checkLocationPermission(context = context)
    }

    // 일기가 새로 로드되었을 때마다 실행
    LaunchedEffect(key1 = viewModel.diaryMap) {
        // 로딩에서 지워진 일기는 삭제
        viewModel.diaryMap.values.map { diary -> diary.id }.toSet().let { ids ->
            diaryCardLoadedPropMap.keys.toList().forEach { id ->
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
                    id = diary.id,
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
                        diaryModificationBarOpenEvent = DiaryModificationBarOpenEvent(
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

        viewModel.footprintMarkerClickedEvent?.let { event ->
            val diaryCardTopLeft = getDiaryCardTopLeftOffset(
                density = density,
                markerOffset = event.offset,
                // TODO: 발자국 모양 마커일 경우에만 해당 인자를 false로 변경
                includeArrowArea = true,
            )

            val isIncluded = calculateInclusion(
                innerOffset = diaryCardTopLeft,
                innerSize = with(density) { DesignConstant.DiaryCardSizeWithArrowArea.toSize() },
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
                val offsetFromCenter = Offset(
                    x = 0f,
                    y = with(density) {
                        (DesignConstant.DiaryCardSize.height.toPx() / 2)
                            .plus(DesignConstant.MarkerSize.height.toPx() / 4)
                            .plus(topPadding.toPx() / 2)
                    },
                )

                // 화면을 벗어난 경우 지도를 옮긴 후 카드를 띄움
                viewModel.moveMapToPosition(
                    latitude = event.latitude,
                    longitude = event.longitude,
                    pivot = offsetFromCenter,
                    onSucceed = {
                        markerEvent = ModifiedMapMarkerClickedEvent(
                            offset = mapViewSize.center + offsetFromCenter,
                            clusterId = event.clusterId,
                            color = event.color,
                        )
                    },
                )
            }
        }
    }

    // 선택한 카테고리가 있을 경우에는 뒤로가기 버튼으로 카테고리 선택 해제
    BackHandler(
        enabled = viewModel.selectedCategoryId != null,
        onBack = { viewModel.selectShowingCategory(categoryId = null) },
    )

    // 카테고리 선택 바텀시트가 올라온 경우에는 뒤로가기 버튼으로 바텀시트 닫기
    BackHandler(
        enabled = isCategorySelectionBarVisible,
        onBack = { isCategorySelectionBarVisible = false },
    )

    FootprintScreen(
        mapView = {
            Box(
                modifier = Modifier.onGloballyPositioned { mapViewSize = it.size.toSize() },
            ) {
                viewModel.MapView(
                    isBlurApplied = isMapBlurApplied,
                    isLocationMarkingEnabled = isLocationMarkingEnabled
                )
            }
        },
        isCategorySelected = viewModel.selectedCategoryId != null,
        diaryCardProp = markerEvent?.let { event ->
            PositionedDiaryCardProp(
                offset = event.offset,
                prop = DiaryCardProp(
                    clusterId = event.clusterId,
                    defaultCategoryColor = event.color,
                    diaryCardLoadedPropMap = viewModel.diaryMap.mapValues { (_, value) ->
                        diaryCardLoadedPropMap[value.id]
                    },
                    onNewDiaryRequested = { page ->
                        viewModel.getDiaryFromServer(
                            page = page,
                        )
                    },
                ),
            )
        },
        diaryModificationBarProp = diaryModificationBarOpenEvent?.let { info ->
            DiaryModificationBarProp(
                onModifyOptionClicked = {
                    diaryCardLoadedPropMap[info.targetDiaryId]?.apply {
                        diaryCardLoadedPropMap[id] = copy(
                            isFlipped = true, diaryModificationModeProp = DiaryModificationModeProp(
                                contentValue = content,
                                onContentValueChanged = {
                                    diaryCardLoadedPropMap[id]?.apply {
                                        if (diaryModificationModeProp != null) {
                                            diaryCardLoadedPropMap[id] = copy(
                                                diaryModificationModeProp = diaryModificationModeProp.copy(
                                                    contentValue = it
                                                )
                                            )
                                        }
                                    }
                                },
                                onModificationDone = {
                                    diaryCardLoadedPropMap[id]?.apply {
                                        if (diaryModificationModeProp != null) viewModel.modifyDiary(
                                            diaryId = id,
                                            content = diaryModificationModeProp.contentValue,
                                        )
                                        keyboard?.hide()
                                    }
                                },
                            )
                        )
                    }
                    diaryModificationBarOpenEvent = null
                },
                onDeleteOptionClicked = {
                    viewModel.deleteDiary(
                        diaryId = info.targetDiaryId,
                        onSucceed = { diaryModificationBarOpenEvent = null },
                        onFailed = { diaryModificationBarOpenEvent = null })
                },
                onDismissed = { diaryModificationBarOpenEvent = null },
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
                            viewModel.selectShowingCategory(categoryId = categoryInfo.id)
                            isCategorySelectionBarVisible = false
                        },
                    )
                },
                onNewCategoryButtonClicked = { onNavigateToCategoryApp() },
            ),
            onDismissed = { isCategorySelectionBarVisible = false },
        ),
        onCategoryButtonClicked = {
            if (viewModel.selectedCategoryId != null) viewModel.selectShowingCategory(categoryId = null)
            else isCategorySelectionBarVisible = !isCategorySelectionBarVisible
        },
        onLocationButtonClicked = {
            viewModel.moveMapToCurrentPosition(
                onFailed = {
                    permissionRequester.checkLocationPermission(
                        context = context,
                        requestOnNotGranted = true,
                    )
                },
            )
        },
    )
}
