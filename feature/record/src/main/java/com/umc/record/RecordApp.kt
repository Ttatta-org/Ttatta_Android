package com.umc.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RecordApp(
    viewModel: RecordViewModel,
    onNavigateToCategoryApp: () -> Unit
) {
    var isEditingLocation by rememberSaveable { mutableStateOf(false) }

    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val diaryText by viewModel.diaryText.collectAsStateWithLifecycle()

    println("🟢 isEditingLocation: $isEditingLocation") // 상태 로그 추가

    if (isEditingLocation) {
        RecordEditLocationScreen(
            mapView = viewModel.getMapView(),
            onLocationButtonClicked = {
                println("🔴 Returning to RecordScreen") // 상태 변경 확인
                isEditingLocation = false
            }
        )
    } else {
        RecordScreen(
            categories = categories,
            selectedCategory = selectedCategory,
            diaryText = diaryText,
            onCategorySelect = { category -> viewModel.selectCategory(category) },
            onDiaryTextUpdate = { text -> viewModel.updateDiaryText(text) },
            onEditLocation = {
                println("🟠 Navigating to RecordEditLocationScreen") // 로그 추가
                isEditingLocation = true
            }
        )
    }
}