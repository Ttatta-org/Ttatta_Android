package com.umc.footprint.modal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.CategoryColor
import com.umc.design.component.CustomBottomSheet
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.R
import com.umc.footprint.model.prop.CategoryItemProp
import com.umc.footprint.model.prop.CategorySelectionBarProp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionBar(
    prop: CategorySelectionBarProp,
) {
    val density = LocalDensity.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val scrollState = rememberLazyListState()

    CustomBottomSheet(
        sheetState = sheetState,
        containerColor = LocalColorTheme.current.secondary[100],
        onDismissRequest = prop.onDismiss,
    ) {
        Column(
            modifier = Modifier.onSizeChanged {
                with(density) { prop.onHeightChanged(it.height.toDp()) }
            },
        ) {
            // 제목 라인
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .padding(top = 7.dp, bottom = 16.dp, start = 34.dp, end = 34.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(prop.userName)
                        append(stringResource(id = R.string.category_list_suffix))
                        append(" ")
                        append(prop.itemProps.size.toString())
                    },
                    color = LocalColorTheme.current.primary[500],
                    fontWeight = FontWeight.W700,
                    fontSize = 15.sp,
                )
                Text(
                    text = prop.itemProps
                        .sumOf { it.count ?: 0 }
                        .toString(),
                    color = LocalColorTheme.current.grey[600],
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                )
            }
            // 카테고리 목록
            Box {
                LazyColumn(
                    state = scrollState,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .heightIn(max = 500.dp)
                        .padding(horizontal = 22.dp)
                ) {
                    items(count = prop.itemProps.size + 1) { index ->
                        if (index == 0) {
                            // 새 카테고리 등록 버튼
                            CategoryItem(
                                prop = CategoryItemProp(
                                    name = stringResource(id = R.string.new_footprint),
                                    icon = R.drawable.ic_new_category,
                                    count = null,
                                    onClicked = prop.onNewCategoryButtonClicked
                                ),
                            )
                        } else {
                            CategoryItem(prop = prop.itemProps[index - 1])
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(22.dp))
                    }
                }
                if (scrollState.canScrollBackward) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    0f to LocalColorTheme.current.secondary[100],
                                    1f to LocalColorTheme.current.secondary[100].copy(alpha = 0f),
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(prop: CategoryItemProp) {
    Box(
        modifier = Modifier
            .background(
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 1.dp,
                color = LocalColorTheme.current.primary[100],
                shape = RoundedCornerShape(18.dp),
            )
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = prop.onClicked)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 11.dp, horizontal = 17.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = prop.icon),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = prop.name,
                    fontWeight = FontWeight.W400,
                    fontSize = 13.sp,
                )
            }
            prop.count?.let {
                Text(
                    text = it.toString(),
                    color = LocalColorTheme.current.grey[600],
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

val previewCategorySelectionBarProp = CategorySelectionBarProp(
    userName = "test",
    itemProps = listOf(
        CategoryItemProp(
            name = "친구들",
            icon = CategoryColor.YELLOW.footV2IconId,
            count = 10,
            onClicked = {},
        ),
        CategoryItemProp(
            name = "가족",
            icon = CategoryColor.GREEN.footV2IconId,
            count = 7,
            onClicked = {},
        ),
        CategoryItemProp(
            name = "연인",
            icon = CategoryColor.BLUE.footV2IconId,
            count = 14,
            onClicked = {},
        ),
        CategoryItemProp(
            name = "일상",
            icon = com.umc.design.R.raw.ic_foot_default_v2,
            count = 14,
            onClicked = {},
        ),
        CategoryItemProp(
            name = "다시 오고싶은 장소",
            icon = CategoryColor.BLUE.footV2IconId,
            count = 20,
            onClicked = {},
        ),
        CategoryItemProp(
            name = "?",
            icon = com.umc.design.R.raw.ic_foot_default_v2,
            count = 14,
            onClicked = {},
        ),
    ).let {
        it + it + it
    },
    onNewCategoryButtonClicked = {},
    onDismiss = {},
    onHeightChanged = {},
)

@Preview
@Composable
fun PreviewCategorySelectionBar() {
    ThemeProvider {
        CategorySelectionBar(prop = previewCategorySelectionBarProp)
    }
}
