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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.umc.record.RecordEditLocationScreen
import com.umc.record.RecordScreen
import com.umc.record.RecordViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@AndroidEntryPoint
class RecordTestActivity : ComponentActivity() {
    private val viewModel: RecordViewModel by viewModels()
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets(0))
                    .background(Color.White)
            ) {
                RecordNavHost(navController, viewModel)
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


@Composable
fun RecordNavHost(
    navController: NavHostController,
    viewModel: RecordViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "record_screen"
    ) {
        composable("record_screen") {
            val categories by viewModel.categories.collectAsState()
            val selectedCategory by viewModel.selectedCategory.collectAsState()
            val diaryText by viewModel.diaryText.collectAsState()

            RecordScreen(
                categories = categories,
                selectedCategory = selectedCategory,
                diaryText = diaryText,
                onCategorySelect = { viewModel.selectCategory(it) },
                onDiaryTextUpdate = { viewModel.updateDiaryText(it) },
                onEditLocation = { navController.navigate("record_edit_screen") } // ✅ 네비게이션 이동
            )
        }
        composable("record_edit_screen") {
            RecordEditLocationScreen(
                mapView = viewModel.getMapView(),
                onLocationButtonClicked = {
                    println("🔴 Returning to RecordScreen")
                    navController.popBackStack() // ✅ 뒤로 가기 (RecordScreen으로)
                }
            )
        }
    }
}