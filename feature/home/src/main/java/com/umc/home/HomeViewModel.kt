package com.umc.home

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.model.Diary
import com.umc.core.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    // 기본 일기 목록 상태
    private val diaryListState = mutableStateOf<List<Diary>>(emptyList())
    val diaryList get() = diaryListState.value

    // 검색 결과 상태
    private val searchResultsState = mutableStateOf<List<Diary>>(emptyList())
    val searchResults get() = searchResultsState.value

    // 현재 검색어 상태
    var searchQuery = mutableStateOf("")
        private set

    // 초기 데이터 로딩 (페이지 1, 날짜 필터 없음)
    init {
        loadDiaries(page = 1, date = null)
    }

    /**
     * 서버에서 일기 목록을 가져와 diaryListState를 업데이트합니다.
     * 날짜 필터가 있을 경우 해당 날짜에 해당하는 일기를 불러옵니다.
     */
    fun loadDiaries(
        page: Int,
        date: LocalDate?,
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "📌 loadDiaries() 실행됨 - 페이지: $page, 날짜: $date")
                // Repository의 getDiaries(page, date) 호출
                val diaries = diaryRepository.getDiaries(page, date)
                diaryListState.value = diaries
                Log.d("HomeViewModel", "✅ 다이어리 데이터 로드 성공: ${diaries.size}개")
                onSucceed()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ 다이어리 데이터 로드 실패", e)
                onFailed(e)
            }
        }
    }

    /**
     * 검색어에 따라 일기 목록을 불러옵니다.
     * 검색어는 repository의 getDiaries(page, searchWord)를 호출합니다.
     */
    fun searchDiaries(
        searchWord: String,
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                // 검색어를 업데이트하고 검색 결과를 받아옴
                searchQuery.value = searchWord
                val results = diaryRepository.getDiaries(page = 1, searchWord = searchWord)
                searchResultsState.value = results
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    /**
     * 일기 생성 API를 호출한 후, 목록을 갱신합니다.
     */
    fun createDiary(
        categoryId: Long,
        date: LocalDateTime,
        content: String,
        image: File,
        latitude: Double,
        longitude: Double,
        locationName: String,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.createDiary(
                    categoryId = categoryId,
                    date = date,
                    content = content,
                    image = image,
                    latitude = latitude,
                    longitude = longitude,
                    locationName = locationName
                )
                // 생성 후 최신 목록을 다시 로딩 (예: 페이지 1, 날짜 필터 없음)
                loadDiaries(page = 1, date = null, onSucceed = onSucceed, onFailed = { throw it })
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    /**
     * 일기 수정 API를 호출한 후, 목록을 갱신합니다.
     */
    fun modifyDiary(
        diaryId: Long,
        categoryId: Long?,
        content: String?,
        image: File?,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.modifyDiary(
                    diaryId = diaryId,
                    categoryId = categoryId,
                    content = content,
                    image = image
                )
                loadDiaries(page = 1, date = null, onSucceed = onSucceed, onFailed = { throw it })
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    /**
     * 일기 삭제 API를 호출한 후, 목록을 갱신합니다.
     */
    fun deleteDiary(
        diaryId: Long,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.deleteDiary(diaryId = diaryId)
                loadDiaries(page = 1, date = null, onSucceed = onSucceed, onFailed = { throw it })
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }
}

//data class HomeUiState(
//    val diaries: List<Diary> = emptyList()
//)
//
//data class Diary(
//    val date: LocalDateTime,
//    // 나중에 string으로 변경 필요
//    val imageUrl: Int,
//    val content: String
//)
//
//open class HomeViewModel : ViewModel() {
//    private val _uiState = MutableStateFlow(HomeUiState())
//    open val uiState: StateFlow<HomeUiState> = _uiState
//
//    // 검색 결과 상태 관리
//    private val _searchResults = MutableStateFlow<List<Diary>>(emptyList())
//    val searchResults: StateFlow<List<Diary>> = _searchResults
//
//    private val _searchQuery = MutableStateFlow("")
//    val searchQuery: StateFlow<String> = _searchQuery
//
//    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
//    val recentSearches: StateFlow<List<String>> = _recentSearches
//
//    init {
//        loadDiaries()
//    }
//
//    private fun loadDiaries() {
//        viewModelScope.launch {
//            val dummyDiaries = listOf(
//                Diary(LocalDateTime.of(2025, 1, 25, 14, 30), R.drawable.pudding, "귀여운 깜찍 토끼 초코푸딩!"),
//                Diary(LocalDateTime.of(2025, 1, 22, 18, 45), R.drawable.letter, "항상 건강하고 행복하게!"),
//                Diary(LocalDateTime.of(2025, 1, 22, 9, 15), R.drawable.cafe, "오늘의 다짐: 더 나은 내가 되자!"),
//                Diary(LocalDateTime.of(2025, 1, 21, 11, 0), R.drawable.cafe, "토끼 모양 케이크가 정말 귀엽다.")
//            )
//            _uiState.value = HomeUiState(diaries = dummyDiaries)
//        }
//    }
//
//    // 다이어리 최신순 정렬
//    fun getSortedDiaries(): List<Diary> {
//        return _uiState.value.diaries.sortedByDescending { it.date } // 최신순 정렬
//    }
//
//    fun updateSearchQuery(query: String) {
//        _searchQuery.value = query
//    }
//
//    fun searchDiaries(query: String) {
//        viewModelScope.launch {
//            // 검색어를 공백 기준으로 분리
//            val queryKeywords = query.trim().split("\\s+".toRegex())
//
//            val filteredDiaries = _uiState.value.diaries.filter { diary ->
//                // 각 다이어리 콘텐츠를 공백 제거한 뒤, 검색어 키워드들이 포함되어 있는지 확인
//                val normalizedContent = diary.content.replace("\\s+".toRegex(), "")
//                queryKeywords.all { normalizedContent.contains(it, ignoreCase = true) }
//            }.sortedByDescending { it.date } // 최신순 정렬
//
//            _searchResults.value = filteredDiaries
//            addRecentSearch(query)
//            _searchQuery.value = ""
//        }
//    }
//
//    private fun addRecentSearch(query: String) {
//        if (query.isNotEmpty() && !_recentSearches.value.contains(query)) { // ✅ 중복 검색어 방지
//            val updatedList = _recentSearches.value.toMutableList()
//            updatedList.add(0, query)
//            if (updatedList.size > 3) updatedList.removeAt(updatedList.size - 1)
//            _recentSearches.value = updatedList
//        }
//    }
//
//}