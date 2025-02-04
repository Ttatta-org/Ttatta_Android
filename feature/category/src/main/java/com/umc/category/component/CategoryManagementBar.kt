package com.umc.category.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.category.R
import com.umc.design.R as Res

data class CategoryManagementBarProp(
    val onDismissed: () -> Unit,
    val onModifyOptionClicked: () -> Unit,
    val onDeleteCategoryOptionClicked: () -> Unit,
    val onDeleteCategoryAndAllIncludedDiariesOptionClicked: () -> Unit,
)

private val categoryManagementBarShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementBar(
    prop: CategoryManagementBarProp
) {
    ModalBottomSheet(
        onDismissRequest = prop.onDismissed,
        shape = categoryManagementBarShape,
        containerColor = Color.White,
        scrimColor = Color.Transparent,
        dragHandle = {
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
        },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable { prop.onModifyOptionClicked() }
            ) {
                Text(
                    text = stringResource(id = R.string.modify),
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable { prop.onDeleteCategoryOptionClicked() }
            ) {
                Text(
                    text = stringResource(id = R.string.delete),
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable { prop.onDeleteCategoryAndAllIncludedDiariesOptionClicked() }
            ) {
                Text(
                    text = stringResource(id = R.string.delete_all),
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )
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
    CategoryManagementBar(prop = previewCategoryManagementBarProp)
}