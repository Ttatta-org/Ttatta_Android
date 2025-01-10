package com.umc.footprint

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class CategoryItemProp(
    val name: String,
    val color: Color,
    val count: Int,
    val onClick: () -> Unit,
)

@Composable
fun CategorySelectionCard(
    userName: String,
    props: List<CategoryItemProp>,
    onNewCategoryButtonClicked: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(32.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // 제목 라인
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(userName)
                        append(stringResource(id = R.string.category_list_suffix))
                        append(" ")
                        append(props.size.toString())
                    }
                )
                Text(
                    text = props.sumOf { it.count }.toString()
                )
            }
            // 카테고리 목록
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(count = props.size * 2 + 1) { index ->
                    if (index == 0) {
                        // 새 카테고리 등록 버튼
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .clickable { onNewCategoryButtonClicked() }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.new_category),
                                )
                            }
                        }
                    } else if (index and 1 == 0) {
                        val prop = props[(index shr 1) - 1]
                        CategoryItem(prop = prop)
                    } else {
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(prop: CategoryItemProp) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .clickable { prop.onClick() }
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

                Text(
                    text = prop.name,
                )
            }
            Text(
                text = prop.count.toString()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategorySelectionCard() {
    CategorySelectionCard(
        userName = "test",
        props = listOf(
            CategoryItemProp(
                name = "친구들",
                color = Color.Yellow,
                count = 10,
                onClick = {},
            ),
            CategoryItemProp(
                name = "가족",
                color = Color.Green,
                count = 7,
                onClick = {},
            ),
            CategoryItemProp(
                name = "연인",
                color = Color.Blue,
                count = 14,
                onClick = {},
            ),
            CategoryItemProp(
                name = "일상",
                color = Color.Red,
                count = 14,
                onClick = {},
            )
        ),
    )
}