package com.umc.category.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.umc.category.R
import com.umc.design.CategoryColor
import com.umc.design.Grey300
import com.umc.design.Primary200
import com.umc.design.Primary300
import com.umc.design.Primary500
import com.umc.design.R as Res

data class CategoryModificationBarProp(
    val maxCategoryNameLength: Int,
    val categoryNameInputFieldValue: String,
    val selectedColor: CategoryColor?,
    val onDismissed: () -> Unit,
    val onCategoryNameInputFieldValueChanged: (String) -> Unit,
    val onCategoryColorClicked: (CategoryColor) -> Unit,
    val onDoneButtonClicked: () -> Unit,
)

private val categoryModificationBarShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)

@Composable
fun CategoryModificationBar(
    prop: CategoryModificationBarProp
) {
    val density = LocalDensity.current

    InsetProviderDialog(
        onDismissed = prop.onDismissed
    ) { inset ->
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        shape = categoryModificationBarShape, elevation = 8.dp
                    )
                    .background(
                        color = Color.White, shape = categoryModificationBarShape
                    )
                    .clickable(
                        indication = null,
                        interactionSource = null,
                        onClick = {},
                    )
            ) {
                Column {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(space = 48.dp),
                        modifier = Modifier.padding(
                            start = 32.dp,
                            end = 32.dp,
                            top = 16.dp,
                            bottom = 32.dp
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = Res.drawable.ic_header_deco),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = stringResource(id = R.string.modify),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(space = 24.dp)
                            ) {
                                // 이름 입력 창
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.modify_footprint),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Primary300,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    BasicTextField(
                                        value = prop.categoryNameInputFieldValue,
                                        onValueChange = prop.onCategoryNameInputFieldValueChanged,
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
                                                    if (prop.categoryNameInputFieldValue.isBlank()) Text(
                                                        text = stringResource(id = R.string.footprint_placeholder),
                                                        fontSize = 12.sp,
                                                        color = Color.Grey300,
                                                    )
                                                    innerTextField()
                                                }
                                                Text(
                                                    text = buildAnnotatedString {
                                                        append(prop.categoryNameInputFieldValue.length.toString())
                                                        withStyle(
                                                            style = SpanStyle(color = Color.Grey300)
                                                        ) {
                                                            append("/${prop.maxCategoryNameLength}")
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
                                            painter = painterResource(id = com.umc.design.R.drawable.ic_header_deco),
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
                                                modifier = Modifier.clickable {
                                                    prop.onCategoryColorClicked(
                                                        color
                                                    )
                                                }
                                            ) {
                                                Image(
                                                    painter = painterResource(id = color.flowerIconId),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Fit,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                                if (color == prop.selectedColor) Image(
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
                        }
                        ElevatedButton(
                            shape = RoundedCornerShape(percent = 50),
                            onClick = prop.onDoneButtonClicked,
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
                    inset()
                }
            }
        }
    }
}

val previewCategoryModificationBarProp = CategoryModificationBarProp(
    maxCategoryNameLength = 20,
    categoryNameInputFieldValue = "test",
    selectedColor = CategoryColor.ORANGE,
    onDismissed = {},
    onCategoryNameInputFieldValueChanged = {},
    onCategoryColorClicked = {},
    onDoneButtonClicked = {}
)

@Preview
@Composable
fun PreviewCategoryModificationBar() {
    CategoryModificationBar(prop = previewCategoryModificationBarProp)
}