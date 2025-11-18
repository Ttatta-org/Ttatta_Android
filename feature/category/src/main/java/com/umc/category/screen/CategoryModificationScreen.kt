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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
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
import com.umc.design.R as Res

@Composable
fun CategoryModificationScreen(
    topBarTitle: String,
    maxCategoryNameLength: Int,
    categoryNameInputFieldValue: String,
    selectedCategoryColor: CategoryColor?,
    isDoneButtonEnabled: Boolean,
    onCategoryNameInputFieldValueChanged: (String) -> Unit,
    onCategoryColorClicked: (CategoryColor) -> Unit,
    onDoneButtonClicked: () -> Unit,
    onBackButtonClicked: () -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val colorSelectionBarScrollState = rememberScrollState()

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
                modifier = Modifier
                    .padding(22.dp)
                    .weight(1f)
            ) {
                // 카테고리명
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
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
                        CustomButton(
                            text = stringResource(id = R.string.done),
                            isEnabled = isDoneButtonEnabled,
                            onClick = onDoneButtonClicked,
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

@Preview(showBackground = true)
@Composable
fun PreviewCategoryModificationScreen() {
    var fieldValue by remember { mutableStateOf("") }

    ThemeProvider {
        CategoryModificationScreen(
            topBarTitle = "발자국 새로 만들기 및 수정",
            maxCategoryNameLength = 20,
            categoryNameInputFieldValue = fieldValue,
            selectedCategoryColor = CategoryColor.WHITE,
            isDoneButtonEnabled = true,
            onDoneButtonClicked = {},
            onCategoryNameInputFieldValueChanged = { fieldValue = it },
            onCategoryColorClicked = {},
            onBackButtonClicked = {},
        )
    }
}