package com.umc.record.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.record.R
import com.umc.record.component.LocationBottomSheet
import com.umc.record.component.LocationBottomSheetProp
import com.umc.record.component.ShadowedIcon
import com.umc.record.component.TopBar
import com.umc.record.component.TopBarProp
import com.umc.record.component.previewLocationBottomSheetProp

data class EditLocationScreenTopBarProp(
    val searchWord: String,
    val onSearchWordChanged: (String) -> Unit,
    val onSearchButtonClicked: () -> Unit,
)

@Composable
fun EditLocationScreen(
    mapView: @Composable () -> Unit,
    topBarProp: EditLocationScreenTopBarProp,
    bottomSheetProp: LocationBottomSheetProp,
    onLocationButtonClicked: () -> Unit,
) {
    var topBarHeight by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 지도
        mapView()
        Column {
            // 플로팅 버튼
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(17.dp),
            ) {
                IconButton(
                    onClick = onLocationButtonClicked,
                    modifier = Modifier.size(60.dp)
                ) {
                    ShadowedIcon(
                        id = R.drawable.btn_location,
                        contentDescription = null,
                        width = 59.04.dp,
                        height = 55.dp,
                    )
                }
            }
            // 바텀 시트
            LocationBottomSheet(prop = bottomSheetProp)
        }
        // 탑 바
        TopBar(
            prop = TopBarProp(
                searchWord = topBarProp.searchWord,
                onSearchWordChanged = topBarProp.onSearchWordChanged,
                onSearchButtonClicked = topBarProp.onSearchButtonClicked,
                onHeightChanged = { topBarHeight = it }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecordEditLocationScreen() {
    EditLocationScreen(
        mapView = {},
        topBarProp = EditLocationScreenTopBarProp(
            searchWord = "",
            onSearchWordChanged = {},
            onSearchButtonClicked = {},
        ),
        bottomSheetProp = previewLocationBottomSheetProp,
        onLocationButtonClicked = {},
    )
}