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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.CategoryColor
import com.umc.design.theme.ThemeProvider
import com.umc.record.R
import com.umc.record.util.RecordCategoryColorScheme
import com.umc.record.util.SpeechBubbleTopTailShape
import com.umc.design.R as Res

data class CategoryDropdownItemProp(
    val color: CategoryColor?,
    val name: String,
    val onClicked: () -> Unit,
)

private val categoryDropdownWidth = 212.dp
private val categoryDropdownMaxHeight = 212.dp

@Composable
fun CategoryDropdown(
    itemProps: List<CategoryDropdownItemProp>,
    onNewCategoryButtonClicked: () -> Unit,
    selectedCategoryColor: CategoryColor,
) {
    val bubbleShape = remember {
        SpeechBubbleTopTailShape(
            cornerRadius = 12.dp,
            tailWidth = 18.dp,
            tailHeight = 8.dp,
            tailOffsetFromRight = 12.dp
        )
    }

    Column(
        modifier = Modifier
            .width(categoryDropdownWidth)
            .background(
                RecordCategoryColorScheme.CategoryDropdownBackgroundColor[selectedCategoryColor]!!,
                bubbleShape
            )
            .clip(bubbleShape)
            .padding(top = 10.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .heightIn(max = categoryDropdownMaxHeight)
        ) {
            items(count = itemProps.size * 2 + 1) { index ->
                if (index == itemProps.size * 2) Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNewCategoryButtonClicked() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp, start = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_record_footprint_new_category),
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
                        .padding(horizontal = 11.dp)
                        .height(1.dp)
                ) {
                    val dotSize = 2.dp.toPx()
                    val spaceSize = 3.dp.toPx()
                    val strokeWidth = 0.5f.dp.toPx()
                    val dashColor = RecordCategoryColorScheme.ChipFootColor[selectedCategoryColor]!!

                    var currentX = 0f
                    while (currentX < size.width) {
                        drawLine(
                            color = dashColor,
                            start = Offset(currentX, size.height / 2),
                            end = Offset(currentX + dotSize, size.height / 2),
                            strokeWidth = strokeWidth
                        )

                        currentX += dotSize + spaceSize
                    }
                } else {
                    val itemProp = itemProps[index / 2]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { itemProp.onClicked() }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 19.dp),
                        ) {
                            Image(
                                painter = painterResource(
                                    id = itemProp.color?.footV2IconId ?: Res.drawable.ic_foot,
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = itemProp.name,
                                fontSize = 13.sp,
                                lineHeight = 13.sp,
                                letterSpacing = (-0.8).sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCategoryDropdown() {
    ThemeProvider {
        CategoryDropdown(
            itemProps = CategoryColor.entries.map {
                CategoryDropdownItemProp(
                    color = it,
                    name = it.name,
                    onClicked = {},
                )
            },
            selectedCategoryColor = CategoryColor.RED,
            onNewCategoryButtonClicked = {},
        )
    }
}