package com.umc.home.test

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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


        // ✅ 테스트
        prepareTest()


        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            HomeApp(viewModel = viewModel)
        }
    }

    private fun prepareTest() {
        CoroutineScope(Dispatchers.IO).launch {
            // 1. 사용자 가입 및 로그인
            if (!userRepository.isIdAlreadyOccupied(id = TestValues.ID)) {
                userRepository.join(
                    id = TestValues.ID,
                    password = TestValues.PASSWORD,
                    name = TestValues.NAME,
                    nickname = TestValues.NICKNAME,
                    email = TestValues.EMAIL
                )
            }

            userRepository.login(
                id = TestValues.ID,
                password = TestValues.PASSWORD
            )

            // 2. diaryRepository를 이용하여 일기 관련 작업 수행

            // 2.1. 카테고리 조회 (없으면 생성)
            var categories = diaryRepository.getAllCategoryInfo()
            val categoryId = if (categories.isNotEmpty()) {
                categories.first().id
            } else {
                // 카테고리 생성 (예: 이름 "TestCategory", 색상 BLUE)
                diaryRepository.createCategory("TestCategory", CategoryColor.BLUE)
                // 새로 생성된 카테고리를 다시 조회
                categories = diaryRepository.getAllCategoryInfo()
                categories.first { it.name == "TestCategory" }.id
            }

            // 2.2. 테스트용 이미지 파일 준비
            val imageFile = File(cacheDir, "test_image.jpg")
            if (!imageFile.exists()) {
                FileOutputStream(imageFile).use { out ->
                    // resources에서 테스트용 이미지를 복사 (예: R.raw.img_seoul_city_hall)
                    resources.openRawResource(com.umc.data.R.raw.img_seoul_city_hall).copyTo(out)
                }
            }

            // 2.3. 일기 생성 (날짜, 내용, 위치 등은 TestValues에 정의되어 있다고 가정)
            diaryRepository.createDiary(
                categoryId = categoryId,
                date = TestValues.TODAY,       // TODAY는 LocalDateTime 타입이어야 함
                content = TestValues.CONTENT,
                image = imageFile,
                latitude = TestValues.LATITUDE,
                longitude = TestValues.LONGITUDE,
                locationName = "서울시청"
            )

            // 2.4. 일기 불러오기 (예: 오늘 날짜의 일기 목록 조회)
            val diaries = diaryRepository.getDiaries(
                page = 0,
                date = TestValues.TODAY.toLocalDate()  // LocalDateTime에서 LocalDate로 변환
            )
            Log.d("TestActivity", "Diaries retrieved: $diaries")

            // 2.5. 생성된 일기가 한 건 이상 존재하는지 확인
            assert(diaries.isNotEmpty()) { "No diaries found after creation" }
        }
    }

//    private suspend fun testDiary() {
//        login()
//
//        // 일기 업로드
//        val randomCategory = diaryRepository.getAllCategoryInfo().random()
//        uploadDiary(randomCategory.id)
//
//        // 일기 조회
//        println(
//            diaryRepository.getDiaries(
//                page = 0,
//                date = TestValues.TODAY.toLocalDate(),
//            )
//        )
//        println(
//            diaryRepository.getDiaries(
//                page = 0,
//                searchWord = TestValues.CONTENT.substring(0 until 5),
//            )
//        )
//        // 일기 수정
//        val originalDiary = diaryRepository.getDiaries(
//            page = 0,
//            date = TestValues.TODAY.toLocalDate(),
//        ).first()
//        diaryRepository.modifyDiary(
//            diaryId = originalDiary.id,
//            content = "modified"
//        )
//        val modifiedDiary = diaryRepository.getDiaries(
//            page = 0,
//            date = TestValues.TODAY.toLocalDate(),
//        ).first()
//        assert(originalDiary.content != modifiedDiary.content)
//
//        // 일기 삭제
//        diaryRepository.deleteDiary(originalDiary.id)
//
//        logout()
//    }
//
//
//    private suspend fun login() {
//        userRepository.login(
//            id = TestValues.ID,
//            password = TestValues.PASSWORD,
//        )
//    }
//
//    private suspend fun logout() {
//        userRepository.logout()
//    }
//
//    private suspend fun uploadDiary(categoryId: Long) {
//        diaryRepository.createDiary(
//            categoryId = categoryId,
//            date = TestValues.TODAY,
//            content = TestValues.CONTENT,
//            image = File(
//                cacheDir, // Activity 내에서는 바로 사용 가능
//                "test_image.jpg"
//            ).apply {
//                FileOutputStream(this).use {
//                    resources.openRawResource(com.umc.data.R.raw.img_seoul_city_hall).copyTo(it)
//                }
//            },
//            latitude = TestValues.LATITUDE,
//            longitude = TestValues.LONGITUDE,
//            locationName = "서울시청",
//        )
//    }

}