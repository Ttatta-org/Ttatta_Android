package com.umc.home

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.format.DateTimeFormatter
import com.umc.core.model.CategoryInfo
import com.umc.core.model.Diary
import com.umc.core.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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

data class AiSummaryState(
    val summaryText: String = "",
    val timestamp: String = "", // "2025.11.06 21:00 생성" 같은 포맷
    val isLoading: Boolean = false,
    val error: String? = null,
    val isVisible: Boolean = false
)

/**
 * 화면의 주요 모드를 정의합니다. (데이터 리스트의 상태)
 * - Home: 기본 홈
 * - Filtered: 날짜 선택
 * - Search: 검색 결과 (코드는 동일, 데이터만 다름)
 */
sealed class ScreenMode {
    object Home : ScreenMode()
    data class Filtered(val date: LocalDate) : ScreenMode()
    data class Search(val query: String) : ScreenMode()
}
/**
 * TopBar의 시각적 상태를 정의합니다. -> 리팩토링:지금의 고정인 상태에서 바꾸기위함.
 * - Closed: 닫힘
 * - CalendarOpen: 캘린더 열림
 * - SearchOpen: 검색창 열림
 */
enum class TopBarState {
    Closed,
    CalendarOpen,
    SearchOpen
}

/**
 * ViewModel이 UI에게 전달할 최종 데이터 묶음입니다.
 * UI는 이 클래스 하나만 바라보면 됨.
 */
data class HomeUiState(
    val screenMode: ScreenMode = ScreenMode.Home,
    val diaries: List<Diary> = emptyList(), // ✅ 화면에 표시될 '단 하나'의 리스트
    val isLoading: Boolean = true,
    val recordedDates: List<LocalDate> = emptyList(), // 캘린더 점 찍기용
    val aiSummaryState: AiSummaryState = AiSummaryState()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    // ✅ 1. 단 하나의 상태(State) 선언
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isPaging = MutableStateFlow(false)
    val isPaging: StateFlow<Boolean> = _isPaging.asStateFlow()

    private var isFirstLoad = true

    private val summaryTimestampFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm 생성")
//    var isLoading = true

    // isLoading을 MutableStateFlow로 변경 (Compose에서 감지 가능!)
//    private val _isLoading = MutableStateFlow(true) // 초기값을 true로 설정
//    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()



    init {
        if (isFirstLoad) {
            loadDiaries(page = 0,isFiltered = false, date = null)
            loadAllRecordedDates()
            loadCategories()
            isFirstLoad = false  // ✅ 이후에는 다시 호출하지 않도록 설정
        }
    }

    // 새로고침 필요할 때 호출하기
//    fun refreshDiaries() {
//        loadAllDiaries()
//        loadAllRecordedDates()
//    }

    // ✅ 전체 일기 목록을 저장하는 StateFlow 추가
//    private val _fullDiaryListState = MutableStateFlow<List<Diary>>(emptyList())
//    val fullDiaryListState: StateFlow<List<Diary>> = _fullDiaryListState

//    private val _recordedDatesState = MutableStateFlow<List<LocalDate>>(emptyList()) // ✅ 전체 일기 날짜 저장
//    val recordedDatesState: StateFlow<List<LocalDate>> = _recordedDatesState

//    private val _filteredDiaryListState = MutableStateFlow<List<Diary>>(emptyList())
//    val filteredDiaryListState: StateFlow<List<Diary>> = _filteredDiaryListState

    // 기본 일기 목록 상태
//    private val _diaryListState = MutableStateFlow<List<Diary>>(emptyList())
//    val diaryListState: StateFlow<List<Diary>> = _diaryListState

    // ✅ **검색 결과 저장**
//    private val _searchResultsState = MutableStateFlow<List<Diary>>(emptyList())
//    val searchResultsState: StateFlow<List<Diary>> = _searchResultsState

    var currentPage = 0 // ✅ 일반 다이어리 리스트의 페이지 상태
    private var currentFilteredPage = 0
    private var searchPage = 0
    //private var currentSearchPage = 0 //  검색 결과의 페이지 상태

    // ✅ **최근 검색어 저장 (최대 3개)**
    private val _recentSearchesState = MutableStateFlow<List<String>>(emptyList())
    val recentSearchesState: StateFlow<List<String>> = _recentSearchesState

    // ✅ **현재 검색어 상태**
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // 초기 데이터 로딩 (페이지 1, 날짜 필터 없음)
//    init {
//        loadAllDiaries()
//        loadAllRecordedDates()
//        loadCategories()
//        //loadDiaries(page = 0, date = null) // 초기 데이터 로딩
//    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * ✅ 다음 페이지 로드 (무한 스크롤)
     */
//    fun loadNextPage() {
//        Log.d("Pagination", "🟢 loadNextPage() 호출됨 - 현재 페이지: $currentPage")
//
//        if (!isLoading) {
//            Log.d("Pagination", "🔥 loadDiaries() 실행 시도 - 페이지: $currentPage")
//            loadDiaries(page = currentPage, date = null)
//        } else {
//            Log.d("Pagination", "❌ API 요청 안됨 - isLoading이 true 상태")
//        }
//    }


    /**
     * ✅ 전체 일기 날짜 불러오기 (달력에서 사용)
     */
    fun loadAllRecordedDates() {
        viewModelScope.launch {
            try {
                val allDates = diaryRepository.getAllRecordedDates()
                _uiState.value = _uiState.value.copy(recordedDates = allDates)
                Log.d("HomeViewModel", "✅ 전체 기록된 날짜 로드 완료: ${allDates.size}개")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ 기록된 날짜 불러오기 실패: ${e.message}")
            }
        }
    }

    /**
     * 🟢 **전체 일기 데이터 로드 (캘린더에 표시될 모든 일기들)**
     */
//    fun loadAllDiaries() {
//        viewModelScope.launch {
//            try {
//                Log.d("HomeViewModel", "전체 다이어리 목록 불러오기 (날짜 필터 없음)")
//                val allDiaries = diaryRepository.getDiaries(page = 0, date = null) // ✅ 날짜 필터 없이 전체 가져오기
//                _fullDiaryListState.value = allDiaries
//                Log.d("HomeViewModel", "전체 다이어리 저장 완료: ${allDiaries.size}개")
//            } catch (e: Exception) {
//                Log.e("HomeViewModel", "전체 다이어리 불러오기 실패: ${e.message}")
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }

    /**
     * 서버에서 일기 목록을 가져와 diaryListState를 업데이트합니다.
     * 날짜 필터가 있을 경우 해당 날짜에 해당하는 일기를 불러옵니다.
     */
    fun loadDiaries(
        page: Int,
        date: LocalDate?,
        isFiltered: Boolean,
        reset: Boolean = false,
        onSucceed: () -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    ) {
        if (_isPaging.value) return // 🚨 중복 호출 방지

        _isPaging.value = true // ✅ 로딩 시작

        if (reset) {
            if (isFiltered) {
                Log.d("Pagination", "📌 reset=true → 필터된 다이어리 초기화 & currentFilteredPage=0")
                currentFilteredPage = 0  // 필터된 페이지 초기화
            } else {
                Log.d("Pagination", "📌 reset=true → 일반 다이어리 초기화 & currentPage=0")
                currentPage = 0  // 일반 페이지 초기화
            }
            // 리셋 시에 uistate 의 diaries도 비워준다.
            _uiState.value = _uiState.value.copy(diaries = emptyList())
        }

        viewModelScope.launch {
            try {
                val targetPage = if (isFiltered) currentFilteredPage else currentPage
                Log.d("Pagination", "📌 요청한 페이지: $targetPage (isFiltered=$isFiltered)")

                val newDiaries = diaryRepository.getDiaries(page = targetPage, date = date)

                // ✅ _uiState를 통째로 업데이트
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    // ✅ 현재 모드를 screenMode로 명시
                    screenMode = if (isFiltered) ScreenMode.Filtered(date!!) else ScreenMode.Home,
                    // ✅ diaries 리스트를 업데이트 (reset 여부에 따라 덮어쓰거나 추가)
                    diaries = if (reset) {
                        newDiaries
                    } else {
                        (_uiState.value.diaries + newDiaries).distinctBy { it.id }
                    }
                )

                if (newDiaries.isNotEmpty()) {
                    if (isFiltered) currentFilteredPage++
                    else currentPage++
                }
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            } finally {
                _isPaging.value = false
            }
        }
    }

    fun loadNextPage(isFiltered: Boolean, selectedDate: LocalDate?) {
        if (_isPaging.value) return // 🚨 중복 호출 방지

        Log.d("Pagination", "🟢 loadNextPage() 호출됨 - 현재 페이지: ${if (isFiltered) currentFilteredPage else currentPage}")

        if (isFiltered) {
            loadDiaries(page = currentFilteredPage, date = selectedDate, isFiltered = true)
        } else {
            loadDiaries(page = currentPage, isFiltered = false, date = null)
        }
    }

    /**
     * ✅ AI 요약 불러오기 (GET)
     * 요약을 '조회'하고, 없으면(null) '생성'을 요청합니다.
     */
    fun loadAiSummary(date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                aiSummaryState = AiSummaryState(isLoading = true, isVisible = true) // 1. 로딩 시작
            )
            try {
                // 2. Repository의 'getDailySummary' 호출
                val summary = diaryRepository.getDailySummary(date)

                if (summary != null) {
                    // 3a. 요약이 있으면 UI 상태 업데이트 (보이기)
                    _uiState.value = _uiState.value.copy(
                        aiSummaryState = AiSummaryState(
                            summaryText = summary.summary,
                            timestamp = summary.createdTime.format(summaryTimestampFormatter),
                            isVisible = true // ✅ 보이기
                        )
                    )
                    Log.d("HomeViewModel", "✅ AI 요약 조회 성공")
                } else {
                    // 3b. 요약이 없으면(null) 생성 요청
                    Log.w("HomeViewModel", "⚠️ AI 요약 없음(404). 새로 '생성'을 요청합니다.")
                    generateAiSummary(date) // ✅ 생성 함수 호출
                }
            } catch (e: Exception) {
                // 4. 조회 중 (404가 아닌) 다른 에러가 나면
                Log.e("HomeViewModel", "❌ AI 요약 조회 실패: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    aiSummaryState = AiSummaryState(
                        error = "요약 로딩 실패",
                        isVisible = true // ✅ 에러 카드라도 보여주기
                    )
                )
            }
        }
    }

    /**
     * AI 요약 생성/재생성 (POST/PUT)
     * (loadAiSummary 또는 onRefreshSummary에서 호출됨)
     */
    private fun generateAiSummary(date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                aiSummaryState = _uiState.value.aiSummaryState.copy(isLoading = true, isVisible = true)
            )
            try {
                // 1. generateDailySummary 호출 (이 함수가 알아서 POST/PUT 처리 후 DailySummary 반환)
                val newSummary = diaryRepository.generateDailySummary(date)

                // 2. 즉시 UI 상태 업데이트 (더 이상 GET을 또 호출할 필요 없음)
                _uiState.value = _uiState.value.copy(
                    aiSummaryState = AiSummaryState(
                        summaryText = newSummary.summary,
                        timestamp = newSummary.createdTime.format(summaryTimestampFormatter),
                        isVisible = true
                    )
                )
                Log.d("HomeViewModel", "✅ AI 요약 생성/재생성 성공 (API 응답 직접 사용)")

            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ AI 요약 생성/재생성 실패: ${e.message}}")
                _uiState.value = _uiState.value.copy(
                    aiSummaryState = AiSummaryState(
                        error = "요약 생성/새로고침 실패",
                        isVisible = true
                    )
                )
            }
        }
    }

    /**
     * AI 요약 새로고침 (HomeScreen의 onRefresh 버튼과 연결됨)
     */
    fun onRefreshSummary() {
        val mode = _uiState.value.screenMode
        if (mode !is ScreenMode.Filtered) return // Filtered 모드가 아니면 무시

        Log.d("HomeViewModel", "🔄 AI 요약 새로고침 요청 (날짜: ${mode.date})")
        generateAiSummary(mode.date) // 'generate' 함수가 알아서 PUT(재생성)을 호출
    }


    // ✅ SearchScreen의 검색 기능 (검색 시 reset = true)
    fun searchDiaries(searchWord: String, reset: Boolean = true, onSucceed: () -> Unit = {}, onFailed: (Exception) -> Unit = {}) {
//        if (_isLoading.value) return
//        _isLoading.value = true

        if (reset) {
            searchPage = 0  // ✅ 검색 시작 시 항상 0으로 초기화
            // ✅ 리셋 시 uiState의 diaries도 비워줌
            _uiState.value = _uiState.value.copy(diaries = emptyList())
        }

        viewModelScope.launch {
            try {
                val results = diaryRepository.getDiaries(page = searchPage, searchWord = searchWord)

                // ✅ _uiState를 통째로 업데이트
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    // ✅ 현재 모드를 'Search'로 명시
                    screenMode = ScreenMode.Search(searchWord),
                    // ✅ diaries 리스트를 업데이트
                    diaries = if (reset) {
                        results
                    } else {
                        (_uiState.value.diaries + results).distinctBy { it.id }
                    }
                )

                if (results.isNotEmpty()) {
                    searchPage++
                    if (searchWord.isNotBlank()) {
                        addRecentSearch(searchWord)
                    }
                }  // ✅ 다음 페이지 증가

                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }
    fun addRecentSearch(query: String) {
        val updatedSearches = (listOf(query) + _recentSearchesState.value).distinct().take(4) // 최대 3개 유지
        _recentSearchesState.value = updatedSearches
        Log.d("RecentSearchesViewModel", "🔹 검색어 추가됨: $updatedSearches")
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
                loadDiaries(page = 0, isFiltered = false, date = null, onSucceed = onSucceed, onFailed = { throw it })
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
                Log.d("modifyDiary", "📤 수정 요청 시작 (diaryId: $diaryId, image: ${image?.path}, categoryId: $categoryId)")

                // ✅ 다이어리 수정 API 호출
                diaryRepository.modifyDiary(
                    diaryId = diaryId,
                    categoryId = categoryId,
                    content = content,
                    image = image
                )

                // ✅ _uiState의 diaries 리스트만 map으로 수정
                _uiState.value = _uiState.value.copy(
                    diaries = _uiState.value.diaries.map { diary ->
                        if (diary.id == diaryId) {
                            diary.copy(
                                categoryId = categoryId ?: diary.categoryId,
                                content = content ?: diary.content,
                                imageUrl = image?.path ?: diary.imageUrl
                            )
                        } else diary
                    }
                )

                Log.d("modifyDiary", "✅ 수정 후 리스트 업데이트 완료")
                onSucceed() // ✅ 성공 콜백 호출

            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    // 🔹 현재 선택된 카테고리 상태 (각 다이어리에 개별 적용하기 위해 Map 사용)
    private val _selectedCategoryState = MutableStateFlow<Map<Long, Pair<Long, String>>>(emptyMap())
    val selectedCategoryState: StateFlow<Map<Long, Pair<Long, String>>> = _selectedCategoryState.asStateFlow()

    // 🔹 카테고리 목록 상태
    private val _categoryListState = MutableStateFlow<List<CategoryInfo>>(emptyList())
    val categoryListState: StateFlow<List<CategoryInfo>> = _categoryListState.asStateFlow()

    // ✅ 카테고리 목록 불러오기
    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = diaryRepository.getAllCategoryInfo()
                _categoryListState.value = categories
                Log.d("HomeViewModel", "✅ 카테고리 불러오기 성공: ${categories.map { it.name }}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ 카테고리 불러오기 실패: ${e.message}")
            }
        }
    }

    // ✅ 특정 다이어리의 선택된 카테고리 업데이트 (ID + 이름 저장)
    fun updateSelectedCategory(diaryId: Long, categoryId: Long, categoryName: String) {
        _selectedCategoryState.value = _selectedCategoryState.value.toMutableMap().apply {
            put(diaryId, Pair(categoryId, categoryName)) // ✅ 특정 다이어리의 카테고리 변경
        }
        Log.d("HomeViewModel", "✅ 다이어리($diaryId)의 카테고리 업데이트: $categoryName ($categoryId)")
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

                // ✅ _uiState의 diaries 리스트만 filterNot으로 수정
                _uiState.value = _uiState.value.copy(
                    diaries = _uiState.value.diaries.filterNot { it.id == diaryId }
                )

                loadAllRecordedDates()

                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    /**
     * ✅ UI가 날짜를 선택했을 때 호출하는 함수
     */
    fun onDateSelected(date: LocalDate) {
        // 날짜 필터 모드로 다이어리를 로드 (항상 0페이지부터, 리셋)
        loadDiaries(page = 0, date = date, isFiltered = true, reset = true)
        loadAiSummary(date)
    }

    /**
     * ✅ UI가 검색을 실행했을 때 호출하는 함수
     */
    fun onSearchSubmitted(query: String) {
        // 검색 모드로 다이어리를 로드 (항상 0페이지부터, 리셋)
        searchDiaries(searchWord = query, reset = true)
    }

    /**
     * ✅ UI가 필터/검색을 해제하고 홈으로 돌아갈 때 호출하는 함수
     */
    fun onClearMode() {
        // 홈 모드로 다이어리를 로드 (항상 0페이지부터, 리셋)
        loadDiaries(page = 0, date = null, isFiltered = false, reset = true)
    }
}