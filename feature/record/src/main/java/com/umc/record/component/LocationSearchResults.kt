package com.umc.record.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.core.model.LocationSearchResult
import com.umc.design.theme.LocalColorTheme
import com.umc.record.R

@Composable
fun LocationSearchResults(
    results: List<LocationSearchResult>,
    keyword: String,
    onSelect: (LocationSearchResult) -> Unit,
    onClickMore: () -> Unit = {},
    showAll: Boolean,
    scrollEnabled: Boolean
) {
    val visible = if (showAll) results else results.take(3)

    if (results.isEmpty()) {
        // 빈 결과 메시지
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 150.dp, start = 103.dp, end = 102.dp, bottom = 15.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = null,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = "찾으시는 검색어의 결과가 없어요!",
                    fontSize = 12.sp,
                    color = Color(0xFFFF6060)
                )
            }
        }
        return
    }

    // 결과 리스트
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        userScrollEnabled = scrollEnabled, // 스크롤 막기
        modifier = Modifier.fillMaxWidth()
    ) {
        items(visible) { item ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(13.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(item) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pin),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = 3.31.dp)
                        .size(24.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    HighlightedText(
                        fullText = item.title,
                        keyword = keyword
                    )
                    Text(
                        text = item.address.ifBlank { item.description },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF8E8E8E),
                        maxLines = 1
                    )
                }
            }
        }
        // 더보기 버튼 (결과가 4개 이상일 때만)
        if (!showAll && results.size > 3) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "더보기",
                        fontSize = 13.sp,
                        color = Color(0xFFFF8072),
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .clickable { onClickMore() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HighlightedText(
    fullText: String,
    keyword: String,
    normalColor: Color = Color.Black,
    highlightColor: Color = LocalColorTheme.current.primary[600],
    fontSize: Int = 14
) {
    val annotated = buildAnnotatedString {
        if (keyword.isBlank()) {
            append(fullText)
            return@buildAnnotatedString
        }

        var startIndex = 0
        val lowerFull = fullText.lowercase()
        val lowerKeyword = keyword.lowercase()

        while (true) {
            val index = lowerFull.indexOf(lowerKeyword, startIndex)
            if (index == -1) {
                // 나머지 텍스트는 일반 스타일
                withStyle(SpanStyle(color = normalColor, fontWeight = FontWeight.Bold)) {
                    append(fullText.substring(startIndex))
                }
                break
            }
            // 키워드 앞 부분
            if (index > startIndex) {
                withStyle(SpanStyle(color = normalColor, fontWeight = FontWeight.Bold)) {
                    append(fullText.substring(startIndex, index))
                }
            }
            // 키워드 부분
            withStyle(SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)) {
                append(fullText.substring(index, index + keyword.length))
            }
            startIndex = index + keyword.length
        }
    }

    Text(
        text = annotated,
        fontSize = fontSize.sp,
        maxLines = 1
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLocationSearchResults_List() {
    val sample = listOf(
        LocationSearchResult(
            title = "고래와",
            description = "서울 용산구 한강대로62길 45-17 지하 1층",
            category = "음식점",
            address = "서울 용산구 한강대로62길 45-17 지하 1층",
            latitude = 37.529, longitude = 126.964
        ),
        LocationSearchResult(
            title = "고래와 치보 강남점",
            description = "서울 강남구 테헤란로51길 7 지하 1층",
            category = "음식점",
            address = "서울 강남구 테헤란로51길 7 지하 1층",
            latitude = 37.504, longitude = 127.043
        )
    )

    LocationSearchResults(
        results = sample,
        keyword = "고려대",
        onSelect = { /* no-op for preview */ },
        showAll = false,
        scrollEnabled = false
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLocationSearchResults_Empty() {
    LocationSearchResults(
        results = emptyList(),
        keyword = "아메리카또또",
        onSelect = { /* no-op for preview */ },
        showAll = false,
        scrollEnabled = false
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLocationSearchResults_Goraewa() {
    val sample = listOf(
        LocationSearchResult(
            title = "고래와",
            description = "서울 용산구 한강대로62길 45-17 지하 1층",
            category = "음식점",
            address = "서울 용산구 한강대로62길 45-17 지하 1층",
            latitude = 37.529, longitude = 126.964
        ),
        LocationSearchResult(
            title = "고래와 치보 강남점",
            description = "서울 강남구 테헤란로51길 7 지하 1층",
            category = "음식점",
            address = "서울 강남구 테헤란로51길 7 지하 1층",
            latitude = 37.504, longitude = 127.043
        ),
        LocationSearchResult(
            title = "고래와 참치",
            description = "대구 수성구 범어천로 17",
            category = "음식점",
            address = "대구 수성구 범어천로 17",
            latitude = 35.857, longitude = 128.623
        ),
        LocationSearchResult(
            title = "고래와",
            description = "제주 서귀포시 표선면 표선리",
            category = "음식점",
            address = "제주 서귀포시 표선면 표선리",
            latitude = 33.325, longitude = 126.835
        )
    )

    LocationSearchResults(
        results = sample,
        keyword = "고래와",
        onSelect = { /* no-op for preview */ },
        showAll = true,
        scrollEnabled = true
    )
}