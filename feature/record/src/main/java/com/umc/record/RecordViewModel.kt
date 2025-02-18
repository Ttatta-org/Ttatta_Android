package com.umc.record

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.repository.DiaryRepository
import com.umc.core.repository.UserRepository
import com.umc.record.core.LocationHandler
import com.umc.record.core.MapHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

data class ClickedMarkerInfo(
    val x: Float,
    val y: Float,
    val clusterId: Long,
)

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val mapHandler: MapHandler,
    private val locationHandler: LocationHandler,
    private val userRepository: UserRepository,
    private val diaryRepository: DiaryRepository
): ViewModel() {
    private val userNameState = mutableStateOf("")

    // 사용자 이름을 StateFlow로 변경하여 Compose에서 사용할 수 있도록 수정
    private val _userName = MutableStateFlow("사용자")  // 기본값 설정
    val userName: StateFlow<String> = _userName

    fun loadUserName() {
        viewModelScope.launch {
            try {
                _userName.value = userRepository.getUserInfo().name
                Log.d("RecordViewModel", "✅ 사용자 이름 불러오기 성공: ${_userName.value}")
            } catch (e: Exception) {
                Log.e("RecordViewModel", "🚨 사용자 이름 불러오기 실패", e)
            }
        }
    }

    // 카테고리 목록 (더미 데이터)
    private val _categories = MutableStateFlow(
        listOf(
            "친구들" to "Red",
            "가족" to "Blue",
            "남자친구" to "Pink",
            "일상" to "Yellow",
            "다시 오고 싶은 장소" to "Green",
            "제주여행" to "Turquoise"
        )
    )
    val categories: StateFlow<List<Pair<String, String>>> = _categories

    // 선택된 카테고리
    private val _selectedCategory = MutableStateFlow("default")
    val selectedCategory: StateFlow<String> = _selectedCategory

    // ✅ 사용자가 입력한 다이어리 텍스트 저장
    private val _diaryText = MutableStateFlow("")
    val diaryText: StateFlow<String> = _diaryText

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    // ✅ 사용자가 입력한 다이어리 텍스트 업데이트
    fun updateDiaryText(newText: String) {
        _diaryText.value = newText
    }

    private val _selectedImage = MutableStateFlow<String?>(null)  // ✅ 선택한 이미지 경로 저장
    val selectedImage: StateFlow<String?> = _selectedImage

    fun loadImage(mode: RecordMode) {
        viewModelScope.launch {
            _selectedImage.value = when (mode) {
                RecordMode.CAMERA -> capturePhoto()  // ✅ 카메라 촬영 함수 호출
                RecordMode.GALLARY -> pickImageFromGallery()  // ✅ 갤러리에서 선택 함수 호출
            }
        }
    }

    // ✅ 카메라에서 사진 촬영 (예시)
    private fun capturePhoto(): String? {
        // 실제로는 Intent를 사용하여 사진을 찍고 저장해야 함.
        return "file://path_to_camera_photo.jpg"
    }

    // ✅ 갤러리에서 사진 선택 (예시)
    private fun pickImageFromGallery(): String? {
        // 실제로는 Intent를 사용하여 사진을 가져와야 함.
        return "file://path_to_gallery_photo.jpg"
    }

    fun initialize() {
        viewModelScope.launch {
            launch {
                movePinToCurrentLocation()  // 앱 시작 시 지도 위치 설정
            }
            // 사용자 이름 로드
            launch {
                getUserNameFromServer(
                    onSucceed = { /* TODO */ },
                    onFailed = { /* TODO */ },
                )
            }
            launch {
                loadUserName()
            }
        }
    }

    // 지도
    @Composable
    fun MapView(
        isLocationMarkingEnabled: Boolean,
    ) {
        mapHandler.MapView(
            isLocationMarkingEnabled = isLocationMarkingEnabled
        )
    }

    // ✅ 현재 위치 가져와서 지도 이동시키는 함수
    fun movePinToCurrentLocation(
        onSucceed: () -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val location = locationHandler.getCurrentLocation() // ✅ 현재 위치 가져오기
                Log.d("RecordViewModel", "📍 현재 위치: ${location.latitude}, ${location.longitude}")

                mapHandler.movePin(location.latitude, location.longitude) // ✅ 지도 핀 이동
                Log.d("RecordViewModel", "✅ 지도 핀이 현재 위치로 이동됨")

                onSucceed()
            } catch (e: Exception) {
                Log.e("RecordViewModel", "🚨 현재 위치 가져오기 실패", e)
                onFailed(e)
            }
        }
    }

    fun saveDiary(
        categoryId: Long,
        content: String,
        imagePath: String?, // 이미지 경로 (갤러리 or 카메라)
        latitude: Double,
        longitude: Double,
        locationName: String
    ) {
        viewModelScope.launch {
            try {
                val imageFile = imagePath?.let { File(it) } // 이미지 파일 변환

                diaryRepository.createDiary(
                    categoryId = categoryId,
                    date = LocalDateTime.now(), // 현재 시간
                    content = content,
                    image = imageFile ?: File(""), // 이미지 없으면 빈 파일
                    latitude = latitude,
                    longitude = longitude,
                    locationName = locationName
                )

                Log.d("RecordViewModel", "✅ 기록 저장 성공!")
            } catch (e: Exception) {
                Log.e("RecordViewModel", "🚨 기록 저장 실패: ${e.message}", e)
            }
        }
    }

    private fun getUserNameFromServer(
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
}
