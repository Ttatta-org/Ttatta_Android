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

    private var isFirstLoad = true  // ✅ 처음 로드 여부 확인

    init {
        if (isFirstLoad) {
            loadDiaries(page = 0,isFiltered = false, date = null)
            loadAllRecordedDates()
            isFirstLoad = false  // ✅ 이후에는 다시 호출하지 않도록 설정
        }
    }

    // 새로고침 필요할 때 호출하기
    fun refreshDiaries() {
        loadAllDiaries()
        loadAllRecordedDates()
    }

    // ✅ 전체 일기 목록을 저장하는 StateFlow 추가
    private val _fullDiaryListState = MutableStateFlow<List<Diary>>(emptyList())
    val fullDiaryListState: StateFlow<List<Diary>> = _fullDiaryListState

    private val _recordedDatesState = MutableStateFlow<List<LocalDate>>(emptyList()) // ✅ 전체 일기 날짜 저장
    val recordedDatesState: StateFlow<List<LocalDate>> = _recordedDatesState

    private val _filteredDiaryListState = MutableStateFlow<List<Diary>>(emptyList())
    val filteredDiaryListState: StateFlow<List<Diary>> = _filteredDiaryListState

    // 기본 일기 목록 상태
    private val _diaryListState = MutableStateFlow<List<Diary>>(emptyList())
    val diaryListState: StateFlow<List<Diary>> = _diaryListState

    var currentPage = 0 // ✅ 일반 다이어리 리스트의 페이지 상태
    private var searchPage = 0
    //private var currentSearchPage = 0 // ✅ 검색 결과의 페이지 상태

    var isLoading = false // ✅ 중복 요청 방지

    // ✅ **검색 결과 저장**
    private val _searchResultsState = MutableStateFlow<List<Diary>>(emptyList())
    val searchResultsState: StateFlow<List<Diary>> = _searchResultsState

    // ✅ **최근 검색어 저장 (최대 3개)**
    private val _recentSearchesState = MutableStateFlow<List<String>>(emptyList())
    val recentSearchesState: StateFlow<List<String>> = _recentSearchesState

    // ✅ **현재 검색어 상태**
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // 초기 데이터 로딩 (페이지 1, 날짜 필터 없음)
    init {
        loadAllDiaries()
        loadAllRecordedDates()
        //loadDiaries(page = 0, date = null) // 초기 데이터 로딩
    }

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
                _recordedDatesState.value = allDates
                Log.d("HomeViewModel", "✅ 전체 기록된 날짜 로드 완료: ${allDates.size}개")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ 기록된 날짜 불러오기 실패: ${e.message}")
            }
        }
    }

    /**
     * 🟢 **전체 일기 데이터 로드 (캘린더에 표시될 모든 일기들)**
     */
    fun loadAllDiaries() {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "📌 전체 다이어리 목록 불러오기 (날짜 필터 없음)")
                val allDiaries = diaryRepository.getDiaries(page = 0, date = null) // ✅ 날짜 필터 없이 전체 가져오기
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

    fun loadDiaries(page: Int, date: LocalDate?, isFiltered: Boolean, reset: Boolean = false, onSucceed: () -> Unit = {}, onFailed: (Exception) -> Unit = {}) {
        if (isLoading) return
        isLoading = true

        if (reset) {
            currentPage = 0
            if (isFiltered) _filteredDiaryListState.value = emptyList()
            else _diaryListState.value = emptyList()
        }

        viewModelScope.launch {
            try {
                val newDiaries = diaryRepository.getDiaries(page = page, date = date)
                Log.d("HomeViewModel", "✅ 다이어리 데이터 로드 성공: ${newDiaries.size}개")

                if (isFiltered) {
                    if (reset) {
                        // 새로운 날짜 선택 시 기존 데이터 삭제
                        _filteredDiaryListState.value = newDiaries
                    } else {
                        // 무한스크롤 시 기존 데이터 유지하면서 추가
                        _filteredDiaryListState.value = (_filteredDiaryListState.value + newDiaries).distinctBy { it.id }
                    }

                    if (newDiaries.isNotEmpty()) currentPage++
                } else {
                    // ✅ 전체 다이어리 리스트 업데이트
                    _diaryListState.value = (_diaryListState.value + newDiaries).distinctBy { it.id }
                    if (newDiaries.isNotEmpty()) currentPage++
                }

                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            } finally {
                isLoading = false
            }
        }
    }
    fun loadNextPage(isFiltered: Boolean, selectedDate: LocalDate?) {
        Log.d("Pagination", "🟢 loadNextPage() 호출됨 - 현재 선택 날짜: $selectedDate")
        Log.d("Pagination", "🟢 loadNextPage() 호출됨 - 현재 페이지: $currentPage")


        Log.d("Pagination", "🔥 loadDiaries() 실행 시도 - 페이지: $currentPage")

        if (isFiltered) {
            // ✅ 필터된 날짜의 다이어리 무한 스크롤
            Log.d("Pagination", "📌 특정 날짜의 다이어리 로드 시도")
            loadDiaries(page = currentPage, date = selectedDate, isFiltered = true)
        } else {
            Log.d("Pagination", "📌 전체 다이어리 로드 시도")
            // ✅ 전체 다이어리 무한 스크롤
            loadDiaries(page = currentPage, isFiltered = false, date = null)
        }
    }

    // ✅ SearchScreen의 검색 기능 (검색 시 reset = true)
    fun searchDiaries(searchWord: String, reset: Boolean = true, onSucceed: () -> Unit = {}, onFailed: (Exception) -> Unit = {}) {
        if (isLoading) return
        isLoading = true

        if (reset) {
            searchPage = 0  // ✅ 검색 시작 시 항상 0으로 초기화
            _searchResultsState.value = emptyList()  // ✅ 기존 검색 결과 초기화
            //addRecentSearch(searchWord)
        }

        viewModelScope.launch {
            try {
                val results = diaryRepository.getDiaries(page = searchPage, searchWord = searchWord)
                _searchResultsState.value = (_searchResultsState.value + results).distinctBy { it.id }
                if (results.isNotEmpty()) {
                    searchPage++
                    if (searchWord.isNotBlank()) {
                        addRecentSearch(searchWord)
                    }
                }  // ✅ 다음 페이지 증가

                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            } finally {
                isLoading = false
            }
        }
    }
    fun addRecentSearch(query: String) {
        val updatedSearches = (listOf(query) + _recentSearchesState.value).distinct().take(3) // 최대 3개 유지
        _recentSearchesState.value = updatedSearches
        Log.d("RecentSearchesViewModel", "🔹 검색어 추가됨: $updatedSearches")
    }

//    fun loadDiaries(
//        page: Int,
//        date: LocalDate?,
//        reset: Boolean = false, // ✅ 초기화 여부
//        isFiltered: Boolean = false, // ✅ 특정 날짜의 데이터만 가져오는지 여부
//        onSucceed: () -> Unit = {},
//        onFailed: (e: Exception) -> Unit = {}
//    ) {
//        Log.d("Pagination", "🔥 loadDiaries() 호출됨 - 페이지: $page, 날짜: $date, reset: $reset, isFiltered: $isFiltered")
//
//        if (isLoading) {
//            Log.d("Pagination", "❌ 중복 요청 방지 - API 요청 차단됨 (isLoading = true)")
//            return
//        }
//
//        isLoading = true
//
//        viewModelScope.launch {
//            try {
//                Log.d("HomeViewModel", "📌 loadDiaries() 실행됨 - 페이지: $page, 날짜: $date")
//
//                val newDiaries = diaryRepository.getDiaries(page = page, date = date)
//
//                if (isFiltered) {
//                    // ✅ 특정 날짜의 데이터를 별도로 저장
//                    _filteredDiaryListState.value = newDiaries
//                } else {
//                    // ✅ 기존 데이터 유지하면서 새로운 데이터 추가 (중복 방지)
//                    if (reset) {
//                        _diaryListState.value = newDiaries
//                    } else {
//                        _diaryListState.value = (_diaryListState.value.filterNot { diary ->
//                            newDiaries.any { it.id == diary.id }
//                        } + newDiaries).toList()
//                    }
//                }
//
//                if (newDiaries.isNotEmpty()) {
//                    Log.d("Pagination", "🚀 페이지 증가 전: $currentPage")
//                    currentPage++
//                    Log.d("Pagination", "✅ 페이지 증가 후: $currentPage")
//                } else {
//                    Log.d("Pagination", "⚠️ 새로운 데이터 없음 (마지막 페이지 도달)")
//                }
//
//                Log.d("HomeViewModel", "✅ 다이어리 데이터 로드 성공: ${newDiaries.size}개")
//                onSucceed()
//            } catch (e: Exception) {
//                onFailed(e)
//            } finally {
//                isLoading = false
//                Log.d("Pagination", "✅ isLoading 상태 해제됨 (false)")
//            }
//        }
//    }
//
//    /**
//     * 🔍 **검색어에 따라 일기 목록 불러오기**
//     */
//    /**
//     * 🔍 **검색어에 따라 일기 목록 불러오기 (무한 스크롤)**
//     */
//    fun searchDiaries(
//        searchWord: String,
//        reset: Boolean = true, // ✅ 새로운 검색어 입력 시 초기화 여부
//        onSucceed: () -> Unit,
//        onFailed: (e: Exception) -> Unit
//    ) {
//        if (reset) {
//            currentSearchPage = 0 // ✅ 새로운 검색어가 입력되면 페이지 초기화
//            _searchResultsState.value = emptyList() // ✅ 기존 검색 결과 초기화
//        }
//
//        if (isLoading) {
//            Log.d("Pagination", "❌ 중복 요청 방지 - API 요청 차단됨 (isLoading = true)")
//            return
//        }
//        isLoading = true
//
//        viewModelScope.launch {
//            try {
//                _searchQuery.value = searchWord
//                Log.d("HomeViewModel", "🔍 검색어: $searchWord (페이지: $currentSearchPage)")
//
//                // ✅ 검색 API 호출 (page를 적용)
//                val results = diaryRepository.getDiaries(page = currentSearchPage, searchWord = searchWord)
//
////                if (results.isNotEmpty()) {
////                    _searchResultsState.value = (_searchResultsState.value + results).toList() // ✅ 기존 검색 결과에 추가
//                // ✅ 검색어가 변경되면 새 리스트로 교체
//
//                _searchResultsState.value = results
//                if (results.isNotEmpty() && _searchResultsState.value.size > 5) {
//                    Log.d("Pagination", "📌 검색 리스트 업데이트됨 (총 개수: ${_searchResultsState.value.size})")
//
//                    currentSearchPage++ // ✅ 다음 페이지 설정
//                    Log.d("Pagination", "✅ 검색 페이지 증가 후: $currentSearchPage")
//
//                } else {
//                    Log.d("Pagination", "⚠️ 새로운 검색 데이터 없음 (마지막 페이지 도달)")
//                }
//
//                // ✅ 최근 검색어 업데이트 (중복 제거 & 최신순 정렬)
//                if (searchWord.isNotBlank() && reset) {
//                    val updatedList = _recentSearchesState.value.toMutableList()
//
//                    updatedList.remove(searchWord) // 중복 제거
//                    updatedList.add(0, searchWord) // 맨 앞에 추가
//
//                    if (updatedList.size > 3) {
//                        updatedList.removeAt(updatedList.size - 1) // 최대 3개 유지
//                    }
//
//                    _recentSearchesState.value = updatedList
//                }
//
//                Log.d("HomeViewModel", "✅ 검색 결과: ${results.size}개")
//
//                onSucceed()
//
//            } catch (e: Exception) {
//                Log.e("HomeViewModel", "❌ 검색 실패: ${e.message}")
//                onFailed(e)
//            } finally {
//                isLoading = false // ✅ isLoading 해제
//                Log.d("Pagination", "✅ isLoading 상태 해제됨 (false)")
//            }
//        }
//    }

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
                Log.d("modifyDiary", "📤 수정 요청 시작 (diaryId: $diaryId, image: ${image?.path})")

                // ✅ 다이어리 수정 API 호출
                diaryRepository.modifyDiary(
                    diaryId = diaryId,
                    categoryId = categoryId,
                    content = content,
                    image = image
                )

                // ✅ 기존 리스트에서 해당 다이어리 찾아 업데이트
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

                _searchResultsState.value = _searchResultsState.value.map { diary ->
                    if (diary.id == diaryId) {
                        diary.copy(
                            content = content ?: diary.content,
                            imageUrl = image?.path ?: diary.imageUrl
                        )
                    } else diary
                }

                Log.d("modifyDiary", "✅ 수정 후 리스트 업데이트 완료")
                onSucceed() // ✅ 성공 콜백 호출

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
                _searchResultsState.value = _searchResultsState.value.filterNot { it.id == diaryId }

                loadAllRecordedDates()

                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }
}