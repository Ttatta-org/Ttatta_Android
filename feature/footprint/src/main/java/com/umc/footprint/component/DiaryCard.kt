package com.umc.footprint.component

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.umc.design.Grey500
import com.umc.design.Primary400
import com.umc.design.Secondary300
import com.umc.footprint.R
import java.time.LocalDate

val diaryCardWidth = 261.dp
val diaryCardHeight = 311.dp

data class DiaryCardProp(
    val clusterId: Long,
    val diaryCardLoadedPropMap: Map<Int, DiaryCardLoadedProp?>,
    val onNewDiaryRequested: (Int) -> Unit,
)

data class DiaryCardLoadedProp(
    val id: Long,
    val date: LocalDate,
    val imageUrl: String,
    val content: String,
    val isFlipped: Boolean,
    val diaryModificationModeProp: DiaryModificationModeProp?,
    val onCardClicked: () -> Unit,
    val onModifyButtonClicked: () -> Unit,
)

class DiaryCardFrontProp(
    val date: LocalDate,
    val imageUrl: String,
    val onModifyButtonClicked: () -> Unit,
)

class DiaryCardBackProp(
    val date: LocalDate,
    val content: String,
    val diaryModificationModeProp: DiaryModificationModeProp?,
    val onModifyButtonClicked: () -> Unit
)

data class DiaryModificationModeProp(
    val contentValue: String,
    val onContentValueChanged: (String) -> Unit,
    val onModificationDone: () -> Unit,
)

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
        } ?: true,
        modifier = Modifier.width(diaryCardWidth),
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
                .graphicsLayer {
                    rotationY = rotateAngle
                    cameraDistance = 8 * density
                }
                .clickable(
                    indication = null,
                    interactionSource = null,
                ) {
                    diary?.let { diary ->
                        if (diary.diaryModificationModeProp == null)
                            diary.onCardClicked()
                    }
                }
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
                    }
                )
            }
            // 뒷면
            else Box(
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            ) {
                DiaryCardBack(
                    prop = diary?.let { diary ->
                        DiaryCardBackProp(
                            date = diary.date,
                            content = diary.content,
                            diaryModificationModeProp = diary.diaryModificationModeProp,
                            onModifyButtonClicked = diary.onModifyButtonClicked
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun DiaryCardFront(
    prop: DiaryCardFrontProp?
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .width(diaryCardWidth)
            .height(diaryCardHeight)
    ) {
        // 카드 이미지
        ShadowedImage(
            id = R.drawable.view_diary_popup,
            contentDescription = null,
            width = diaryCardWidth,
            height = diaryCardHeight,
            shadowColor = Color(0x806E38DE)
        )
        // 카드 내용
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(top = 12.dp, bottom = 36.dp)
                .fillMaxWidth()
        ) {
            // 제목 라인
            Box(
                modifier = Modifier.padding(horizontal = 32.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = com.umc.design.R.drawable.ic_header_deco),
                        contentDescription = null,
                        modifier = Modifier.width(32.dp),
                    )
                    Text(
                        text = prop?.let {
                            "${prop.date.year}년 ${prop.date.monthValue}월 ${prop.date.dayOfMonth}일"
                        } ?: stringResource(id = R.string.loading),
                        fontSize = with(density) { 12.dp.toSp() },
                        color = Color.Primary400,
                    )
                }
                IconButton(
                    onClick = { prop?.onModifyButtonClicked?.invoke() },
                    modifier = Modifier.size(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_modify),
                        contentDescription = null,
                    )
                }
            }
            // 본문
            if (prop != null) Image(
                painter = rememberAsyncImagePainter(model = prop.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) else Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun DiaryCardBack(
    prop: DiaryCardBackProp?
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .width(diaryCardWidth)
            .height(diaryCardHeight)
    ) {
        ShadowedImage(
            id = R.drawable.view_diary_popup_flipped,
            contentDescription = null,
            width = diaryCardWidth,
            height = diaryCardHeight,
            shadowColor = Color(0x806E38DE)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(top = 12.dp, bottom = 36.dp)
                .fillMaxWidth()
        ) {
            // 제목 라인
            Box(
                modifier = Modifier.padding(horizontal = 32.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = com.umc.design.R.drawable.ic_header_deco),
                        contentDescription = null,
                        modifier = Modifier.width(32.dp),
                    )
                    Text(
                        text = prop?.let {
                            "${prop.date.year}년 ${prop.date.monthValue}월 ${prop.date.dayOfMonth}일"
                        } ?: stringResource(id = R.string.loading),
                        fontSize = with(density) { 12.dp.toSp() },
                        color = Color.Primary400,
                    )
                }
                IconButton(
                    onClick = { prop?.onModifyButtonClicked?.invoke() },
                    modifier = Modifier.size(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_modify),
                        contentDescription = null,
                    )
                }
            }
            // 본문
            if (prop != null) Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Secondary300)
                    .padding(16.dp)
            ) {
                if (prop.diaryModificationModeProp != null) {
                    val focusRequester = remember { FocusRequester() }

                    BasicTextField(
                        value = prop.diaryModificationModeProp.contentValue,
                        onValueChange = prop.diaryModificationModeProp.onContentValueChanged,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { prop.diaryModificationModeProp.onModificationDone() }
                        ),
                        textStyle = TextStyle(
                            color = Color.Grey500,
                            fontSize = 13.sp,
                        ),
                        modifier = Modifier.focusRequester(focusRequester)
                    )

                    LaunchedEffect(key1 = Unit) { focusRequester.requestFocus() }
                } else Text(
                    text = prop.content,
                    style = TextStyle(
                        color = Color.Grey500,
                        fontSize = 13.sp,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)
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

@Preview
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
                            }
                        )
                    )
                },
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        repeat(3) { makeNewDiaryCardLoadedProp() }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
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

private val previewDiaryCardFrontProp = DiaryCardFrontProp(
    date = LocalDate.now(),
    imageUrl = "",
    onModifyButtonClicked = {},
)

@Preview
@Composable
fun PreviewDiaryCardFront() {
    DiaryCardFront(
        prop = null // previewDiaryCardFrontProp
    )
}

private val previewDiaryCardBackProp = DiaryCardBackProp(
    date = LocalDate.now(),
    content = "This is diary.",
    diaryModificationModeProp = null,
    onModifyButtonClicked = {},
)

@Preview
@Composable
fun PreviewDiaryCardBack() {
    DiaryCardBack(
        prop = null // previewDiaryCardBackProp
    )
}