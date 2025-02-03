package com.umc.footprint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.umc.core.model.DiaryForCard
import com.umc.footprint.component.CategoryItemProp
import com.umc.footprint.component.CategorySelectionBarProp
import com.umc.footprint.component.DiaryCardPageProp
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
    val isDiaryCardFlippedMap =
        remember { viewModel.diaryList.map { it.id to false }.toMutableStateMap() }
    var diaryModificationBarInfo by remember { mutableStateOf<DiaryModificationBarInfo?>(null) }
    var diaryModificationModeInfo by remember { mutableStateOf<DiaryModificationModeInfo?>(null) }

    LaunchedEffect(key1 = viewModel.diaryList) {
        viewModel.diaryList.forEach {
            if (isDiaryCardFlippedMap[it.id] == null)
                isDiaryCardFlippedMap[it.id] = false
        }
    }

    FootprintScreen(
        mapView = viewModel.getMapView(),
        diaryCardProp = viewModel.clickedMarkerInfo?.let { clickedMarkerInfo ->
            PositionedDiaryCardProp(
                x = clickedMarkerInfo.x,
                y = clickedMarkerInfo.y,
                prop = DiaryCardProp(
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
                    diaryCardPagePropList = viewModel.diaryList.map { diary ->
                        DiaryCardPageProp(
                            id = diary.id,
                            date = diary.date,
                            imageUrl = diary.imageUrl,
                            content = diary.content,
                            isFlipped = isDiaryCardFlippedMap[diary.id] ?: false,
                            onCardClicked = {
                                isDiaryCardFlippedMap[diary.id]?.let { isFlipped ->
                                    isDiaryCardFlippedMap[diary.id] = !isFlipped
                                }
                            },
                            onModifyButtonClicked = {
                                diaryModificationBarInfo = DiaryModificationBarInfo(
                                    targetDiary = diary
                                )
                            },
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
            itemProps = viewModel.categoryInfoList.map { categoryInfo ->
                CategoryItemProp(
                    name = categoryInfo.name,
                    color = categoryInfo.color,
                    count = categoryInfo.count,
                    onClicked = { viewModel.selectShowingCategory(categoryId = categoryInfo.id) }
                )
            },
            onNewCategoryButtonClicked = { onNavigateToCategoryApp() },
            onDismissed = { isCategorySelectionBarVisible = false }
        ) else null,
        onCategoryButtonClicked = { isCategorySelectionBarVisible = !isCategorySelectionBarVisible },
        onLocationButtonClicked = { viewModel.moveMapToCurrentPosition() },
    )
}
