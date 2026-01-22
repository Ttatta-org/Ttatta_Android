package com.umc.record.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.component.CustomHeader
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme
import com.umc.design.theme.ThemeProvider
import com.umc.record.R
import com.umc.record.component.LocationSearchResultViewer
import com.umc.record.component.LocationSearchResultViewerSearchResult

@Composable
fun EditLocationSearchResultScreen(
    searchValue: String,
    searchResults: List<LocationSearchResultViewerSearchResult>?,
    onBackButtonClicked: () -> Unit,
    onSearchButtonClicked: () -> Unit,
    onSearchValueChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        CustomHeader(
            showLogo = false,
            backgroundColor = Color.White,
            waveColor = Color.Transparent,
            onBackButtonClicked = onBackButtonClicked,
            headerTrailing = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BasicTextField(
                        value = searchValue,
                        onValueChange = onSearchValueChanged,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { onSearchButtonClicked() },
                        ),
                        textStyle = TextStyle(
                            fontFamily = LocalFontTheme.current.font,
                            fontSize = 13.sp,
                            lineHeight = 13.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.W400,
                        ),
                        modifier = Modifier.weight(1f),
                    ) { inner ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = LocalColorTheme.current.primary[500],
                                    shape = RoundedCornerShape(percent = 50)
                                )
                                .background(
                                    color = LocalColorTheme.current.secondary[100],
                                    shape = RoundedCornerShape(percent = 50)
                                ),
                        ) {
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 15.dp)
                                    .weight(1f),
                            ) {
                                Text(
                                    text = stringResource(id = R.string.search_placeholder),
                                    fontSize = 13.sp,
                                    lineHeight = 13.sp,
                                    fontWeight = FontWeight.W400,
                                    color = LocalColorTheme.current.grey[600],
                                    modifier = Modifier
                                        .padding(top = 10.dp, bottom = 9.dp)
                                        .alpha(if (searchValue.isEmpty()) 1f else 0f)
                                )
                                inner.invoke()
                            }
                            if (searchValue.isNotEmpty()) Image(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Clear",
                                modifier = Modifier
                                    .padding(end = 11.dp)
                                    .size(16.dp)
                                    .clickable(
                                        interactionSource = null,
                                        indication = null,
                                        onClick = { onSearchValueChanged("") },
                                    ),
                            )
                        }
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search",
                        tint = LocalColorTheme.current.primary[500],
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = onSearchButtonClicked
                            ),
                    )
                }
            },
        )
        LocationSearchResultViewer(
            matchValue = searchValue,
            isBottomPaddingNeeded = true,
            searchResults = searchResults,
        )
    }
}

@Preview
@Composable
private fun PreviewEditLocationSearchResultScreenLoading() {
    ThemeProvider {
        EditLocationSearchResultScreen(
            searchValue = "",
            searchResults = null,
            onSearchButtonClicked = {},
            onBackButtonClicked = {},
            onSearchValueChanged = {},
        )
    }
}

@Preview
@Composable
private fun PreviewEditLocationSearchResultScreenFullOfResult() {
    ThemeProvider {
        EditLocationSearchResultScreen(
            searchValue = "고래와",
            searchResults = listOf(
                LocationSearchResultViewerSearchResult(
                    name = "고래와",
                    address = "서울 용산구 한강대로62길 45-17 지하 1층",
                    onClick = {},
                ),
                LocationSearchResultViewerSearchResult(
                    name = "고래와참치",
                    address = "서울 강남구 테헤란로51길 7 지하 1층",
                    onClick = {},
                ),
                LocationSearchResultViewerSearchResult(
                    name = "고래왓",
                    address = "제주 서귀포시 표선면 표선리",
                    onClick = {},
                ),
            ).let {
                it + it + it + it + it + it + it
            },
            onSearchButtonClicked = {},
            onBackButtonClicked = {},
            onSearchValueChanged = {},
        )
    }
}

@Preview
@Composable
private fun PreviewEditLocationSearchResultScreenNoResult() {
    ThemeProvider {
        EditLocationSearchResultScreen(
            searchValue = "고래와",
            searchResults = emptyList(),
            onSearchButtonClicked = {},
            onBackButtonClicked = {},
            onSearchValueChanged = {},
        )
    }
}