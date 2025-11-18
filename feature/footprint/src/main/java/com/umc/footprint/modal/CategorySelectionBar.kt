package com.umc.footprint.modal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.CategoryColor
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
    val navigationBarHeight = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    ModalBottomSheet(
        scrimColor = Color.Transparent,
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        containerColor = LocalColorTheme.current.secondary[100],
        dragHandle = {
            Box(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = com.umc.design.R.drawable.ic_header_deco),
                    contentDescription = null,
                    modifier = Modifier.width(32.dp),
                )
            }
        },
        onDismissRequest = prop.onDismiss,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 22.dp)
        ) {
            // 제목 라인
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .padding(16.dp)
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.heightIn(max = 500.dp)
            ) {
                items(count = prop.itemProps.size + 1) { index ->
                    if (index == 0) {
                        // 새 카테고리 등록 버튼
                        CategoryItem(
                            prop = CategoryItemProp(
                                name = stringResource(id = R.string.new_category),
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
            Spacer(modifier = Modifier.height(navigationBarHeight))
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
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = prop.icon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
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
            icon = CategoryColor.YELLOW.footIconId,
            count = 10,
            onClicked = {},
        ),
        CategoryItemProp(
            name = "가족",
            icon = CategoryColor.GREEN.footIconId,
            count = 7,
            onClicked = {},
        ),
        CategoryItemProp(
            name = "연인",
            icon = CategoryColor.BLUE.footIconId,
            count = 14,
            onClicked = {},
        ),
        CategoryItemProp(
            name = "일상",
            icon = com.umc.design.R.drawable.ic_foot,
            count = 14,
            onClicked = {},
        ),
        CategoryItemProp(
            name = "다시 오고싶은 장소",
            icon = CategoryColor.BLUE.footIconId,
            count = 20,
            onClicked = {},
        ),
        CategoryItemProp(
            name = "?",
            icon = com.umc.design.R.drawable.ic_foot,
            count = 14,
            onClicked = {},
        ),
    ).let {
        it + it + it
    },
    onNewCategoryButtonClicked = {},
    onDismiss = {},
)

@Preview
@Composable
fun PreviewCategorySelectionBar() {
    ThemeProvider {
        CategorySelectionBar(prop = previewCategorySelectionBarProp)
    }
}
