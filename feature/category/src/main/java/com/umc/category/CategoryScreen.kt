package com.umc.category

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.category.component.CategoryDeletionDialog
import com.umc.category.component.CategoryDeletionDialogProp
import com.umc.category.component.CategoryManagementBar
import com.umc.category.component.CategoryManagementBarProp
import com.umc.category.component.CategoryModificationBar
import com.umc.category.component.CategoryModificationBarProp
import com.umc.design.CategoryColor
import com.umc.design.Grey300
import com.umc.design.Primary200
import com.umc.design.Primary300
import com.umc.design.Primary500
import com.umc.design.Secondary100
import java.util.Locale
import com.umc.design.R as Res

data class CategoryListItemProp(
    val name: String,
    val color: CategoryColor?,
    val onClicked: () -> Unit,
)

@Composable
fun CategoryScreen(
    maxCategoryNameLength: Int,
    categoryNameInputFieldValue: String,
    selectedCategoryColor: CategoryColor?,
    categoryList: List<CategoryListItemProp>,
    categoryManagementBarProp: CategoryManagementBarProp?,
    categoryModificationBarProp: CategoryModificationBarProp?,
    categoryDeletionDialogProp: CategoryDeletionDialogProp?,
    onCategoryNameInputFieldValueChanged: (String) -> Unit,
    onCategoryColorClicked: (CategoryColor) -> Unit,
    onDoneButtonClicked: () -> Unit,
) {
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Secondary100)
    ) {
        Spacer(modifier = Modifier.height(statusBarHeight))
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 32.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // 새로 만들기
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 48.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                ) {
                    // 이름 입력 창
                    Column(
                        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.new_footprint),
                            fontWeight = FontWeight.Bold,
                            color = Color.Primary300,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        BasicTextField(
                            value = categoryNameInputFieldValue,
                            onValueChange = onCategoryNameInputFieldValueChanged,
                            textStyle = TextStyle(
                                fontSize = 12.sp
                            )
                        ) { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        color = Color.Primary500,
                                        shape = RoundedCornerShape(percent = 50)
                                    )
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(percent = 50)
                                    )
                                    .padding(vertical = 16.dp, horizontal = 24.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        contentAlignment = Alignment.CenterStart,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(with(density) { 16.sp.toDp() })
                                    ) {
                                        if (categoryNameInputFieldValue.isBlank()) Text(
                                            text = stringResource(id = R.string.footprint_placeholder),
                                            fontSize = 12.sp,
                                            color = Color.Grey300,
                                        )
                                        innerTextField()
                                    }
                                    Text(
                                        text = buildAnnotatedString {
                                            append(categoryNameInputFieldValue.length.toString())
                                            withStyle(
                                                style = SpanStyle(color = Color.Grey300)
                                            ) {
                                                append("/${maxCategoryNameLength}")
                                            }
                                        },
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                        }
                    }
                    // 색상 선택 창
                    Column(
                        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = Res.drawable.ic_header_deco),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.color_choice),
                                fontWeight = FontWeight.Bold,
                                color = Color.Primary300
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
                            modifier = Modifier
                                .horizontalScroll(state = rememberScrollState())
                        ) {
                            CategoryColor.entries.forEach { color ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.clickable { onCategoryColorClicked(color) }
                                ) {
                                    Image(
                                        painter = painterResource(id = color.flowerIconId),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    if (color == selectedCategoryColor) Image(
                                        painter = painterResource(id = R.drawable.ic_check),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                // 완료 버튼
                ElevatedButton(
                    shape = RoundedCornerShape(percent = 50),
                    onClick = onDoneButtonClicked,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color.Primary200,
                    ),
                    modifier = Modifier.fillMaxWidth(0.5f),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.done),
                        color = Color.White
                    )
                }
            }
            // 카테고리 목록
            Column(
                verticalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = Res.drawable.ic_header_deco),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.category_list),
                        fontWeight = FontWeight.Bold,
                        color = Color.Primary300
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (categoryList.isNotEmpty()) {
                        items(count = categoryList.size * 2 - 1) { index ->
                            if (index and 1 == 0) CategoryListItem(categoryList[index / 2])
                            else HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }

    categoryManagementBarProp?.let { CategoryManagementBar(prop = it) }
    categoryModificationBarProp?.let { CategoryModificationBar(prop = it) }
    categoryDeletionDialogProp?.let { CategoryDeletionDialog(prop = it) }
}

@Composable
private fun CategoryListItem(
    prop: CategoryListItemProp
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = prop.color?.footIconId ?: Res.drawable.ic_foot),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(32.dp)
            )
            Text(text = prop.name)
        }
        IconButton(
            onClick = prop.onClicked,
            modifier = Modifier.size(32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_modify),
                contentDescription = null,
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoryScreen() {
    var fieldValue by remember { mutableStateOf("") }

    CategoryScreen(
        maxCategoryNameLength = 20,
        categoryNameInputFieldValue = fieldValue,
        selectedCategoryColor = CategoryColor.RED,
        categoryManagementBarProp = null,
        categoryModificationBarProp = null,
        categoryDeletionDialogProp = null,
        categoryList = CategoryColor.entries.map { color ->
            CategoryListItemProp(
                name = color.name.lowercase(Locale.ROOT),
                color = color,
                onClicked = {},
            )
        },
        onDoneButtonClicked = {},
        onCategoryNameInputFieldValueChanged = { fieldValue = it },
        onCategoryColorClicked = {},
    )
}