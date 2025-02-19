package com.umc.record.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import com.umc.design.CategoryColor
import com.umc.design.Primary300
import com.umc.record.R
import com.umc.record.component.CategoryDropdown
import com.umc.record.component.CategoryDropdownProp
import com.umc.record.component.DiaryBottomSheet
import com.umc.record.component.DiaryBottomSheetProp
import com.umc.record.component.previewCategoryDropdownProp
import com.umc.record.component.previewDiaryBottomSheetProp
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun RecordScreen(
    image: ImageBitmap,
    date: LocalDateTime,
    location: String,
    selectedCategoryColor: CategoryColor?,
    categoryDropdownProp: CategoryDropdownProp?,
    diaryBottomSheetProp: DiaryBottomSheetProp,
    onDateChipClicked: () -> Unit,
    onLocationChipClicked: () -> Unit,
    onCategoryChipClicked: () -> Unit,
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배경 이미지
        Image(
            bitmap = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // 상단 칩 메뉴
        Column {
            var categoryChipCenterOffset by remember { mutableStateOf(Offset.Zero) }

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 32.dp + WindowInsets.systemBars
                            .asPaddingValues()
                            .calculateTopPadding()
                    )
            ) {
                var maxHeight by remember { mutableStateOf<Dp?>(null) }

                // 날짜
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = Color(0xFEF6F2E5),
                            shape = RoundedCornerShape(percent = 50)
                        )
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable { onDateChipClicked() }
                        .onGloballyPositioned {
                            val height = with(density) { it.size.height.toDp() }
                            maxHeight = maxHeight?.let { maxHeight ->
                                max(maxHeight, height)
                            } ?: height
                        }
                        .let {
                            maxHeight?.let { maxHeight -> it.height(maxHeight) } ?: it
                        }
                ) {
                    Text(
                        text = date.toLocalDate().toString(),
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        color = Color.Primary300,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                // 위치
                Row(
                    modifier = Modifier
                        .background(
                            color = Color(0xFEF6F2E5),
                            shape = RoundedCornerShape(percent = 50)
                        )
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable { onLocationChipClicked() }
                        .onGloballyPositioned {
                            val height = with(density) { it.size.height.toDp() }
                            maxHeight = maxHeight?.let { maxHeight ->
                                max(maxHeight, height)
                            } ?: height
                        }
                        .let {
                            maxHeight?.let { maxHeight -> it.height(maxHeight) } ?: it
                        }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.Primary300,
                        )
                        Text(
                            text = location,
                            fontSize = 15.sp,
                            color = Color.Primary300,
                            modifier = Modifier.padding(end = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                // 카테고리
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(
                            color = Color(0xFEF6F2E5),
                            shape = RoundedCornerShape(percent = 50)
                        )
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable { onCategoryChipClicked() }
                        .onGloballyPositioned {
                            val height = with(density) { it.size.height.toDp() }
                            maxHeight = maxHeight?.let { maxHeight ->
                                max(maxHeight, height)
                            } ?: height
                            categoryChipCenterOffset = it.positionInParent().let { offset ->
                                Offset(
                                    x = offset.x + it.size.width / 2,
                                    y = offset.y + it.size.height / 2
                                )
                            }
                        }
                        .let {
                            maxHeight?.let { maxHeight -> it.height(maxHeight) } ?: it
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(
                                id = selectedCategoryColor?.footIconId ?: R.drawable.ic_foot_default
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            // 카테고리 드롭다운 메뉴
            categoryDropdownProp?.let { prop ->
                var dropdownWidth by remember { mutableIntStateOf(0) }

                Box(
                    modifier = Modifier
                        .offset {
                            Offset(
                                x = categoryChipCenterOffset.x - dropdownWidth,
                                y = 16.dp.toPx(),
                            ).round()
                        }
                        .onGloballyPositioned { dropdownWidth = it.size.width }
                ) {
                    CategoryDropdown(prop = prop)
                }
            }
        }
        // 바텀 시트
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            DiaryBottomSheet(
                prop = diaryBottomSheetProp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecordScreen() {
    val context = LocalContext.current

    RecordScreen(
        image = BitmapFactory.decodeStream(
            context.resources.openRawResource(R.raw.img_test)
        ).asImageBitmap(),
        date = LocalDateTime.now(),
        location = "Cafe PORTE",
        selectedCategoryColor = CategoryColor.GREEN,
        categoryDropdownProp = previewCategoryDropdownProp,
        diaryBottomSheetProp = previewDiaryBottomSheetProp,
        onDateChipClicked = {},
        onLocationChipClicked = {},
        onCategoryChipClicked = {},
    )
}