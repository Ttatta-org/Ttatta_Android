package com.umc.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HomeUiState(
    val diaries: List<Diary> = emptyList()
)

data class Diary(
    val date: LocalDate,
    // 나중에 string으로 변경 필요
    val imageUrl: Int,
    val content: String
)

open class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    open val uiState: StateFlow<HomeUiState> = _uiState

    // 검색 결과 상태 관리
    private val _searchResults = MutableStateFlow<List<Diary>>(emptyList())
    val searchResults: StateFlow<List<Diary>> = _searchResults

    init {
        loadDiaries()
    }

    private fun loadDiaries() {
        viewModelScope.launch {
            val dummyDiaries = listOf(
                Diary(LocalDate.of(2025, 1, 25), R.drawable.pudding, "귀여운 깜찍 토끼 초코푸딩!"),
                Diary(LocalDate.of(2025, 1, 22), R.drawable.cafe, "오늘의 다짐: 더 나은 내가 되자!"),
                Diary(LocalDate.of(2025, 1, 21), R.drawable.cafe, "토끼 모양 케이크가 정말 귀엽다.")
            )
            _uiState.value = HomeUiState(diaries = dummyDiaries)
        }
    }

    fun searchDiaries(query: String) {
        viewModelScope.launch {
            // 검색어를 공백 기준으로 분리
            val queryKeywords = query.trim().split("\\s+".toRegex())

            val filteredDiaries = _uiState.value.diaries.filter { diary ->
                // 각 다이어리 콘텐츠를 공백 제거한 뒤, 검색어 키워드들이 포함되어 있는지 확인
                val normalizedContent = diary.content.replace("\\s+".toRegex(), "")
                queryKeywords.all { normalizedContent.contains(it, ignoreCase = true) }
            }.sortedByDescending { it.date } // 최신순 정렬

            _searchResults.value = filteredDiaries
        }
    }

}