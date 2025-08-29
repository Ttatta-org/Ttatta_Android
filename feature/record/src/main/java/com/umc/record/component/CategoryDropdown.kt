package com.umc.record.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.CategoryColor
import com.umc.record.R

data class CategoryDropdownProp(
    val itemProps: List<CategoryDropdownItemProp>,
    val onNewCategoryButtonClicked: () -> Unit,
)

data class CategoryDropdownItemProp(
    val color: CategoryColor?,
    val name: String,
    val onClicked: () -> Unit,
)

private val categoryDropdownWidth = 212.dp
private val categoryDropdownMaxHeight = 230.dp
private val categoryDropdownColor = Color(0xFFFEF6F2).copy(alpha = 0.9f)

@Composable
fun CategoryDropdown(
    prop: CategoryDropdownProp,
) {
    Column(
        modifier = Modifier
            .width(categoryDropdownWidth)
            .background(
                color = categoryDropdownColor,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .heightIn(max = categoryDropdownMaxHeight)
        ) {
            items(count = prop.itemProps.size * 2 + 1) { index ->
                if (index == prop.itemProps.size * 2) Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { prop.onNewCategoryButtonClicked() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp, start = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_foot_new),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.new_category),
                            fontSize = 12.sp,
                        )
                    }
                } else if (index and 1 == 1) Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp, horizontal = 8.dp) // 카테고리와 점선 간격 2dp
                        .height(1.dp) // 점선 높이 설정
                ) {
                    val dotSize = 5f // 점선 길이
                    val spaceSize = 5f // 점선 간 간격
                    val strokeWidth = 3f // 점선 두께
                    val startX = 0f
                    val endX = size.width

                    var currentX = startX
                    while (currentX < endX) {
                        drawLine(
                            color = Color(0xFFFCAD98), // 점선 색상
                            start = Offset(currentX, size.height / 2),
                            end = Offset(currentX + dotSize, size.height / 2),
                            strokeWidth = strokeWidth
                        )
                        currentX += dotSize + spaceSize
                    }
                } else {
                    val itemProp = prop.itemProps[index / 2]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { itemProp.onClicked() }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp, start = 16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = itemProp.color?.footIconId ?: R.drawable.ic_foot_default),  // TODO: 머지 후 아이콘이 바뀌지 않았다면 변경할 것
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = itemProp.name,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

val previewCategoryDropdownProp = CategoryDropdownProp(
    itemProps = CategoryColor.entries.map {
        CategoryDropdownItemProp(
            color = it,
            name = it.name,
            onClicked = {},
        )
    },
    onNewCategoryButtonClicked = {},
)

@Preview
@Composable
fun PreviewCategoryDropdown() {
    CategoryDropdown(prop = previewCategoryDropdownProp)
}