package com.umc.footprint.component.card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.design.CategoryColor
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.core.DesignConstant
import com.umc.footprint.model.prop.DiaryCardBackLoadedProp
import com.umc.footprint.model.prop.DiaryCardBackProp
import com.umc.footprint.model.prop.DiaryCardFrontLoadedProp
import com.umc.footprint.model.prop.DiaryCardFrontProp
import com.umc.footprint.model.prop.DiaryCardHorizontalPageArrowDirection
import com.umc.footprint.model.prop.DiaryCardHorizontalPageArrowProp
import com.umc.footprint.model.prop.DiaryCardLoadedProp
import com.umc.footprint.model.prop.DiaryCardProp
import com.umc.footprint.model.prop.DiaryModificationModeProp
import com.umc.footprint.util.fadingEdgesHorizontal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

private val diaryCardVerticalPadding = 32.dp

@Composable
fun DiaryCard(prop: DiaryCardProp) {
    val colors = LocalColorTheme.current
    val cardRotationAngles = remember { mutableStateMapOf<Long, Float>() }
    val pagerState = rememberPagerState { 1 + (prop.diaryCardLoadedPropMap.keys.maxOrNull() ?: 0) }
    val isScrollEnabled = prop.diaryCardLoadedPropMap[pagerState.currentPage]?.let {
        it.diaryModificationModeProp == null
    } != false

    LaunchedEffect(key1 = prop.clusterId) {
        prop.onNewDiaryRequested(0)
    }

    Box(
        modifier = Modifier
            .width(DesignConstant.DiaryCardSizeWithShadowArea.width)
            .height(DesignConstant.DiaryCardSizeWithShadowArea.height + diaryCardVerticalPadding * 2)
            .offset(y = -diaryCardVerticalPadding)
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = isScrollEnabled,
            modifier = Modifier
                .fillMaxSize()
                .fadingEdgesHorizontal(fadeWidth = DesignConstant.DiaryCardFrameShadowRadius),
            beyondViewportPageCount = 2,
        ) { page ->
            val diary = prop.diaryCardLoadedPropMap[page]
            val rotateAngle = cardRotationAngles[diary?.id] ?: 0f

            LaunchedEffect(key1 = prop.clusterId) {
                prop.onNewDiaryRequested(page + 1)
            }

            LaunchedEffect(key1 = diary?.isFlipped) {
                diary?.let { diary ->
                    animate(
                        initialValue = rotateAngle,
                        targetValue = if (diary.isFlipped) 180f else 0f,
                        animationSpec = tween(durationMillis = 200),
                    ) { value, _ ->
                        cardRotationAngles[diary.id] = value
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(
                        horizontal = DesignConstant.DiaryCardFrameShadowRadius,
                        vertical = diaryCardVerticalPadding,
                    )
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
                Box(
                    modifier = Modifier.alpha(if (rotateAngle < 90f) 1f else 0f),
                ) {
                    DiaryCardFront(
                        prop = DiaryCardFrontProp(
                            defaultColor = if (page == 0) prop.defaultCategoryColor else prop.diaryCardLoadedPropMap[page - 1]?.categoryColor,
                            prop = diary?.let { diary ->
                                DiaryCardFrontLoadedProp(
                                    date = diary.date,
                                    categoryColor = diary.categoryColor,
                                    imageUrl = diary.imageUrl,
                                    onModifyButtonClicked = diary.onModifyButtonClicked
                                )
                            },
                        ),
                    )
                }
                // 뒷면
                Box(
                    modifier = Modifier
                        .graphicsLayer { rotationY = 180f }
                        .alpha(if (rotateAngle < 90f) 0f else 1f),
                ) {
                    DiaryCardBack(
                        prop = DiaryCardBackProp(
                            defaultColor = if (page == 0) prop.defaultCategoryColor else prop.diaryCardLoadedPropMap[page - 1]?.categoryColor,
                            prop = diary?.let { diary ->
                                DiaryCardBackLoadedProp(
                                    date = diary.date,
                                    categoryColor = diary.categoryColor,
                                    content = diary.content,
                                    diaryModificationModeProp = diary.diaryModificationModeProp,
                                    onModifyButtonClicked = diary.onModifyButtonClicked
                                )
                            },
                        ),
                    )
                }
            }
        }

        // 좌우 스크롤 표지 화살표
        Box(
            modifier = Modifier.padding(vertical = diaryCardVerticalPadding)
        ) {
            val scope = rememberCoroutineScope()
            var targetPage by remember { mutableIntStateOf(0) }

            val currentDiaryCategoryColor = prop.diaryCardLoadedPropMap[targetPage]?.categoryColor
            val isLeftPageExist = targetPage > 0
            val isRightPageExist = prop.diaryCardLoadedPropMap.containsKey(targetPage + 1)

            LaunchedEffect(pagerState) {
                scope.launch {
                    snapshotFlow { pagerState.targetPage }.collect { targetPage = it }
                }
            }

            DiaryCardHorizontalPageArrowDirection.entries.forEach { direction ->
                Box(
                    modifier = Modifier.offset(
                        x = when (direction) {
                            DiaryCardHorizontalPageArrowDirection.LEFT -> -DesignConstant.DiaryCardHorizontalPageArrowSize.width
                            DiaryCardHorizontalPageArrowDirection.RIGHT -> DesignConstant.DiaryCardSizeWithShadowArea.width
                        },
                        y = DesignConstant.DiaryCardSize.height / 2 - DesignConstant.DiaryCardHorizontalPageArrowSize.height / 2
                    )
                ) {
                    AnimatedVisibility(
                        visible = when (direction) {
                            DiaryCardHorizontalPageArrowDirection.LEFT -> isLeftPageExist
                            DiaryCardHorizontalPageArrowDirection.RIGHT -> isRightPageExist
                        },
                        enter = fadeIn(tween(durationMillis = diaryCardFrameAnimationDurationMillis)),
                        exit = fadeOut(tween(durationMillis = diaryCardFrameAnimationDurationMillis)),
                    ) {
                        DiaryCardHorizontalPageArrow(
                            prop = DiaryCardHorizontalPageArrowProp(
                                direction = direction,
                                outerColor = currentDiaryCategoryColor?.b ?: colors.primary[400],
                                innerColor = currentDiaryCategoryColor?.a ?: colors.secondary[200],
                                colorAnimationDuration = 200,
                                onClicked = {
                                    if (isScrollEnabled) scope.launch {
                                        pagerState.animateScrollToPage(
                                            page = pagerState.targetPage + when (direction) {
                                                DiaryCardHorizontalPageArrowDirection.LEFT -> -1
                                                DiaryCardHorizontalPageArrowDirection.RIGHT -> 1
                                            }
                                        )
                                    }
                                },
                            )
                        )
                    }
                }
            }
        }
    }
}

val previewDiaryCardLoadedProp = DiaryCardLoadedProp(
    id = 1L,
    date = LocalDate.now(),
    categoryColor = CategoryColor.RED,
    imageUrl = "",
    content = "This is diary.",
    isFlipped = false,
    diaryModificationModeProp = null,
    onCardClicked = {},
    onModifyButtonClicked = {},
)

val previewDiaryCardProp = DiaryCardProp(
    clusterId = 1L,
    defaultCategoryColor = CategoryColor.BLUE,
    diaryCardLoadedPropMap = List(10) { index ->
        index to previewDiaryCardLoadedProp
    }.toMap(),
    onNewDiaryRequested = {},
)

@Preview(showBackground = true)
@Composable
fun PreviewDiaryCard() {
    val scope = rememberCoroutineScope()
    val diaryCardLoadedPropMap = remember { mutableStateMapOf<Int, DiaryCardLoadedProp>() }

    val makeNewDiaryCardLoadedProp: () -> Unit = remember {
        {
            val page = diaryCardLoadedPropMap.size
            val id = diaryCardLoadedPropMap.size.toLong()
            diaryCardLoadedPropMap[page] = previewDiaryCardLoadedProp.copy(
                id = id,
                categoryColor = CategoryColor.entries.random(),
                onCardClicked = {
                    diaryCardLoadedPropMap[page]?.let { diary ->
                        diaryCardLoadedPropMap[page] = diary.copy(isFlipped = !diary.isFlipped)
                    }
                },
                onModifyButtonClicked = {
                    diaryCardLoadedPropMap[page]?.let { diary ->
                        val contentState = MutableStateFlow(diary.content)

                        scope.launch {
                            contentState.collect { currentContent ->
                                diaryCardLoadedPropMap[page] = diary.copy(
                                    isFlipped = true,
                                    diaryModificationModeProp = DiaryModificationModeProp(
                                        contentValue = currentContent,
                                        onContentValueChanged = { contentState.value = it },
                                        onModificationDone = {
                                            diaryCardLoadedPropMap[page] = diary.copy(
                                                isFlipped = true,
                                                content = currentContent,
                                                diaryModificationModeProp = null,
                                            )
                                        },
                                    ),
                                )
                            }
                        }
                    }
                },
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        repeat(3) { makeNewDiaryCardLoadedProp() }
    }

    ThemeProvider {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFD7F3D0))
        ) {
            DiaryCard(
                prop = DiaryCardProp(
                    clusterId = 1L,
                    defaultCategoryColor = CategoryColor.BLUE,
                    diaryCardLoadedPropMap = diaryCardLoadedPropMap,
                    onNewDiaryRequested = onNewDiaryRequested@{ page ->
                        if (diaryCardLoadedPropMap.containsKey(page) || page >= 10) return@onNewDiaryRequested
                        makeNewDiaryCardLoadedProp()
                    },
                )
            )
        }
    }
}

