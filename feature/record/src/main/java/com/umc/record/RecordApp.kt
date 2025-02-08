package com.umc.record

import androidx.compose.runtime.Composable

@Composable
fun RecordApp(
    categories: List<Pair<String, String>>,
    selectedCategory: String,
    diaryText: String,
    onCategorySelect: (String) -> Unit,
    onDiaryTextUpdate: (String) -> Unit,
    mapView: @Composable () -> Unit,
    onLocationButtonClicked: () -> Unit
) {
    RecordScreen(
        categories = categories,
        selectedCategory = selectedCategory,
        diaryText = diaryText,
        onCategorySelect = onCategorySelect,
        onDiaryTextUpdate = onDiaryTextUpdate
    )

    RecordEditLocationScreen(
        mapView = mapView,
        onLocationButtonClicked = onLocationButtonClicked
    )
}
