package com.umc.footprint

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.umc.core.model.DiaryForCard
import com.umc.footprint.component.CategoryItemProp
import com.umc.footprint.component.CategorySelectionBarProp
import com.umc.footprint.component.DiaryCardLoadedProp
import com.umc.footprint.component.DiaryCardProp
import com.umc.footprint.component.DiaryModificationBarProp
import com.umc.footprint.component.DiaryModificationModeProp

data class DiaryModificationBarInfo(
    val targetDiary: DiaryForCard
)

data class DiaryModificationModeInfo(
    val modifyingDiaryId: Long,
    val contentValueState: MutableState<String>,
)

@Composable
fun FootprintApp(
    viewModel: FootprintViewModel,
    onNavigateToCategoryApp: () -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current

    var isCategorySelectionBarVisible by remember { mutableStateOf(false) }
    var diaryModificationBarInfo by remember { mutableStateOf<DiaryModificationBarInfo?>(null) }
    var diaryModificationModeInfo by remember { mutableStateOf<DiaryModificationModeInfo?>(null) }

    val diaryCardLoadedPropMap = remember { mutableStateMapOf<Long, DiaryCardLoadedProp>() }

    LaunchedEffect(key1 = viewModel.diaryList) {
        if (viewModel.diaryList.isNotEmpty()) viewModel.diaryList.forEach { diary ->
            diaryCardLoadedPropMap[diary.id]?.let { prop ->
                diaryCardLoadedPropMap[diary.id] = prop.copy(
                    content = diary.content,
                )
            } ?: run {
                diaryCardLoadedPropMap[diary.id] = DiaryCardLoadedProp(
                    id = diary.id,
                    date = diary.date,
                    imageUrl = diary.imageUrl,
                    content = diary.content,
                    isFlipped = false,
                    onCardClicked = {
                        val diaryProp = diaryCardLoadedPropMap[diary.id]!!
                        diaryCardLoadedPropMap[diary.id] = diaryProp.copy(
                            isFlipped = !diaryProp.isFlipped
                        )
                    },
                    onModifyButtonClicked = {
                        diaryModificationBarInfo = DiaryModificationBarInfo(
                            targetDiary = diary
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
        mapView = viewModel.getMapView(),
        isCategorySelected = viewModel.selectedCategoryId != null,
        diaryCardProp = viewModel.clickedMarkerInfo?.let { clickedMarkerInfo ->
            PositionedDiaryCardProp(
                x = clickedMarkerInfo.x,
                y = clickedMarkerInfo.y,
                prop = DiaryCardProp(
                    diaryCardLoadedPropList = viewModel.diaryList.mapNotNull {
                        diaryCardLoadedPropMap[it.id]
                    },
                    diaryModificationModeProp = diaryModificationModeInfo?.let { info ->
                        DiaryModificationModeProp(
                            contentValue = info.contentValueState.value,
                            onContentValueChanged = { info.contentValueState.value = it },
                            onModificationDone = {
                                viewModel.modifyDiary(
                                    diaryId = info.modifyingDiaryId,
                                    content = info.contentValueState.value,
                                    onSucceed = { diaryModificationModeInfo = null },
                                    onFailed = { /* TODO */ }
                                )
                                keyboard?.hide()
                            },
                        )
                    },
                    isFullyLoaded = viewModel.isDiaryFullyLoaded,
                    onNewDiaryRequested = {
                        viewModel.getDiaryFromServer(
                            onSucceed = { /* TODO */ },
                            onFailed = { /* TODO */ },
                        )
                    }
                )
            )
        },
        diaryModificationBarProp = diaryModificationBarInfo?.let { info ->
            val dismissBar = { diaryModificationBarInfo = null }

            DiaryModificationBarProp(
                onModifyOptionClicked = {
                    diaryModificationModeInfo = DiaryModificationModeInfo(
                        contentValueState = mutableStateOf(info.targetDiary.content),
                        modifyingDiaryId = info.targetDiary.id,
                    )
                    dismissBar()
                },
                onDeleteOptionClicked = {
                    viewModel.deleteDiary(
                        diaryId = info.targetDiary.id,
                        onSucceed = { dismissBar() },
                        onFailed = { dismissBar() }
                    )
                },
                onDismissed = dismissBar,
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
            isCategorySelectionBarVisible = !isCategorySelectionBarVisible
        },
        onLocationButtonClicked = { viewModel.moveMapToCurrentPosition() },
    )
}
