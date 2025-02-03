package com.umc.footprint.component

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
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
    val diaryCardPagePropList: List<DiaryCardPageProp>,
    val diaryModificationModeProp: DiaryModificationModeProp?,
)

data class DiaryCardPageProp(
    val id: Long,
    val date: LocalDate,
    val imageUrl: String,
    val content: String,
    val isFlipped: Boolean,
    val onCardClicked: () -> Unit,
    val onModifyButtonClicked: () -> Unit,
)

data class DiaryModificationModeProp(
    val contentValue: String,
    val onContentValueChanged: (String) -> Unit,
    val onModificationDone: () -> Unit,
)

@Composable
fun DiaryCard(prop: DiaryCardProp) {
    val pagerState = rememberPagerState { prop.diaryCardPagePropList.size }
    val cardRotationAngles = remember { prop.diaryCardPagePropList.map { it.id to 0f }.toMutableStateMap() }

    val currentPage = pagerState.targetPage
    val currentProp = prop.diaryCardPagePropList[currentPage]

    LaunchedEffect(key1 = currentProp.isFlipped) {
        animate(
            initialValue = cardRotationAngles[currentProp.id] ?: 0f,
            targetValue = if (currentProp.isFlipped) 180f else 0f,
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing,
            )
        ) { value, _ ->
            cardRotationAngles[currentProp.id] = value
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.width(diaryCardWidth)
    ) { index ->
        val diaryCardPageProp = prop.diaryCardPagePropList[index]
        val rotateAngle = cardRotationAngles[diaryCardPageProp.id] ?: 0f

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
                    if (prop.diaryModificationModeProp == null)
                        diaryCardPageProp.onCardClicked()
                }
        ) {
            // 앞면
            if (rotateAngle < 90f) {
                DiaryCardFront(
                    date = diaryCardPageProp.date,
                    imageUrl = diaryCardPageProp.imageUrl,
                    onModifyButtonClicked = diaryCardPageProp.onModifyButtonClicked
                )
            }
            // 뒷면
            else Box(
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            ) {
                DiaryCardBack(
                    date = diaryCardPageProp.date,
                    content = diaryCardPageProp.content,
                    diaryModificationModeProp = prop.diaryModificationModeProp,
                    onModifyButtonClicked = diaryCardPageProp.onModifyButtonClicked
                )
            }
        }
    }
}

@Composable
private fun DiaryCardFront(
    date: LocalDate,
    imageUrl: String,
    onModifyButtonClicked: () -> Unit
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
                        text = "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일",
                        fontSize = with(density) { 12.dp.toSp() },
                        color = Color.Primary400,
                    )
                }
                IconButton(
                    onClick = onModifyButtonClicked,
                    modifier = Modifier.size(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_modify),
                        contentDescription = null,
                    )
                }
            }
            // 본문
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
private fun DiaryCardBack(
    date: LocalDate,
    content: String,
    diaryModificationModeProp: DiaryModificationModeProp?,
    onModifyButtonClicked: () -> Unit
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .width(diaryCardWidth)
            .height(diaryCardHeight)
    )  {
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
                        text = "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일",
                        fontSize = with(density) { 12.dp.toSp() },
                        color = Color.Primary400,
                    )
                }
                IconButton(
                    onClick = onModifyButtonClicked,
                    modifier = Modifier.size(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_modify),
                        contentDescription = null,
                    )
                }
            }
            // 본문
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Secondary300)
                    .padding(16.dp)
            ) {
                val textStyle = remember {
                    TextStyle(
                        color = Color.Grey500,
                        fontSize = 12.sp,
                    )
                }
                if (diaryModificationModeProp != null) {
                    val focusRequester = remember { FocusRequester() }
                    var focused by remember { mutableStateOf(false) }

                    BasicTextField(
                        value = diaryModificationModeProp.contentValue,
                        onValueChange = diaryModificationModeProp.onContentValueChanged,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { diaryModificationModeProp.onModificationDone() }),
                        textStyle = textStyle,
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged { state ->
                                if (state.isFocused)
                                    focused = true
                                else if (focused)
                                    diaryModificationModeProp.onModificationDone()
                            }
                            .onGloballyPositioned { focusRequester.requestFocus() }
                    )
                } else Text(
                    text = content,
                    style = textStyle,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

val previewDiaryCardProp = DiaryCardProp(
    diaryModificationModeProp = DiaryModificationModeProp(
        contentValue = "",
        onContentValueChanged = {},
        onModificationDone = {},
    ),
    diaryCardPagePropList = List(10) { index ->
        DiaryCardPageProp(
            id = index.toLong(),
            date = LocalDate.now(),
            imageUrl = "",
            content = "This is diary.",
            isFlipped = false,
            onCardClicked = {},
            onModifyButtonClicked = {},
        )
    }
)

@Preview
@Composable
fun PreviewDiaryCard() {
    var isModifyingMode by remember { mutableStateOf(false) }
    var content by remember { mutableStateOf("") }
    val isFlippedMap = remember { List(10) { it.toLong() to false }.toMutableStateMap() }
    val diaryCardPagePropList = remember {
        List(10) { index ->
            val diaryContent = "This is diary."
            DiaryCardPageProp(
                id = index.toLong(),
                date = LocalDate.now(),
                imageUrl = "",
                content = diaryContent,
                isFlipped = false,
                onCardClicked = { isFlippedMap[index.toLong()] = !isFlippedMap[index.toLong()]!! },
                onModifyButtonClicked = {
                    content = diaryContent
                    isFlippedMap[index.toLong()] = true
                    isModifyingMode = true
                },
            )
        }.toMutableStateList()
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        DiaryCard(
            prop = DiaryCardProp(
                diaryModificationModeProp = if (isModifyingMode) DiaryModificationModeProp(
                    contentValue = content,
                    onContentValueChanged = { content = it },
                    onModificationDone = {
                        isModifyingMode = false
                        content = ""
                    }
                ) else null,
                diaryCardPagePropList = diaryCardPagePropList.map {
                    it.copy(isFlipped = isFlippedMap[it.id]!!)
                }
            )
        )
    }
}

@Preview
@Composable
fun PreviewDiaryCardFront() {
    DiaryCardFront(
        date = previewDiaryCardProp.diaryCardPagePropList.first().date,
        imageUrl = previewDiaryCardProp.diaryCardPagePropList.first().imageUrl,
        onModifyButtonClicked = previewDiaryCardProp.diaryCardPagePropList.first().onModifyButtonClicked
    )
}

@Preview
@Composable
fun PreviewDiaryCardBack() {
    DiaryCardBack(
        date = previewDiaryCardProp.diaryCardPagePropList.first().date,
        content = previewDiaryCardProp.diaryCardPagePropList.first().content,
        diaryModificationModeProp = null,
        onModifyButtonClicked = previewDiaryCardProp.diaryCardPagePropList.first().onModifyButtonClicked
    )
}