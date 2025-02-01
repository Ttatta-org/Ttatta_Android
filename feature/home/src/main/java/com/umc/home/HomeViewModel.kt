package com.umc.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class HomeUiState(
    val diaries: List<Diary> = emptyList()
)

data class Diary(
    val date: LocalDateTime,
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    init {
        loadDiaries()
    }

    private fun loadDiaries() {
        viewModelScope.launch {
            val dummyDiaries = listOf(
                Diary(LocalDateTime.of(2025, 1, 25, 14, 30), R.drawable.pudding, "귀여운 깜찍 토끼 초코푸딩!"),
                Diary(LocalDateTime.of(2025, 1, 22, 18, 45), R.drawable.letter, "항상 건강하고 행복하게!"),
                Diary(LocalDateTime.of(2025, 1, 22, 9, 15), R.drawable.cafe, "오늘의 다짐: 더 나은 내가 되자!"),
                Diary(LocalDateTime.of(2025, 1, 21, 11, 0), R.drawable.cafe, "토끼 모양 케이크가 정말 귀엽다.")
            )
            _uiState.value = HomeUiState(diaries = dummyDiaries)
        }
    }

    // 다이어리 최신순 정렬
    fun getSortedDiaries(): List<Diary> {
        return _uiState.value.diaries.sortedByDescending { it.date } // 최신순 정렬
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
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
            addRecentSearch(query)
            _searchQuery.value = ""
        }
    }

    private fun addRecentSearch(query: String) {
        if (query.isNotEmpty() && !_recentSearches.value.contains(query)) { // ✅ 중복 검색어 방지
            val updatedList = _recentSearches.value.toMutableList()
            updatedList.add(0, query)
            if (updatedList.size > 3) updatedList.removeAt(updatedList.size - 1)
            _recentSearches.value = updatedList
        }
    }

}