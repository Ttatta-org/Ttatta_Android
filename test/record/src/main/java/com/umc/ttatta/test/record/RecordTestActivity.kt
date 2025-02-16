package com.umc.ttatta.test.record

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
import com.umc.core.repository.UserRepository
import com.umc.record.RecordApp
import com.umc.record.RecordMode
import com.umc.record.RecordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class RecordTestActivity : ComponentActivity() {
    @Inject
    lateinit var userRepository: UserRepository

    private val viewModel: RecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        // `intent`를 통해 모드 설정 (기본값은 GALLERY)
        val mode = intent?.getSerializableExtra("RECORD_MODE") as? RecordMode ?: RecordMode.GALLARY

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets(0))
                    .background(Color.White)
            ) {
                RecordApp(
                    viewModel = viewModel,
                    mode = mode,  // 모드 전달
                    onNavigateToCategoryApp = { /* TODO: Add navigation logic */ }
                )
            }
        }

        // ✅ 테스트 데이터 준비
        prepareTest()

        // ✅ 위치 권한 확인 및 요청
        checkLocationPermission()
    }

    // 📌 위치 권한 요청 및 이동 처리 함수
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            println("✅ Location permission granted!")
            viewModel.moveMapToCurrentPosition(
                onSucceed = { /* TODO */ },
                onFailed = { /* TODO */ },
            )
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
                viewModel.moveMapToCurrentPosition(
                    onSucceed = { /* TODO */ },
                    onFailed = { /* TODO */ },
                )
            } else {
                println("🚨 Location permission denied!")
            }
        }

    private fun prepareTest() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!userRepository.isIdAlreadyOccupied(id = TestValues.ID)) {
                userRepository.join(
                    id = TestValues.ID,
                    password = TestValues.PASSWORD,
                    name = TestValues.NAME,
                    nickname = TestValues.NICKNAME,
                    email = TestValues.EMAIL
                )

                userRepository.login(
                    id = TestValues.ID,
                    password = TestValues.PASSWORD
                )

                userRepository.logout()
            }

            userRepository.login(
                id = TestValues.ID,
                password = TestValues.PASSWORD
            )
        }
    }
}