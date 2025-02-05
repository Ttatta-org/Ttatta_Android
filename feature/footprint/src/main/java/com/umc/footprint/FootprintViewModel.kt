package com.umc.footprint

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.model.CategoryInfo
import com.umc.core.model.DiaryForCard
import com.umc.core.repository.DiaryRepository
import com.umc.core.repository.UserRepository
import com.umc.design.CategoryColor
import com.umc.footprint.core.MapHandler
import com.umc.footprint.core.MapMarker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClickedMarkerInfo(
    val x: Float,
    val y: Float,
    val clusterId: Long,
)

@HiltViewModel
class FootprintViewModel @Inject constructor(
    private val mapHandler: MapHandler,
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val markers = mutableMapOf<Long, MutableList<MapMarker>>()

    private val diaryPagesState = mutableStateListOf<List<DiaryForCard>>()
    private val isDiaryFullyLoadedState = mutableStateOf(false)
    private val clickedMarkerInfoState = mutableStateOf<ClickedMarkerInfo?>(null)
    private val categoryListState = mutableStateOf<List<CategoryInfo>>(listOf())
    private val selectedCategoryIdState = mutableStateOf<Long?>(null)
    private val userNameState = mutableStateOf("")

    val clickedMarkerInfo get() = clickedMarkerInfoState.value
    val diaryList get() = diaryPagesState.flatten()
    val isDiaryFullyLoaded get() = isDiaryFullyLoadedState.value
    val categoryList get() = categoryListState.value
    val selectedCategoryId get() = selectedCategoryIdState.value
    val userName get() = userNameState.value

    init {
        viewModelScope.launch {
            // 모든 발자국 로드 및 마커 추가
            launch {
                diaryRepository.getAllFootprints().map { footprint ->
                    markMap(
                        latitude = footprint.latitude,
                        longitude =  footprint.longitude,
                        clusterId = footprint.clusterId,
                        categoryId = footprint.categoryId,
                        color = footprint.color,
                    )
                }
            }
            // 현재 위치로 맵 이동
            launch {
                moveMapToCurrentPosition()
            }
            // 카테고리 정보 로드
            launch {
                getAllCategoryInfoFromServer(
                    onSucceed = { /* TODO */ },
                    onFailed = { /* TODO */ },
                )
            }
            // 사용자 이름 로드
            launch {
                getUserNameFromServer(
                    onSucceed = { /* TODO */ },
                    onFailed = { /* TODO */ },
                )
            }
        }
    }

    fun getMapView(): @Composable () -> Unit {
        return mapHandler.getMapView()
    }

    fun moveMapToCurrentPosition() {
        viewModelScope.launch { mapHandler.moveToCurrentPosition() }
    }

    fun selectShowingCategory(
        categoryId: Long?,
    ) {
        viewModelScope.launch {
            mapHandler.removeAllMarkers()
            markers[categoryId]?.forEach { marker ->
                mapHandler.addMarker(marker)
            } ?: markers.values.forEach { markerList ->
                markerList.forEach { marker ->
                    mapHandler.addMarker(marker)
                }
            }
            selectedCategoryIdState.value = categoryId
        }
    }

    fun getDiaryFromServer(
        page: Int = diaryPagesState.size,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val diaryList = diaryRepository.getDiaries(
                    page = page,
                    clusterId = clickedMarkerInfo!!.clusterId,
                )
                if (page < diaryPagesState.size)
                    diaryPagesState[page] = diaryList
                else
                    diaryPagesState.add(diaryList)
                onSucceed()
            } catch (e: Exception) {
                // TODO: 빈 리스트 반환으로써 오류가 났을 경우에 분기 처리
                isDiaryFullyLoadedState.value = true
                if (page < diaryPagesState.size)
                    diaryPagesState.removeAt(page)
                onFailed(e)
            }
        }
    }

    fun modifyDiary(
        diaryId: Long,
        content: String,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.modifyDiary(
                    diaryId = diaryId,
                    content = content,
                )
                getDiaryFromServer(
                    page = diaryPagesState.indexOfFirst { page ->
                        page.any { diary -> diary.id == diaryId }
                    },
                    onSucceed = { onSucceed() },
                    onFailed = { throw it },
                )
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun deleteDiary(
        diaryId: Long,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.deleteDiary(diaryId = diaryId)
                for (page in diaryPagesState.indexOfFirst { page ->
                    page.any { diary -> diary.id == diaryId }
                } until diaryPagesState.size) {
                    getDiaryFromServer(
                        page = page,
                        onSucceed = onSucceed,
                        onFailed = { throw it }
                    )
                }
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun getAllCategoryInfoFromServer(
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                categoryListState.value = diaryRepository.getAllCategoryInfo()
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun getUserNameFromServer(
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                userNameState.value = userRepository.getUserInfo().name
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    private fun markMap(
        latitude: Double,
        longitude: Double,
        clusterId: Long,
        categoryId: Long,
        color: CategoryColor?,
    ) {
        val marker = MapMarker(
            latitude = latitude,
            longitude = longitude,
            color = color,
            onClicked = onClicked@{ x, y ->
                clickedMarkerInfoState.value = ClickedMarkerInfo(
                    x = x,
                    y = y,
                    clusterId = clusterId,
                )
                getDiaryFromServer(
                    page = 0,
                    onSucceed = { /* TODO */ },
                    onFailed = { /* TODO */ },
                )
                return@onClicked {
                    clickedMarkerInfoState.value = null
                    diaryPagesState.clear()
                    isDiaryFullyLoadedState.value = false
                }
            }
        )
        markers[categoryId]?.add(marker) ?: run { markers[categoryId] = mutableListOf(marker) }
        viewModelScope.launch { mapHandler.addMarker(marker) }
    }
}