package com.umc.ttatta.test.home

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.umc.core.repository.DiaryRepository
import com.umc.core.repository.UserRepository
import com.umc.design.CategoryColor
import com.umc.home.HomeApp
import com.umc.home.HomeViewModel
import com.umc.home.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class TestActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var diaryRepository: DiaryRepository

    private val viewModel: HomeViewModel by viewModels()

    //private val testViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            HomeApp(viewModel = viewModel)
        }
        // ✅ 테스트
        prepareTest()
    }

    private fun prepareTest() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // ✅ 사용자 로그인 (회원가입 체크 후 로그인)
                if (!userRepository.isIdAlreadyOccupied(id = TestValues.ID)) {
                    userRepository.join(
                        id = TestValues.ID,
                        password = TestValues.PASSWORD,
                        name = TestValues.NAME,
                        nickname = TestValues.NICKNAME,
                        email = TestValues.EMAIL
                    )
                }
                userRepository.login(id = TestValues.ID, password = TestValues.PASSWORD)

                // ✅ 카테고리 조회 (없으면 생성)
                var categories = diaryRepository.getAllCategoryInfo()
                val categoryId = if (categories.isNotEmpty()) {
                    categories.first().id
                } else {
                    diaryRepository.createCategory("TestCategory", CategoryColor.BLUE)
                    categories = diaryRepository.getAllCategoryInfo()
                    categories.first { it.name == "TestCategory" }.id
                }

                // ✅ 테스트용 이미지 파일 준비
                val imageFile = File(cacheDir, "test_letter.jpg")
                if (!imageFile.exists()) {
                    try {
                        FileOutputStream(imageFile).use { out ->
                            resources.openRawResource(com.umc.data.R.raw.img_cafe).copyTo(out)
                            Log.d("TestActivity", "♦️ image파일 말고 raw파일 사용")
                        }
                    } catch (e: Exception) {
                        Log.e("TestActivity", "❌ 이미지 파일 복사 실패: ${e.message}")
                    }
                }

                // ✅ 기존 일기 개수 확인
                val existingDiaries = diaryRepository.getDiaries(page = 0, date = null)
                Log.d("TestActivity", "🔍 기존 일기 개수: ${existingDiaries.size}")

                // ✅ 새로운 일기 추가
//                diaryRepository.createDiary(
//                    categoryId = categoryId,
//                    date = TestValues.TODAY,
//                    content = TestValues.CONTENT,
//                    image = imageFile,
//                    latitude = TestValues.LATITUDE,
//                    longitude = TestValues.LONGITUDE,
//                    locationName = "어딘가 어딘가 어딘가"
//                )

                // ✅ ViewModel에서 데이터 로드
//                withContext(Dispatchers.Main) {
//                    viewModel.loadDiaries(page = 0,isFiltered = false, date = null)
//                    //viewModel.loadAllDiaries()
//                    viewModel.loadAllRecordedDates()
//                }
            } catch (e: Exception) {
                Log.e("TestActivity", "❌ prepareTest() 실패: ${e.message}")
            }
        }
    }
}