package com.umc.record.navigation.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.Geocoder
import com.umc.core.model.CategoryInfo
import com.umc.core.repository.DiaryRepository
import com.umc.core.repository.UserRepository
import com.umc.record.util.getImageMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val geocoder: Geocoder,
    private val userRepository: UserRepository,
    private val diaryRepository: DiaryRepository,
) : ViewModel() {

    private val userNameState = MutableStateFlow("")
    private val categoryInfosState = MutableStateFlow(listOf<CategoryInfo>())
    private val selectedCategoryState = MutableStateFlow<CategoryInfo?>(null)
    private val imageState = MutableStateFlow<File?>(null)
    private val dateState = MutableStateFlow(LocalDateTime.now())
    private val locationState = MutableStateFlow<Pair<Double, Double>?>(null)
    private val locationNameState = MutableStateFlow<String?>(null)
    private val isLocationMissingState = MutableStateFlow<Boolean?>(null)
    private val isLoadingState = MutableStateFlow(false)

    val userName: StateFlow<String> = userNameState
    val categoryInfos: StateFlow<List<CategoryInfo>> = categoryInfosState
    val selectedCategory: StateFlow<CategoryInfo?> = selectedCategoryState
    val image: StateFlow<File?> = imageState
    val date: StateFlow<LocalDateTime> = dateState
    val location: StateFlow<Pair<Double, Double>?> = locationState
    val locationName: StateFlow<String?> = locationNameState
    val isLocationMissing: StateFlow<Boolean?> = isLocationMissingState
    val isLoading: StateFlow<Boolean> = isLoadingState
    val content = MutableStateFlow("")

    suspend fun loadUserName() {
        val userInfo = userRepository.getUserInfo()
        userNameState.value = userInfo.name
    }

    suspend fun loadCategoryInfos() {
        val categoryInfos = diaryRepository.getAllCategoryInfo()
        categoryInfosState.value = categoryInfos
        selectedCategoryState.value = categoryInfos.firstOrNull()
    }

    fun setImage(image: File) {
        val metadata = getImageMetadata(image)

        imageState.value = image
        dateState.value = metadata.date ?: LocalDateTime.now()

        if (metadata.latitude == null || metadata.longitude == null) {
            isLocationMissingState.value = true
            return
        }

        locationState.value = metadata.latitude to metadata.longitude

        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoadingState.value = true

                val address =
                    geocoder.convertCoordinateToAddress(metadata.latitude, metadata.longitude)

                locationNameState.value = address
                isLocationMissingState.value = false
            } catch (_: Exception) {
                isLocationMissingState.value = true
            } finally {
                isLoadingState.value = false
            }
        }
    }

    fun selectCategory(categoryId: Long) {
        selectedCategoryState.value = categoryInfos.value.find { it.id == categoryId } ?: return
    }

    fun updateLocation(
        latitude: Double? = null,
        longitude: Double? = null,
        locationName: String? = null,
    ) {
        locationState.value?.let { previousLocation ->
            locationState.value =
                (latitude ?: previousLocation.first) to (longitude ?: previousLocation.second)
        }

        if (locationName != null) locationNameState.value = locationName

        isLocationMissingState.value =
            locationState.value == null || locationNameState.value == null
    }

    suspend fun saveDiary() {
        val categoryId = selectedCategory.value?.id ?: return
        val date = date.value
        val content = content.value
        val image = image.value ?: return
        val (latitude, longitude) = location.value ?: return
        val locationName = locationName.value ?: return

        if (isLoading.value) return
        isLoadingState.value = true

        try {
            diaryRepository.createDiary(
                categoryId = categoryId,
                date = date,
                content = content,
                image = image,
                latitude = latitude,
                longitude = longitude,
                locationName = locationName,
            )
        } finally {
            isLoadingState.value = false
        }
    }
}