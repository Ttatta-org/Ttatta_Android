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
                Log.d("CustomCategoryField", "📌 현재 카테고리 리스트: $categories")
                val categoryId = categories.find { it.name == "남자친구" }?.id ?: run {
                    Log.d("CustomCategoryField", "🚀 '남자친구' 카테고리가 없음. 새로 생성 중...")
                    // ✅ "남자친구" 카테고리가 없으면 새로 생성
                    diaryRepository.createCategory("남자친구", CategoryColor.BLUE)

                    // ✅ 다시 카테고리 조회 후 "남자친구"의 ID 가져오기
                    categories = diaryRepository.getAllCategoryInfo()
                    Log.d("CustomCategoryField", "📌 카테고리 재조회 후 리스트: $categories")

                    categories.find { it.name == "남자친구" }?.id
                        ?: error("❌ '남자친구' 카테고리 생성 실패")
                }
                Log.d("CustomCategoryField", "✅ 선택된 카테고리 ID: $categoryId")

                // ✅ 테스트용 이미지 파일 준비
                val imageFile = File(cacheDir, "test_rabbit.png")
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
                diaryRepository.createDiary(
                    categoryId = categoryId,
                    date = TestValues.TODAY,
                    content = TestValues.CONTENT,
                    image = imageFile,
                    latitude = TestValues.LATITUDE,
                    longitude = TestValues.LONGITUDE,
                    locationName = "카테고리확인용"
                )

                // ✅ ViewModel에서 데이터 로드
                withContext(Dispatchers.Main) {
                    viewModel.loadDiaries(page = 0,isFiltered = false, date = null)
                    //viewModel.loadAllDiaries()
                    viewModel.loadAllRecordedDates()
                }
            } catch (e: Exception) {
                Log.e("TestActivity", "❌ prepareTest() 실패: ${e.message}")
            }
        }
    }
}