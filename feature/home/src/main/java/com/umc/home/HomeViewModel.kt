package com.umc.home

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.model.Diary
import com.umc.core.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

data class EditDiary(
    val id: Int, // 다이어리 ID 추가
    val date: LocalDateTime,
    val imageUrl: String?, // 이미지 URI를 String으로 저장
    val content: String
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    // ✅ 전체 일기 목록을 저장하는 StateFlow 추가
    private val _fullDiaryListState = MutableStateFlow<List<Diary>>(emptyList())
    val fullDiaryListState: StateFlow<List<Diary>> = _fullDiaryListState

    // 기본 일기 목록 상태
    private val _diaryListState = MutableStateFlow<List<Diary>>(emptyList())
    val diaryListState: StateFlow<List<Diary>> = _diaryListState

    private var currentPage = 1 // ✅ 현재 페이지 상태
    private var isLoading = false // ✅ 중복 요청 방지

    // ✅ **검색 결과 저장**
    private val _searchResultsState = MutableStateFlow<List<Diary>>(emptyList())
    val searchResultsState: StateFlow<List<Diary>> = _searchResultsState

    // ✅ **최근 검색어 저장 (최대 5개)**
    private val _recentSearchesState = MutableStateFlow<List<String>>(emptyList())
    val recentSearchesState: StateFlow<List<String>> = _recentSearchesState

    // ✅ **현재 검색어 상태**
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // 초기 데이터 로딩 (페이지 1, 날짜 필터 없음)
    init {
        loadAllDiaries()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * 🟢 **전체 일기 데이터 로드 (캘린더에 표시될 모든 일기들)**
     */
    fun loadAllDiaries() {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "📌 전체 다이어리 목록 불러오기 (날짜 필터 없음)")
                val allDiaries = diaryRepository.getDiaries(page = 1, date = null) // ✅ 날짜 필터 없이 전체 가져오기
                _fullDiaryListState.value = allDiaries
                Log.d("HomeViewModel", "✅ 전체 다이어리 저장 완료: ${allDiaries.size}개")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ 전체 다이어리 불러오기 실패: ${e.message}")
            }
        }
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
        if (isLoading) return // ✅ 중복 요청 방지
        isLoading = true

        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "📌 loadDiaries() 실행됨 - 페이지: $page, 날짜: $date")

                val newDiaries = diaryRepository.getDiaries(page = page, date = null)

                if (newDiaries.isNotEmpty()) {
                    // ✅ 기존 리스트에 새 데이터 추가
                    _diaryListState.value += newDiaries
                    currentPage++ // ✅ 다음 페이지 설정
                }

                Log.d("HomeViewModel", "✅ 다이어리 데이터 로드 성공: ${newDiaries.size}개")
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    /**
     * 🔍 **검색어에 따라 일기 목록 불러오기**
     */
    fun searchDiaries(searchWord: String) {
        viewModelScope.launch {
            try {
                _searchQuery.value = searchWord
                val results = diaryRepository.getDiaries(page = 1, searchWord = searchWord)

                _searchResultsState.value = results  // ✅ 검색 결과 업데이트
                _diaryListState.value = results      // ✅ UI에 반영될 리스트도 업데이트

                // ✅ 최근 검색어 업데이트 (중복 제거 & 최신순 정렬)
                if (searchWord.isNotBlank()) {
                    val updatedList = _recentSearchesState.value.toMutableList()

                    // 중복된 검색어 제거 후 맨 앞에 추가
                    updatedList.remove(searchWord)
                    updatedList.add(0, searchWord)

                    // 최대 3개까지만 유지
                    if (updatedList.size > 3) {
                        updatedList.removeAt(updatedList.size - 1)
                    }

                    _recentSearchesState.value = updatedList
                }

                Log.d("HomeViewModel", "✅ 검색 결과: ${results.size}개")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ 검색 실패: ${e.message}")
                _searchResultsState.value = emptyList()
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
                // ✅ 아무것도 변경되지 않은 경우, API 요청을 막음
//                if (content == null && image == null && categoryId == null) {
//                    onFailed(IllegalArgumentException("수정할 내용이 없습니다."))
//                    return@launch
//                }

                Log.d("modifyDiary", "📤 수정 요청 시작 (diaryId: $diaryId)")
                // ✅ 다이어리 수정 API 호출
                diaryRepository.modifyDiary(
                    diaryId = diaryId,
                    categoryId = categoryId,
                    content = content,
                    image = image
                )

                // ✅ 수정된 다이어리를 다시 불러오기
                //val updatedDiary = diaryRepository.getDiaryById(diaryId)

                //Log.d("modifyDiary", "✅ 서버에서 수정된 다이어리 가져오기 완료: $updatedDiary")

                // ✅ 기존 리스트에서 해당 다이어리 찾기
                _diaryListState.value = _diaryListState.value.map { diary ->
                    if (diary.id == diaryId) {
                        diary.copy(
                            content = content ?: diary.content, // 변경된 내용 반영
                            imageUrl = image?.path ?: diary.imageUrl // 변경된 이미지 반영
                        )
                    } else diary
                }

                _fullDiaryListState.value = _fullDiaryListState.value.map { diary ->
                    if (diary.id == diaryId) {
                        diary.copy(
                            content = content ?: diary.content,
                            imageUrl = image?.path ?: diary.imageUrl
                        )
                    } else diary
                }

                // ✅ 수정 성공 후 다이어리 목록 다시 불러오기
                loadDiaries(page = 1, date = null, onSucceed = onSucceed, onFailed = { throw it })

                onSucceed()
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
                Log.d("HomeViewModel", "✅ 일기 삭제 성공: $diaryId")

                // ✅ 삭제된 항목을 리스트에서 즉시 제거
                _diaryListState.value = _diaryListState.value.filterNot { it.id == diaryId }
                _fullDiaryListState.value = _fullDiaryListState.value.filterNot { it.id == diaryId }

                loadAllDiaries()

                onSucceed()
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