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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.umc.record.RecordViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.umc.record.RecordApp


@AndroidEntryPoint
class RecordTestActivity : ComponentActivity() {
    private val viewModel: RecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets(0))
                    .background(Color.White)
            ) {
                RecordApp(viewModel, onNavigateToCategoryApp = { /* TODO: Add navigation logic */ })
            }
        }
    }

    // 📌 위치 권한 요청 및 이동 처리 함수
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            println("✅ Location permission granted!")
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
                println("✅ Location permission granted!")
                viewModel.moveMapToCurrentPosition()
            } else {
                println("🚨 Location permission denied!")
            }
        }
}