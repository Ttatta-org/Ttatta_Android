package com.umc.record.test

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.umc.record.RecordApp
import com.umc.record.RecordEditLocationScreen
import com.umc.record.RecordScreen
import com.umc.record.RecordViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@AndroidEntryPoint
class RecordTestActivity : ComponentActivity() {
    private val viewModel: RecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
//            val navController = rememberNavController() //  NavController 생성
//            RecordNavHost(navController, viewModel)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets(0))
                    .background(Color.White)
            ) {
                val categories = viewModel.categories.collectAsState().value
                val selectedCategory = viewModel.selectedCategory.collectAsState().value
                val diaryText = viewModel.diaryText.collectAsState().value

                RecordApp(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    diaryText = diaryText,
                    onCategorySelect = { viewModel.selectCategory(it) },
                    onDiaryTextUpdate = { viewModel.updateDiaryText(it) },
                    mapView = viewModel.getMapView(),
                    onEditLocation = { checkLocationPermission() } // ✅ 위치 권한 체크 후 실행
                )
            }
        }
    }

    // 📌 위치 권한 요청 함수 추가
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            println("✅ Location permission granted! Navigating to RecordEditLocationScreen")
            viewModel.moveMapToCurrentPosition()
        } else {
            println("🚨 Location permission NOT granted! Requesting permission...")
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // 📌 위치 권한 요청 실행
    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                println("✅ Location permission granted! Navigating to RecordEditLocationScreen")
                viewModel.moveMapToCurrentPosition()
            } else {
                println("🚨 Location permission denied!")
            }
        }
}


//@Composable
//fun RecordNavHost(
//    navController: NavController,
//    viewModel: RecordViewModel
//) {
//    NavHost(
//        navController = navController,
//        startDestination = "record_screen"
//    ) {
//        composable("record_screen") {
//            RecordScreen(
//                categories = viewModel.categories.collectAsState().value,
//                selectedCategory = viewModel.selectedCategory.collectAsState().value,
//                diaryText = viewModel.diaryText.collectAsState().value,
//                onCategorySelect = { viewModel.selectCategory(it) },
//                onDiaryTextUpdate = { viewModel.updateDiaryText(it) },
//                onEditLocation = { navController.navigate("record_edit_screen") } // 위치 클릭 시 이동
//            )
//        }
//        composable("record_edit_screen") {
//            RecordEditLocationScreen(
//                mapView = viewModel.getMapView(),
//                onLocationButtonClicked = { viewModel.moveMapToCurrentPosition() }
//            )
//        }
//    }
//}