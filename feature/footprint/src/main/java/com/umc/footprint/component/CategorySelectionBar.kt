package com.umc.footprint.component

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.design.CategoryColor
import com.umc.design.Grey400
import com.umc.design.Primary400
import com.umc.design.R
import com.umc.design.Secondary100

val categorySelectionBarShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)

data class CategorySelectionBarProp(
    val userName: String,
    val itemProps: List<CategoryItemProp>,
    val onNewCategoryButtonClicked: () -> Unit,
    val onDismissed: () -> Unit,
)

data class CategoryItemProp(
    val name: String,
    val color: CategoryColor?,
    val count: Int,
    val onClicked: () -> Unit,
)

@Composable
fun CategorySelectionBar(
    prop: CategorySelectionBarProp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .shadow(
                shape = categorySelectionBarShape,
                elevation = 16.dp
            )
            .background(
                color = Color.Secondary100,
                shape = categorySelectionBarShape,
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            // 제목 라인
            Image(
                painter = painterResource(id = R.drawable.ic_header_deco),
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
                        append(prop.userName)
                        append(stringResource(id = com.umc.footprint.R.string.category_list_suffix))
                        append(" ")
                        append(prop.itemProps.size.toString())
                    }, color = Color.Primary400
                )
                Text(
                    text = prop.itemProps.sumOf { it.count }.toString(),
                    color = Color.Grey400
                )
            }
            // 카테고리 목록
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(count = prop.itemProps.size * 2 + 1) { index ->
                    if (index == 0) {
                        // 새 카테고리 등록 버튼
                        Box(modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .clickable { prop.onNewCategoryButtonClicked() }) {
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
                                    painter = painterResource(id = com.umc.footprint.R.drawable.ic_new_category),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                )
                                Text(
                                    text = stringResource(id = com.umc.footprint.R.string.new_category),
                                )
                            }
                        }
                    } else if (index and 1 == 0) {
                        val itemProp = prop.itemProps[(index shr 1) - 1]
                        CategoryItem(prop = itemProp)
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
                    painter = painterResource(id = prop.color?.footIconId ?: R.drawable.ic_foot),
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

val previewCategorySelectionBarProp = CategorySelectionBarProp(
    userName = "test",
    itemProps = listOf(
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
    onDismissed = {},
)

@Preview
@Composable
fun PreviewCategorySelectionBar() {
    CategorySelectionBar(prop = previewCategorySelectionBarProp)
}
