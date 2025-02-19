package com.umc.footprint

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.umc.footprint.component.CategoryItemProp
import com.umc.footprint.component.CategorySelectionBarProp
import com.umc.footprint.component.DiaryCardLoadedProp
import com.umc.footprint.component.DiaryCardProp
import com.umc.footprint.component.DiaryModificationBarProp
import com.umc.footprint.component.DiaryModificationModeProp

data class DiaryModificationBarInfo(
    val targetDiaryId: Long
)

@Composable
fun FootprintApp(
    viewModel: FootprintViewModel,
    isMapBlurApplied: Boolean,
    onNavigateToCategoryApp: () -> Unit
) {
    val context = LocalContext.current as Activity
    val keyboard = LocalSoftwareKeyboardController.current

    var isLocationMarkingEnabled by remember { mutableStateOf(false) }
    var isCategorySelectionBarVisible by remember { mutableStateOf(false) }
    var diaryModificationBarInfo by remember { mutableStateOf<DiaryModificationBarInfo?>(null) }
    val diaryCardLoadedPropMap = remember { mutableStateMapOf<Long, DiaryCardLoadedProp>() }

    val permissionRequester = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted)
            Toast.makeText(context, "위치 권한이 거부되었습니다", Toast.LENGTH_SHORT).show()
        isLocationMarkingEnabled = isGranted
    }

    LaunchedEffect(key1 = Unit) {
        // 뷰모델 정보 초기화
        viewModel.initialize()
        // 위치 권한 확인
        isLocationMarkingEnabled = permissionRequester.checkLocationPermission(context = context)
    }
    
    // 일기가 새로 로드되었을 때마다 실행
    LaunchedEffect(key1 = viewModel.diaryMap) {
        if (viewModel.diaryMap.isNotEmpty()) viewModel.diaryMap.values.forEach { diary ->
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
                        diaryModificationBarInfo = DiaryModificationBarInfo(
                            targetDiaryId = diary.id
                        )
                    }
                )
            }
        } else diaryCardLoadedPropMap.clear()
    }

    BackHandler(
        enabled = viewModel.selectedCategoryId != null,
        onBack = { viewModel.selectShowingCategory(categoryId = null) }
    )

    BackHandler(
        enabled = isCategorySelectionBarVisible,
        onBack = { isCategorySelectionBarVisible = false }
    )

    FootprintScreen(
        mapView = {
            viewModel.MapView(
                isBlurApplied = isMapBlurApplied,
                isLocationMarkingEnabled = isLocationMarkingEnabled
            )
        },
        isCategorySelected = viewModel.selectedCategoryId != null,
        diaryCardProp = viewModel.clickedMarkerInfo?.let { clickedMarkerInfo ->
            PositionedDiaryCardProp(
                x = clickedMarkerInfo.x,
                y = clickedMarkerInfo.y,
                prop = DiaryCardProp(
                    diaryCardLoadedPropMap = viewModel.diaryMap.mapValues { (_, value) ->
                        diaryCardLoadedPropMap[value.id]
                    },
                    onNewDiaryRequested = { page ->
                        viewModel.getDiaryFromServer(
                            page = page,
                            onSucceed = { /* TODO */ },
                            onFailed = { /* TODO */ },
                        )
                    }
                )
            )
        },
        diaryModificationBarProp = diaryModificationBarInfo?.let { info ->
            DiaryModificationBarProp(
                onModifyOptionClicked = {
                    diaryCardLoadedPropMap[info.targetDiaryId]?.apply {
                        diaryCardLoadedPropMap[id] = copy(
                            isFlipped = true,
                            diaryModificationModeProp = DiaryModificationModeProp(
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
                                            onSucceed = { /* TODO */ },
                                            onFailed = { /* TODO */ }
                                        )
                                        keyboard?.hide()
                                    }
                                }
                            )
                        )
                    }
                    diaryModificationBarInfo = null
                },
                onDeleteOptionClicked = {
                    viewModel.deleteDiary(
                        diaryId = info.targetDiaryId,
                        onSucceed = { diaryModificationBarInfo = null },
                        onFailed = { diaryModificationBarInfo = null }
                    )
                },
                onDismissed = { diaryModificationBarInfo = null },
            )
        },
        categorySelectionBarProp = if (isCategorySelectionBarVisible) CategorySelectionBarProp(
            userName = viewModel.userName,
            itemProps = viewModel.categoryList.map { categoryInfo ->
                CategoryItemProp(
                    name = categoryInfo.name,
                    color = categoryInfo.color,
                    count = categoryInfo.count,
                    onClicked = {
                        viewModel.selectShowingCategory(categoryId = categoryInfo.id)
                        isCategorySelectionBarVisible = false
                    }
                )
            },
            onNewCategoryButtonClicked = { onNavigateToCategoryApp() },
            onDismissed = { isCategorySelectionBarVisible = false }
        ) else null,
        onCategoryButtonClicked = {
            if (viewModel.selectedCategoryId != null) viewModel.selectShowingCategory(categoryId = null)
            isCategorySelectionBarVisible = !isCategorySelectionBarVisible
        },
        onLocationButtonClicked = {
            viewModel.moveMapToCurrentPosition(
                onSucceed = { /* empty */ },
                onFailed = {
                    permissionRequester.checkLocationPermission(
                        context = context,
                        requestOnNotGranted = true,
                    )
                }
            )
        },
    )
}

private fun ActivityResultLauncher<String>.checkLocationPermission(
    context: Activity,
    requestOnNotGranted: Boolean = shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)
): Boolean {
    val result = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!result && requestOnNotGranted) launch(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    return result
}
