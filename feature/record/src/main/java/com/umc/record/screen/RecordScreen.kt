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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.umc.design.CategoryColor
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.record.R
import com.umc.record.component.CategoryDropdown
import com.umc.record.component.CategoryDropdownItemProp
import com.umc.record.component.DiaryBottomSheet
import com.umc.record.component.ShadowedImage
import com.umc.record.util.RecordCategoryColorScheme
import com.umc.record.util.translucentShadow
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class CategoryDropdownProp(
    val itemProps: List<CategoryDropdownItemProp>,
    val onNewCategoryButtonClicked: () -> Unit,
)

@Composable
fun RecordScreen(
    image: File?,
    date: LocalDateTime,
    location: String?,
    selectedCategoryColor: CategoryColor,
    showLocationMissingTooltip: Boolean,
    userName: String,
    diaryContent: String,
    isSubmitButtonEnabled: Boolean,
    categoryDropdownProp: CategoryDropdownProp?,
    onCreateButtonClicked: () -> Unit,
    onDiaryContentChanged: (String) -> Unit,
    onDateChipClicked: () -> Unit,
    onLocationChipClicked: () -> Unit,
    onCategoryChipClicked: () -> Unit,
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배경 이미지
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColorTheme.current.grey[700])
        )
        // 상단 칩 메뉴
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
            ) {
                var chipHeight: Dp? by remember { mutableStateOf(null) }
                var locationChipCenterOffset: Dp? by remember { mutableStateOf(null) }
                var categoryChipCenterOffset: Dp? by remember { mutableStateOf(null) }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.4.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(
                            top = 32.dp + WindowInsets.systemBars
                                .asPaddingValues()
                                .calculateTopPadding()
                        )
                        .fillMaxWidth()
                        .onSizeChanged { chipHeight = with(density) { it.height.toDp() } }
                ) {
                    // 날짜
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .translucentShadow(borderRadius = 14.dp)
                            .background(
                                color = LocalColorTheme.current.secondary[100].copy(0.9f),
                                shape = RoundedCornerShape(14.dp),
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onDateChipClicked() }
                            .let { chipHeight?.let { height -> it.height(height) } ?: it }
                    ) {
                        Text(
                            text = date
                                .toLocalDate()
                                .run { "%d.%02d.%02d".format(year, monthValue, dayOfMonth) },
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.W700,
                            letterSpacing = (-0.4).sp,
                            color = LocalColorTheme.current.primary[500],
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp)
                        )
                    }
                    // 위치
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .translucentShadow(borderRadius = 14.dp)
                            .background(
                                color = LocalColorTheme.current.secondary[100].copy(0.9f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onLocationChipClicked() }
                            .onGloballyPositioned { coordinates ->
                                val position = coordinates.positionInParent().x
                                val length = coordinates.size.width

                                locationChipCenterOffset = with(density) {
                                    (position + length / 2f).toDp()
                                }
                            }
                            .let { chipHeight?.let { height -> it.height(height) } ?: it }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.width(10.6.dp),
                        )
                        if (location != null) Text(
                            text = location,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.W800,
                            letterSpacing = (-0.4).sp,
                            color = LocalColorTheme.current.primary[500],
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    // 카테고리
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .translucentShadow(borderRadius = 14.dp)
                            .background(
                                color = RecordCategoryColorScheme.ChipBackgroundColor[selectedCategoryColor]!!.copy(
                                    alpha = 0.9f
                                ),
                                shape = RoundedCornerShape(14.dp),
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onCategoryChipClicked() }
                            .onGloballyPositioned {
                                val position = it.positionInParent().x
                                val length = it.size.width

                                categoryChipCenterOffset = with(density) {
                                    (position + length / 2f).toDp()
                                }
                            }
                            .let { chipHeight?.let { height -> it.height(height) } ?: it }
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(CategoryColor.entries.first().footV2IconId),
                            contentDescription = null,
                            tint = RecordCategoryColorScheme.ChipFootColor[selectedCategoryColor]!!,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 위치 정보 추가 툴팁
                    if (showLocationMissingTooltip) locationChipCenterOffset?.let { offset ->
                        Box(
                            modifier = Modifier.offset(x = offset - 88.5f.dp)
                        ) {
                            ShadowedImage(
                                id = R.raw.img_record_location_info_missing_tooltip,
                                contentDescription = null,
                                width = 177.dp,
                                height = 40.5.dp,
                                shadowBlur = 10.dp,
                                shadowColor = Color(0xFFDE806E).copy(alpha = 0.4f),
                            )
                        }
                    }
                    // 카테고리 드롭다운 메뉴
                    if (categoryDropdownProp != null) categoryChipCenterOffset?.let { offset ->
                        Box(
                            modifier = Modifier.offset(x = offset - 190.dp)
                        ) {
                            CategoryDropdown(
                                itemProps = categoryDropdownProp.itemProps,
                                selectedCategoryColor = selectedCategoryColor,
                                onNewCategoryButtonClicked = categoryDropdownProp.onNewCategoryButtonClicked,
                            )
                        }
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
                userName = userName,
                diaryContent = diaryContent,
                isButtonEnabled = isSubmitButtonEnabled,
                onCreateButtonClicked = onCreateButtonClicked,
                onDiaryContentChanged = onDiaryContentChanged,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRecordScreen() {
    ThemeProvider {
        RecordScreen(
            image = null,
            date = LocalDate
                .parse("2025.12.26", DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                .atStartOfDay(),
            location = "Cate PORTE Cate PORTE Cate PORTE",
            selectedCategoryColor = CategoryColor.GREEN,
            showLocationMissingTooltip = true,
            userName = "김따따",
            diaryContent = "asdf",
            isSubmitButtonEnabled = true,
            categoryDropdownProp = CategoryDropdownProp(
                itemProps = CategoryColor.entries.map {
                    CategoryDropdownItemProp(
                        color = it,
                        name = it.name,
                        onClicked = {},
                    )
                },
                onNewCategoryButtonClicked = {},
            ),
            onDateChipClicked = {},
            onLocationChipClicked = {},
            onCategoryChipClicked = {},
            onCreateButtonClicked = {},
            onDiaryContentChanged = {},
        )
    }
}