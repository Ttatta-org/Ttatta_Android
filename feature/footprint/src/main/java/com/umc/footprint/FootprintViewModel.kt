package com.umc.footprint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.model.CategoryInfo
import com.umc.core.model.DiaryForCard
import com.umc.core.model.DiaryForRemind
import com.umc.core.repository.DiaryRepository
import com.umc.core.repository.UserRepository
import com.umc.design.CategoryColor
import com.umc.footprint.core.MapHandler
import com.umc.footprint.core.MapMarker
import com.umc.footprint.model.event.MapMarkerClickedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FootprintViewModel @Inject constructor(
    private val mapHandler: MapHandler,
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private var previousClickedClusterId: Long? = null

    private val diaryMapState = mutableStateOf<Map<Int, DiaryForCard>>(emptyMap())
    private val mapMarkerClickedEventState = mutableStateOf<MapMarkerClickedEvent?>(null)
    private val categoryListState = mutableStateOf<List<CategoryInfo>>(listOf())
    private val selectedCategoryIdState = mutableStateOf<Long?>(null)
    private val userNameState = mutableStateOf("")

    val footprintMarkerClickedEvent get() = mapMarkerClickedEventState.value
    val diaryMap get() = diaryMapState.value
    val categoryList get() = categoryListState.value
    val selectedCategoryId get() = selectedCategoryIdState.value
    val userName get() = userNameState.value

    init {
        viewModelScope.launch {
            mapHandler.addOnDismissListener { previousClickedClusterId = null }
        }
    }

    @Composable
    fun MapView(
        isBlurApplied: Boolean,
        isLocationMarkingEnabled: Boolean,
    ) {
        mapHandler.MapView(
            isBlurApplied = isBlurApplied, isLocationMarkingEnabled = isLocationMarkingEnabled,
        )
    }

    suspend fun loadInitialData() {
        coroutineScope {
            launch { getAllCategoryInfoFromServer() }
            launch { moveMapToCurrentPosition() }
            launch { getUserNameFromServer() }
            launch { selectShowingCategory(categoryId = null) }
        }
    }

    suspend fun moveMapToCurrentPosition() {
        mapHandler.dismissMarkerEvent()
        mapHandler.moveToCurrentPosition()
    }

    suspend fun moveMapToPosition(
        latitude: Double,
        longitude: Double,
        pivot: Offset,
    ) {
        mapHandler.moveTo(
            latitude = latitude,
            longitude = longitude,
            offset = pivot,
            animationTime = 200,
        )
    }

    suspend fun selectShowingCategory(categoryId: Long?) {
        mapHandler.removeAllMarkers()
        diaryRepository.getAllFootprints(categoryId = categoryId).forEach {
            CoroutineScope(Dispatchers.IO).launch {
                markMap(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    clusterId = it.clusterId,
                    zIndex = it.diaryId.toInt(),
                    isOverlapping = it.isClustered,
                    color = it.color,
                )
            }
        }
        selectedCategoryIdState.value = categoryId
    }

    suspend fun getDiaryFromServer(page: Int) {
        runCatching {
            val diary = diaryRepository.getDiaries(
                page = page,
                clusterId = footprintMarkerClickedEvent!!.clusterId,
                categoryId = selectedCategoryId,
            )
            diaryMapState.value += (page to diary)
        }.onFailure {
            diaryMapState.value -= page
        }
    }

    suspend fun modifyDiary(diaryId: Long, content: String) {
        diaryRepository.modifyDiary(diaryId = diaryId, content = content)
        getDiaryFromServer(
            page = diaryMap.firstNotNullOf { (page, diary) ->
                if (diary.id == diaryId) page else null
            },
        )
    }

    suspend fun deleteDiary(diaryId: Long) {
        diaryRepository.deleteDiary(diaryId = diaryId)
        getAllCategoryInfoFromServer()

        val startPage = diaryMap.firstNotNullOf { (page, diary) ->
            if (diary.id == diaryId) page else null
        }
        val endPage = diaryMap.keys.max()

        (startPage..endPage).forEach { page ->
            runCatching {
                getDiaryFromServer(page = page)
            }.onFailure {
                if (page == 0) {
                    mapHandler.dismissMarkerEvent()
                    selectShowingCategory(categoryId = null)
                }
            }
        }
    }

    suspend fun getDiaryForRemindFromServer(id: Long): DiaryForRemind {
        return diaryRepository.getDiaryForRemind(id = id)
    }

    private suspend fun getAllCategoryInfoFromServer() {
        categoryListState.value = diaryRepository.getAllCategoryInfo()
    }

    private suspend fun getUserNameFromServer() {
        userNameState.value = userRepository.getUserInfo().name
    }

    private suspend fun markMap(
        latitude: Double,
        longitude: Double,
        clusterId: Long,
        zIndex: Int,
        isOverlapping: Boolean,
        color: CategoryColor?,
    ) {
        val marker = MapMarker(
            latitude = latitude,
            longitude = longitude,
            zIndex = zIndex,
            color = color,
            isOverlapping = isOverlapping,
            onClicked = onClicked@{ offset ->
                if (previousClickedClusterId != clusterId) {
                    mapMarkerClickedEventState.value = MapMarkerClickedEvent(
                        offset = offset,
                        latitude = latitude,
                        longitude = longitude,
                        clusterId = clusterId,
                        isBook = isOverlapping,
                        color = color,
                    )
                    previousClickedClusterId = clusterId
                } else {
                    previousClickedClusterId = null
                }

                return@onClicked {
                    mapMarkerClickedEventState.value = null
                    diaryMapState.value = emptyMap()
                }
            },
        )

        mapHandler.addMarker(marker)
    }
}
