package com.umc.record.screen

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.umc.design.CategoryColor
import com.umc.design.Primary300
import com.umc.design.component.LoadingModal
import com.umc.record.R
import com.umc.record.component.CategoryDropdown
import com.umc.record.component.CategoryDropdownProp
import com.umc.record.component.DiaryBottomSheet
import com.umc.record.component.DiaryBottomSheetProp
import com.umc.record.component.previewCategoryDropdownProp
import com.umc.record.component.previewDiaryBottomSheetProp
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

@Composable
fun RecordScreen(
    image: File?,
    date: LocalDateTime,
    location: String,
    selectedCategoryColor: CategoryColor?,
    showLoadingDialog: Boolean,
    categoryDropdownProp: CategoryDropdownProp?,
    diaryBottomSheetProp: DiaryBottomSheetProp,
    onDateChipClicked: () -> Unit,
    onLocationChipClicked: () -> Unit,
    onCategoryChipClicked: () -> Unit,
) {
    val density = LocalDensity.current

    // ✅ 카테고리 이름과 배경색 매핑
    val categoryBackgroundColors = mapOf(
        "RED" to Color(0xE5FFC0C0),
        "ORANGE" to Color(0xE5FFE0D3),
        "YELLOW" to Color(0xE5FFF4D4),
        "GREEN" to Color(0xE5E3FFCC),
        "TURQUOISE" to Color(0xE5D6FAF6),
        "BLUE" to Color(0xE5D4EFFF),
        "NAVY" to Color(0xE5D1DDFF),
        "PURPLE" to Color(0xE5EFD9FF),
        "BROWN" to Color(0xE5EBD9CF),
        "WHITE" to Color(0xE5FFFFFF),
        "PINK" to Color(0xE5FFC5E0),
        "BLACK" to Color(0xE5ACACAC)
    )
    // ✅ 기본 배경색
    val defaultBackgroundColor = Color(0xE6FDDDC1)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배경 이미지
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // 상단 칩 메뉴
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                var categoryChipCenterOffset by remember { mutableStateOf(Offset.Zero) }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        12.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
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
                                color = Color(0xE5FEF6F2),
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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Primary300,
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp)
                        )
                    }
                    // 위치
                    Row(
                        modifier = Modifier
                            .weight(1f, fill = false)
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
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Primary300,
                            )
                            Text(
                                text = location,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Primary300,
//                                modifier = Modifier.padding(end = 4.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    // 카테고리
                    val categoryName = selectedCategoryColor?.name
                    val categoryBackgroundColor = categoryName?.let {
                        categoryBackgroundColors[it]
                    } ?: defaultBackgroundColor

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                color = categoryBackgroundColor,
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
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Image(
                                painter = painterResource(
                                    id = selectedCategoryColor?.footIconId
                                        ?: R.drawable.ic_foot_default
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
                // 카테고리 드롭다운 메뉴
                categoryDropdownProp?.let { prop ->
                    var dropdownWidth by remember { mutableIntStateOf(0) }

                    Box(
                        modifier = Modifier
                            .onGloballyPositioned { dropdownWidth = it.size.width }
                    ) {
                        CategoryDropdown(
                            prop = prop,
                            selectedCategoryForDashes = selectedCategoryColor,
                            selectedCategoryForBackground = selectedCategoryColor,
                        )
                    }
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

    if (showLoadingDialog) LoadingModal()
}

@Preview(showBackground = true)
@Composable
fun PreviewRecordScreen() {
    val context = LocalContext.current

    RecordScreen(
        image = File(
            context.cacheDir,
            "image.jpg"
        ).apply {
            FileOutputStream(this).use {
                context.resources.openRawResource(R.raw.img_test).copyTo(it)
            }
        },
        date = LocalDateTime.now(),
        location = "Cafe PORTE Cafe PORTE Cafe PORTE Cafe PORTE Cafe PORTE",
        selectedCategoryColor = CategoryColor.GREEN,
        showLoadingDialog = false,
        categoryDropdownProp = previewCategoryDropdownProp,
        diaryBottomSheetProp = previewDiaryBottomSheetProp,
        onDateChipClicked = {},
        onLocationChipClicked = {},
        onCategoryChipClicked = {},
    )
}