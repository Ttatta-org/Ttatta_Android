package com.umc.footprint

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.CategoryColor
import com.umc.design.Grey400
import com.umc.design.Grey500
import com.umc.design.Primary400
import com.umc.design.Secondary100
import com.umc.design.Secondary300
import com.umc.footprint.design.ShadowedImage
import java.time.LocalDate
import com.umc.design.R as Res

data class CategoryItemProp(
    val name: String,
    val color: CategoryColor?,
    val count: Int,
    val onClicked: () -> Unit,
)

data class DiaryCardProp(
    val x: Float,
    val y: Float,
    val date: LocalDate,
    val image: ImageBitmap,
    val content: String,
    val isFlipped: Boolean,
    val onCardClicked: () -> Unit,
    val onModifyButtonClicked: () -> Unit,
)

@Composable
fun FootprintScreen(
    mapView: @Composable () -> Unit,
    diaryCardProp: DiaryCardProp?,
    isCategoryMenuVisible: Boolean,
    userName: String,
    categoryItemProps: List<CategoryItemProp>,
    onNewCategoryButtonClicked: () -> Unit,
    onCategoryButtonClicked: () -> Unit,
    onLocationButtonClicked: () -> Unit,
) {
    val density = LocalDensity.current
    var screenHeight by remember { mutableStateOf(0.dp) }
    val diaryCardWidth = 261.dp
    val diaryCardHeight = 311.dp

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 지도
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            mapView()
            // 일기 팝업
            diaryCardProp?.let { prop ->
                Box(
                    modifier = Modifier
                        .offset(
                            x = with(density) { prop.x.toDp() - diaryCardWidth / 2 },
                            y = with(density) { prop.y.toDp() - diaryCardHeight - 12.dp },
                        )
                        .width(diaryCardWidth)
                        .height(diaryCardHeight)
                        .clickable { prop.onCardClicked() }
                ) {
                    ShadowedImage(
                        id = if (!prop.isFlipped)
                            R.drawable.view_diary_popup
                        else
                            R.drawable.view_diary_popup_flipped,
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
                                    painter = painterResource(id = Res.drawable.ic_header_deco),
                                    contentDescription = null,
                                    modifier = Modifier.width(32.dp),
                                )
                                Text(
                                    text = "${prop.date.year}년 ${prop.date.monthValue}월 ${prop.date.dayOfMonth}일",
                                    fontSize = with(density) { 12.dp.toSp() } ,
                                    color = Color.Primary400,
                                )
                            }
                            IconButton(
                                onClick = prop.onModifyButtonClicked,
                                modifier = Modifier.size(16.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_modify),
                                    contentDescription = null,
                                )
                            }
                        }
                        // 본문
                        if (!prop.isFlipped) Image(
                            bitmap = prop.image,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(220.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) else Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .size(220.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Secondary300)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = prop.content,
                                color = Color.Grey500,
                                fontSize = 12.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                with(density) { screenHeight = it.size.height.toDp() }
            }) {
            // 플로팅 버튼
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 카테고리 선택
                    IconButton(
                        onClick = onCategoryButtonClicked, modifier = Modifier.size(64.dp)
                    ) {
                        ShadowedImage(
                            id = if (isCategoryMenuVisible) R.drawable.ic_floating_button_category_selected
                            else R.drawable.ic_floating_button_category_unselected,
                            contentDescription = null,
                            width = 64.dp,
                            height = 64.dp,
                        )
                    }
                    // 내 위치로 이동
                    IconButton(
                        onClick = onLocationButtonClicked,
                        modifier = Modifier.size(64.dp),
                    ) {
                        ShadowedImage(
                            id = R.drawable.ic_floating_button_location,
                            contentDescription = null,
                            width = 64.dp,
                            height = 64.dp,
                        )
                    }
                }
            }
            // 카테고리 메뉴
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        ambientColor = Color.Black,
                    )
                    .background(Color.Secondary100)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .animateContentSize()
                        .heightIn(max = if (isCategoryMenuVisible) screenHeight.times(0.6f) else 0.dp)
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    // 제목 라인
                    Image(
                        painter = painterResource(id = Res.drawable.ic_header_deco),
                        contentDescription = null,
                        modifier = Modifier.width(32.dp),
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(userName)
                                append(stringResource(id = R.string.category_list_suffix))
                                append(" ")
                                append(categoryItemProps.size.toString())
                            }, color = Color.Primary400
                        )
                        Text(
                            text = categoryItemProps.sumOf { it.count }.toString(),
                            color = Color.Grey400
                        )
                    }
                    // 카테고리 목록
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(count = categoryItemProps.size * 2 + 1) { index ->
                            if (index == 0) {
                                // 새 카테고리 등록 버튼
                                Box(modifier = Modifier
                                    .clip(RoundedCornerShape(percent = 50))
                                    .clickable { onNewCategoryButtonClicked() }) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .padding(
                                                vertical = 4.dp, horizontal = 16.dp
                                            )
                                            .fillMaxWidth()
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_new_category),
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp),
                                        )
                                        Text(
                                            text = stringResource(id = R.string.new_category),
                                        )
                                    }
                                }
                            } else if (index and 1 == 0) {
                                val prop = categoryItemProps[(index shr 1) - 1]
                                CategoryItem(prop = prop)
                            } else {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(color = Color.Grey400)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(prop: CategoryItemProp) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(percent = 50))
        .clickable { prop.onClicked() }) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = prop.color?.footIconId ?: Res.drawable.ic_foot),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
                Text(
                    text = prop.name,
                )
            }
            Text(
                text = prop.count.toString(), color = Color.Grey400
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFootprintScreen() {
    FootprintScreen(
        mapView = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "This is map.")
            }
        },
        diaryCardProp = DiaryCardProp(
            x = 600f,
            y = 1000f,
            date = LocalDate.now(),
            image = ImageBitmap(width = 220, height = 220),
            content = "This is diary.",
            isFlipped = true,
            onCardClicked = {},
            onModifyButtonClicked = {},
        ),
        isCategoryMenuVisible = true,
        userName = "test",
        categoryItemProps = listOf(
            CategoryItemProp(
                name = "친구들",
                color = CategoryColor.YELLOW,
                count = 10,
                onClicked = {},
            ), CategoryItemProp(
                name = "가족",
                color = CategoryColor.GREEN,
                count = 7,
                onClicked = {},
            ), CategoryItemProp(
                name = "연인",
                color = CategoryColor.BLUE,
                count = 14,
                onClicked = {},
            ), CategoryItemProp(
                name = "일상",
                color = null,
                count = 14,
                onClicked = {},
            ), CategoryItemProp(
                name = "다시 오고싶은 장소",
                color = CategoryColor.BLUE,
                count = 20,
                onClicked = {},
            ), CategoryItemProp(
                name = "?",
                color = null,
                count = 14,
                onClicked = {},
            )
        ),
        onNewCategoryButtonClicked = {},
        onCategoryButtonClicked = {},
        onLocationButtonClicked = {},
    )
}