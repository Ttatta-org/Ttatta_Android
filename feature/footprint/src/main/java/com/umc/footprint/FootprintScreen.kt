package com.umc.footprint

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FootprintScreen(
    mapView: @Composable () -> Unit,
    categorySelectionCard: @Composable () -> Unit,
    onCategoryButtonClicked: () -> Unit,
    onLocationButtonClicked: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 지도
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            mapView()
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 플로팅 버튼
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 카테고리 선택
                    IconButton(
                        onClick = onCategoryButtonClicked,
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(48.dp),
                        ) {
                            Text(text = "test")
                        }
                    }
                    // 내 위치로 이동
                    IconButton(
                        onClick = onLocationButtonClicked,
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(48.dp),
                        ) {
                            Text(text = "test")
                        }
                    }
                }
            }
            // 카테고리 카드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ) {
                categorySelectionCard()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFootprintScreen() {
    FootprintScreen(
        mapView = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "This is map.")
            }
        },
        categorySelectionCard = {
            PreviewCategorySelectionCard()
        },
        onCategoryButtonClicked = {},
        onLocationButtonClicked = {},
    )
}