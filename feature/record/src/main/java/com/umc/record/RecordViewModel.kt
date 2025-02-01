package com.umc.record

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RecordViewModel: ViewModel() {
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
}