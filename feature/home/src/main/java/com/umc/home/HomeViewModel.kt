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

    init {
        loadDiaries()
    }

    private fun loadDiaries() {
        viewModelScope.launch {
            // 예제 데이터 (LocalDate 사용)
            val dummyDiaries = listOf(
                Diary(LocalDate.of(2025, 1, 25), R.drawable.pudding, "귀여운 깜찍 토끼 초코푸딩!"),
                Diary(LocalDate.of(2025, 1, 22), R.drawable.cafe, "오늘의 다짐: 더 나은 내가 되자!")
            )
            _uiState.value = HomeUiState(diaries = dummyDiaries)
        }
    }

    fun addDiary(date: LocalDate, content: String, imageUrl: Int) {
        viewModelScope.launch {
            val updatedDiaries = _uiState.value.diaries + Diary(date, imageUrl, content)
            _uiState.value = _uiState.value.copy(diaries = updatedDiaries)
        }
    }
}