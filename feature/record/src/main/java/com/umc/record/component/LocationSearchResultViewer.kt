package com.umc.record.component

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.theme.LocalColorTheme
import com.umc.record.R

data class LocationSearchResultViewerSearchResult(
    val name: String,
    val address: String,
    val onClick: () -> Unit,
)

@Composable
fun LocationSearchResultViewer(
    matchValue: String,
    searchResults: List<LocationSearchResultViewerSearchResult>?,
) {
    Column {
        if (searchResults == null) Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp)
                .weight(1f)
        ) {
            CircularProgressIndicator(color = LocalColorTheme.current.primary[500])
        } else if (searchResults.isNotEmpty()) LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(searchResults) { searchResult ->
                Box(
                    modifier = Modifier.clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = { searchResult.onClick() })
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(13.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 8.dp),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pin),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(24.dp)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    searchResult.name.forEach {
                                        withStyle(
                                            style = SpanStyle(
                                                color = if (matchValue.contains(it)) LocalColorTheme.current.primary[600] else LocalColorTheme.current.grey[700],
                                                fontWeight = FontWeight.W700,
                                                fontSize = 14.sp,
                                            )
                                        ) {
                                            append(it)
                                        }
                                    }
                                },
                                fontSize = 13.sp,
                                lineHeight = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF8E8E8E),
                                maxLines = 1
                            )
                            Text(
                                text = searchResult.address,
                                fontSize = 13.sp,
                                lineHeight = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF8E8E8E),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
            item {
                Spacer(
                    modifier = Modifier.height(
                        WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding() + 8.dp,
                    )
                )
            }
        } else Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_info),
                contentDescription = null,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = "찾으시는 검색어의 결과가 없어요!",
                fontSize = 12.sp,
                color = Color(0xFFFF6060),
                fontWeight = FontWeight.Normal
            )
        }
    }
}