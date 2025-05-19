package com.umc.footprint.component.card

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import com.umc.footprint.model.prop.DiaryCardBackProp
import com.umc.footprint.model.prop.DiaryCardFrontProp
import com.umc.footprint.model.prop.DiaryCardLoadedProp
import com.umc.footprint.model.prop.DiaryCardProp
import com.umc.footprint.model.prop.DiaryModificationModeProp
import java.time.LocalDate
import kotlin.collections.get

@Composable
fun DiaryCard(prop: DiaryCardProp) {
    val cardRotationAngles = remember { mutableStateMapOf<Long, Float>() }
    val pagerState = rememberPagerState { 1 + (prop.diaryCardLoadedPropMap.keys.maxOrNull() ?: 0) }

    LaunchedEffect(key1 = prop.clusterId) {
        prop.onNewDiaryRequested(0)
    }

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = prop.diaryCardLoadedPropMap[pagerState.currentPage]?.let {
            it.diaryModificationModeProp == null
        } != false,
        modifier = Modifier.Companion.width(diaryCardFrameSize.width + diaryCardFrameShadowRadius * 2),
        pageSpacing = diaryCardFrameShadowRadius * 2,
        beyondViewportPageCount = 2,
    ) { page ->
        val diary = prop.diaryCardLoadedPropMap[page]
        val rotateAngle = cardRotationAngles[diary?.id] ?: 0f

        LaunchedEffect(key1 = Unit) {
            prop.onNewDiaryRequested(page + 1)
        }

        LaunchedEffect(key1 = diary?.isFlipped) {
            diary?.let { diary ->
                animate(
                    initialValue = rotateAngle,
                    targetValue = if (diary.isFlipped) 180f else 0f,
                    animationSpec = tween(durationMillis = 200)
                ) { value, _ ->
                    cardRotationAngles[diary.id] = value
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(horizontal = diaryCardFrameShadowRadius)
                .graphicsLayer {
                    rotationY = rotateAngle
                    cameraDistance = 8 * density
                }
                .clickable(
                    indication = null,
                    interactionSource = null,
                ) {
                    diary?.let { diary ->
                        if (diary.diaryModificationModeProp == null) diary.onCardClicked()
                    }
                },
        ) {
            // 앞면
            if (rotateAngle < 90f) {
                DiaryCardFront(
                    prop = diary?.let { diary ->
                        DiaryCardFrontProp(
                            date = diary.date,
                            imageUrl = diary.imageUrl,
                            onModifyButtonClicked = diary.onModifyButtonClicked
                        )
                    },
                )
            }
            // 뒷면
            else Box(
                modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                DiaryCardBack(
                    prop = diary?.let { diary ->
                        DiaryCardBackProp(
                            date = diary.date,
                            content = diary.content,
                            diaryModificationModeProp = diary.diaryModificationModeProp,
                            onModifyButtonClicked = diary.onModifyButtonClicked
                        )
                    },
                )
            }
        }
    }
}

val previewDiaryCardProp = DiaryCardProp(
    clusterId = 1L,
    diaryCardLoadedPropMap = List(10) { index ->
        index to DiaryCardLoadedProp(
            id = index.toLong(),
            date = LocalDate.now(),
            imageUrl = "",
            content = "This is diary.",
            isFlipped = false,
            diaryModificationModeProp = null,
            onCardClicked = {},
            onModifyButtonClicked = {},
        )
    }.toMap(),
    onNewDiaryRequested = {},
)

@Preview(showBackground = true)
@Composable
fun PreviewDiaryCard() {
    val diaryCardLoadedPropMap = remember { mutableStateMapOf<Int, DiaryCardLoadedProp>() }
    val makeNewDiaryCardLoadedProp: () -> Unit = remember {
        {
            val page = diaryCardLoadedPropMap.size
            val id = diaryCardLoadedPropMap.size.toLong()
            diaryCardLoadedPropMap[page] = DiaryCardLoadedProp(
                id = id,
                date = LocalDate.now(),
                imageUrl = "",
                content = "This is diary.",
                isFlipped = false,
                diaryModificationModeProp = null,
                onCardClicked = {
                    val diary = diaryCardLoadedPropMap[page]!!
                    diaryCardLoadedPropMap[page] = diary.copy(isFlipped = !diary.isFlipped)
                },
                onModifyButtonClicked = {
                    val diary = diaryCardLoadedPropMap[page]!!
                    var contentState by mutableStateOf(diary.content)
                    diaryCardLoadedPropMap[page] = diary.copy(
                        diaryModificationModeProp = DiaryModificationModeProp(
                            contentValue = contentState,
                            onContentValueChanged = { contentState = it },
                            onModificationDone = {
                                diaryCardLoadedPropMap[page] = diary.copy(
                                    content = contentState,
                                    diaryModificationModeProp = null,
                                )
                            },
                        ),
                    )
                },
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        repeat(3) { makeNewDiaryCardLoadedProp() }
    }

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
    ) {
        DiaryCard(
            prop = DiaryCardProp(
                clusterId = 1L,
                diaryCardLoadedPropMap = diaryCardLoadedPropMap,
                onNewDiaryRequested = { makeNewDiaryCardLoadedProp() },
            )
        )
    }
}

