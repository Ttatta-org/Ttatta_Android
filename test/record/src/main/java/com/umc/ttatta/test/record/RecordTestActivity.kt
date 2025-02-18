package com.umc.ttatta.test.record

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.umc.core.repository.DiaryRepository
import com.umc.core.repository.UserRepository
import com.umc.design.CategoryColor
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
    @Inject
    lateinit var diaryRepository: DiaryRepository

    private val viewModel: RecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prepareTest()

        enableEdgeToEdge()
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets(0))
                    .background(Color.White)
            ) {
                RecordApp(
                    viewModel = viewModel,
                    mode = RecordMode.CAMERA,
                    onBack = { finish() },
                    onNavigateToCategoryApp = {},
                    onDone = { finish() }
                )
            }
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

                diaryRepository.createCategory(
                    name = "테스트 1",
                    color = CategoryColor.RED
                )
                diaryRepository.createCategory(
                    name = "테스트 2",
                    color = CategoryColor.GREEN
                )
                diaryRepository.createCategory(
                    name = "테스트 3",
                    color = CategoryColor.NAVY
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