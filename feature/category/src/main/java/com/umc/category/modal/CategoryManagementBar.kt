package com.umc.category.modal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.category.R
import com.umc.category.model.CategoryManagementBarProp
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.design.R as Res

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementBar(
    prop: CategoryManagementBarProp
) {
    val bottom = WindowInsets.systemBars
        .asPaddingValues()
        .calculateBottomPadding()

    ModalBottomSheet(
        onDismissRequest = prop.onDismissed,
        shape = RectangleShape,
        sheetMaxWidth = 1024.dp,
        containerColor = Color.Transparent,
        scrimColor = Color.Transparent,
        dragHandle = {},
        contentWindowInsets = { WindowInsets(bottom = 0.dp) },
    ) {
        Column {
            // 그림자를 위한 여백
            Spacer(modifier = Modifier.height(16.dp))
            // 본문
            Column(
                modifier = Modifier
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = Res.drawable.ic_header_deco),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    listOf(
                        prop.onModifyOptionClicked to stringResource(id = R.string.modify),
                        prop.onDeleteCategoryOptionClicked to stringResource(id = R.string.delete_category),
                        prop.onDeleteCategoryAndAllIncludedDiariesOptionClicked to stringResource(id = R.string.delete_all),
                    ).forEach { (onClick, text) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(percent = 50))
                                .clickable { onClick() }
                        ) {
                            Text(
                                text = text,
                                fontSize = 15.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.W600,
                                color = LocalColorTheme.current.grey[700],
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(bottom))
            }
        }
    }
}

val previewCategoryManagementBarProp = CategoryManagementBarProp(
    onDismissed = {},
    onModifyOptionClicked = {},
    onDeleteCategoryOptionClicked = {},
    onDeleteCategoryAndAllIncludedDiariesOptionClicked = {}
)

@Preview
@Composable
fun PreviewCategoryManagementBar() {
    ThemeProvider {
        CategoryManagementBar(prop = previewCategoryManagementBarProp)
    }
}