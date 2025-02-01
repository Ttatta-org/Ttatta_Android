package com.umc.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.design.CategoryColor
import com.umc.record.core.MapHandler
import com.umc.record.core.MapMarker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecordViewModel @Inject constructor(
    private val mapHandler: MapHandler
): ViewModel() {
    private val clickedMarkerPositionState = mutableStateOf<Pair<Float, Float>?>(null)
    val clickedMarkerPosition get() = clickedMarkerPositionState.value

    // 카테고리 목록 (더미 데이터)
    private val _categories = MutableStateFlow(
        listOf(
            "친구들" to "Red",
            "가족" to "Blue",
            "남자친구" to "Pink",
            "일상" to "Yellow",
            "다시 오고 싶은 장소" to "Green",
            "제주여행" to "Turquoise"
        )
    )
    val categories: StateFlow<List<Pair<String, String>>> = _categories

    // 선택된 카테고리
    private val _selectedCategory = MutableStateFlow("default")
    val selectedCategory: StateFlow<String> = _selectedCategory

    // ✅ 사용자가 입력한 다이어리 텍스트 저장
    private val _diaryText = MutableStateFlow("")
    val diaryText: StateFlow<String> = _diaryText

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    // ✅ 사용자가 입력한 다이어리 텍스트 업데이트
    fun updateDiaryText(newText: String) {
        _diaryText.value = newText
    }

    // 지도
    fun getMapView(): @Composable () -> Unit {
        return mapHandler.getMapView()
    }

    fun moveMapToCurrentPosition() {
        viewModelScope.launch { mapHandler.moveToCurrentPosition() }
    }

    fun markPositionOnMap(
        latitude: Double,
        longitude: Double,
        category: CategoryColor? = null
    ) {
        val marker = MapMarker(
            latitude = latitude,
            longitude = longitude,
            color = category,
            onClicked = onClicked@{ x, y ->
                clickedMarkerPositionState.value = x to y
                return@onClicked { clickedMarkerPositionState.value = null }
            }
        )
        viewModelScope.launch { mapHandler.addMarker(marker) }
    }
}