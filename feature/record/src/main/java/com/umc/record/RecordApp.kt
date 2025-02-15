package com.umc.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RecordApp(
    viewModel: RecordViewModel,
    onNavigateToCategoryApp: () -> Unit
) {
    val navController = rememberNavController()
    var location by remember { mutableStateOf("Cafe PORTE") }

    RecordNavHost(navController, viewModel, location) { newLocation ->
        location = newLocation // ✅ 위치 변경 시 업데이트
    }
}

@Composable
fun RecordNavHost(
    navController: NavHostController,
    viewModel: RecordViewModel,
    location: String,
    onLocationChange: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "record_screen"
    ) {
        composable("record_screen") {
            val categories by viewModel.categories.collectAsStateWithLifecycle()
            val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
            val diaryText by viewModel.diaryText.collectAsStateWithLifecycle()

            RecordScreen(
                categories = categories,
                selectedCategory = selectedCategory,
                diaryText = diaryText,
                onCategorySelect = { viewModel.selectCategory(it) },
                onDiaryTextUpdate = { viewModel.updateDiaryText(it) },
                onEditLocation = { currentLocation ->
                    onLocationChange(currentLocation) // ✅ 위치 변경 반영
                    navController.navigate("record_edit_screen") // ✅ 네비게이션 이동
                }
            )
        }
        composable("record_edit_screen") {
            RecordEditLocationScreen(
                mapView = viewModel.getMapView(),
                onLocationButtonClicked = {
                    println("🔴 Returning to RecordScreen")
                    navController.popBackStack() // ✅ 뒤로 가기 (RecordScreen으로)
                },
                location = location // ✅ location 값 전달
            )
        }
    }
}