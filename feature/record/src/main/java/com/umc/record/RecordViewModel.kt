package com.umc.record

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.design.CategoryColor
import com.umc.record.core.MapHandler
import com.umc.record.core.MapMarker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClickedMarkerInfo(
    val x: Float,
    val y: Float,
    val clusterId: Long,
)

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val mapHandler: MapHandler
): ViewModel() {

    private val markers = mutableMapOf<Long, MutableList<MapMarker>>()

    private val clickedMarkerPositionState = mutableStateOf<Pair<Float, Float>?>(null)
    private val clickedMarkerInfoState = mutableStateOf<ClickedMarkerInfo?>(null)

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
        return {
            println("🗺️ Rendering mapView...") // ✅ 지도 렌더링 확인 로그 추가
            try {
                mapHandler.getMapView() // ✅ mapView 실행
            } catch (e: Exception) {
                Log.d("ViewModel", "🚨 Error rendering mapView: ${e.localizedMessage}") // ✅ 예외 발생 로그 확인
            }
        }
    }

    fun moveMapToCurrentPosition() {
        viewModelScope.launch { mapHandler.moveToCurrentPosition() }
    }

    private fun markMap(
        latitude: Double,
        longitude: Double,
        diaryId: Long,
        clusterId: Long,
        categoryId: Long,
        color: CategoryColor?,
    ) {
        val marker = MapMarker(
            latitude = latitude,
            longitude = longitude,
            zIndex = diaryId.toInt(),
            color = color,
            onClicked = onClicked@{ x, y ->
                clickedMarkerInfoState.value = ClickedMarkerInfo(
                    x = x,
                    y = y,
                    clusterId = clusterId,
                )
                return@onClicked {
                    clickedMarkerInfoState.value = null
                    //diaryStateMap.clear()
                }
            }
        )
        markers[categoryId]?.add(marker) ?: run { markers[categoryId] = mutableListOf(marker) }
        viewModelScope.launch { mapHandler.addMarker(marker) }
    }
}