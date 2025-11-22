package com.umc.category.screen

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.category.R
import com.umc.category.component.TopBar
import com.umc.design.CategoryColor
import com.umc.design.component.CustomButton
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme
import com.umc.design.theme.ThemeProvider
import java.util.Locale
import com.umc.design.R as Res

data class CategoryListItemProp(
    val name: String,
    val color: CategoryColor?,
    val onClicked: (() -> Unit)?,
)

@Composable
fun CategoryScreen(
    topBarTitle: String,
    maxCategoryNameLength: Int,
    categoryNameInputFieldValue: String,
    selectedCategoryColor: CategoryColor?,
    categoryList: List<CategoryListItemProp>?,
    onCategoryNameInputFieldValueChanged: (String) -> Unit,
    onCategoryColorClicked: (CategoryColor) -> Unit,
    onDoneButtonClicked: () -> Unit,
    onBackButtonClicked: () -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val colorSelectionBarScrollState = rememberScrollState()
    val categoryListScrollState = rememberLazyListState()

    var top by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = LocalColorTheme.current.secondary[100])
        ) {
            Spacer(modifier = Modifier.height(top))
            Column(
                verticalArrangement = Arrangement.spacedBy(space = 32.dp),
                modifier = Modifier.padding(top = 32.dp, start = 22.dp, end = 22.dp)
            ) {
                // 카테고리명
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(space = 48.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                    ) {
                        // 이름 입력 창
                        Column(
                            verticalArrangement = Arrangement.spacedBy(space = 11.dp)
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
                                    text = stringResource(id = R.string.new_category),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.W600,
                                    color = LocalColorTheme.current.primary[500],
                                )
                            }
                            BasicTextField(
                                value = categoryNameInputFieldValue,
                                onValueChange = onCategoryNameInputFieldValueChanged,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
                                textStyle = TextStyle(
                                    fontFamily = LocalFontTheme.current.font,
                                    fontSize = 12.sp
                                )
                            ) { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 1.dp,
                                            color = LocalColorTheme.current.primary[300],
                                            shape = RoundedCornerShape(percent = 50)
                                        )
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(percent = 50)
                                        )
                                        .padding(vertical = 13.dp, horizontal = 28.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.CenterStart,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            if (categoryNameInputFieldValue.isBlank()) Text(
                                                text = stringResource(id = R.string.footprint_placeholder),
                                                fontSize = 13.sp,
                                                lineHeight = 20.sp,
                                                fontWeight = FontWeight.W400,
                                                color = LocalColorTheme.current.grey[500],
                                            )
                                            innerTextField()
                                        }
                                        Text(
                                            text = buildAnnotatedString {
                                                append(categoryNameInputFieldValue.length.toString())
                                                withStyle(
                                                    style = SpanStyle(color = LocalColorTheme.current.grey[600])
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
                                    fontWeight = FontWeight.W600,
                                    fontSize = 15.sp,
                                    color = LocalColorTheme.current.primary[500],
                                )
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
                                    modifier = Modifier
                                        .horizontalScroll(state = colorSelectionBarScrollState)
                                ) {
                                    CategoryColor.entries.forEach { color ->
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.clickable(
                                                interactionSource = null,
                                                indication = null,
                                            ) {
                                                onCategoryColorClicked(color)
                                            }
                                        ) {
                                            Image(
                                                painter = painterResource(id = color.flowerIconId),
                                                contentDescription = null,
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            if (color == selectedCategoryColor) Icon(
                                                painter = painterResource(id = R.drawable.ic_check),
                                                contentDescription = null,
                                                tint = if (color == CategoryColor.WHITE) Color.Black else Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                                // 좌우 블러
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (colorSelectionBarScrollState.canScrollForward) Box(
                                        contentAlignment = Alignment.CenterEnd,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    brush = Brush.horizontalGradient(
                                                        0f to Color.Transparent,
                                                        1f to LocalColorTheme.current.secondary[100],
                                                    )
                                                )
                                                .size(32.dp)
                                        )
                                    }
                                    if (colorSelectionBarScrollState.canScrollBackward) Box(
                                        contentAlignment = Alignment.CenterStart,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    brush = Brush.horizontalGradient(
                                                        0f to LocalColorTheme.current.secondary[100],
                                                        1f to Color.Transparent,
                                                    )
                                                )
                                                .size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // 완료 버튼
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier.widthIn(max = 260.dp)
                        ) {
                            CustomButton(
                                text = stringResource(id = R.string.create),
                                onClick = onDoneButtonClicked,
                            )
                        }
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
                            fontWeight = FontWeight.W600,
                            color = LocalColorTheme.current.primary[500],
                        )
                    }
                    Box {
                        LazyColumn(
                            state = categoryListScrollState,
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (categoryList == null) {
                                item {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        CircularProgressIndicator(
                                            color = LocalColorTheme.current.primary[500],
                                            modifier = Modifier
                                                .size(30.dp)
                                                .padding(vertical = 100.dp)
                                        )
                                    }
                                }
                            } else {
                                items(count = categoryList.size) { index ->
                                    CategoryListItem(categoryList[index])
                                }

                                item {
                                    Spacer(modifier = Modifier.height(100.dp))
                                }
                            }
                        }
                        if (categoryListScrollState.canScrollBackward) Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.verticalGradient(
                                        0f to LocalColorTheme.current.secondary[100],
                                        1f to Color.Transparent,
                                    )
                                )
                                .fillMaxWidth()
                                .height(32.dp)
                        )
                    }
                }
            }
        }

        TopBar(
            topBarTitle = topBarTitle,
            onHeightChanged = { top = it },
            onBackButtonClicked = onBackButtonClicked,
        )
    }
}

@Composable
private fun CategoryListItem(
    prop: CategoryListItemProp
) {
    Box(
        modifier = Modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 1.dp,
                color = LocalColorTheme.current.primary[100],
                shape = RoundedCornerShape(18.dp)
            )
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
                Text(
                    text = prop.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W400,
                    color = LocalColorTheme.current.grey[700],
                )
            }
            if (prop.onClicked != null) IconButton(
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
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoryScreen() {
    var fieldValue by remember { mutableStateOf("") }

    ThemeProvider {
        CategoryScreen(
            topBarTitle = "발자국 새로 만들기 및 수정",
            maxCategoryNameLength = 20,
            categoryNameInputFieldValue = fieldValue,
            selectedCategoryColor = CategoryColor.WHITE,
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
            onBackButtonClicked = {},
        )
    }
}