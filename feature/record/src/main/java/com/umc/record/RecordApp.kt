package com.umc.record

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class RecordMode {
    CAMERA,
    GALLARY,
}

@Composable
fun RecordApp(
    viewModel: RecordViewModel,
    mode: RecordMode,
    onNavigateToCategoryApp: () -> Unit
) {
    val navController = rememberNavController()
    var location by remember { mutableStateOf("Cafe PORTE") }

    // 사용자 이름 로드 (한 번만 실행)
    LaunchedEffect(Unit) {
        viewModel.loadUserName()
    }

    RecordNavHost(navController, viewModel, location, mode) { newLocation ->
        location = newLocation
    }
}

@Composable
fun RecordNavHost(
    navController: NavHostController,
    viewModel: RecordViewModel,
    location: String,
    mode: RecordMode,
    onLocationChange: (String) -> Unit
) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()  // 사용자 이름 관찰

    NavHost(
        navController = navController,
        startDestination = "record_screen"
    ) {
        composable("record_screen") {
            val categories by viewModel.categories.collectAsStateWithLifecycle()
            val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
            val diaryText by viewModel.diaryText.collectAsStateWithLifecycle()
            val selectedImage by viewModel.selectedImage.collectAsStateWithLifecycle() // 이미지 상태 추가

            LaunchedEffect(mode) {
                viewModel.loadImage(mode) // ✅ 모드에 따라 이미지 로드
            }

            RecordScreen(
                categories = categories,
                selectedCategory = selectedCategory,
                diaryText = diaryText,
                selectedImage = selectedImage,
                userName = userName,
                location = location,
                latitude = 37.5665,  // 서울 기본값 (실제 GPS 값 넣기)
                longitude = 126.9780,
                onCategorySelect = { viewModel.selectCategory(it) },
                onDiaryTextUpdate = { viewModel.updateDiaryText(it) },
                onSaveDiary = {
                    viewModel.saveDiary(
                        categoryId = 1,  // 임시 카테고리 ID
                        content = diaryText,
                        imagePath = selectedImage,
                        latitude = 37.5665,  // GPS 데이터
                        longitude = 126.9780,
                        locationName = location
                    )
                },
                onEditLocation = { newLocation -> /* 위치 수정 로직 */ }
            )
        }
        composable("record_edit_screen") {
            var isLocationMarkingEnabled by remember { mutableStateOf(true) }
            val context = LocalContext.current as Activity

            val permissionRequester = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted)
                    Toast.makeText(context, "위치 권한이 거부되었습니다", Toast.LENGTH_SHORT).show()
                isLocationMarkingEnabled = isGranted
            }

            LaunchedEffect(key1 = Unit) {
                // 뷰모델 정보 초기화
                viewModel.initialize()
                // 위치 권한 확인
                isLocationMarkingEnabled = permissionRequester.checkLocationPermission(context = context)
            }

            RecordEditLocationScreen(
                mapView = {
                    viewModel.MapView(
                        isLocationMarkingEnabled = true
                    )
                },
                onLocationButtonClicked = {
                    viewModel.movePinToCurrentLocation(
                        onSucceed = { /* Success 처리 */ },
                        onFailed = {
                            permissionRequester.checkLocationPermission(
                                context = context,
                                requestOnNotGranted = true,
                            )
                        }
                    )
                },
                onChangeLocationButtonClicked = { newLocation -> // 변경된 위치 반영
                    onLocationChange(newLocation)
                    navController.popBackStack()
                },
                location = location
            )
        }
    }
}

private fun ActivityResultLauncher<String>.checkLocationPermission(
    context: Activity,
    requestOnNotGranted: Boolean = shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)
): Boolean {
    val result = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!result && requestOnNotGranted) launch(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    return result
}