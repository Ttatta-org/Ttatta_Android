package com.umc.data

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.umc.core.repository.DiaryRepository
import com.umc.core.repository.UserRepository
import com.umc.data.di.AuthPreferenceModule
import com.umc.data.di.DiaryRepositoryModule
import com.umc.data.di.GsonModule
import com.umc.data.di.ServerApiModule
import com.umc.data.di.UserRepositoryModule
import com.umc.design.CategoryColor
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

private const val TEST_ID = "tester_kim"
private const val TEST_PASSWORD = "test1234"
private const val TEST_NAME = "kim"
private const val TEST_NICKNAME = "tester"
private const val TEST_EMAIL = "tester_kim@test.com"
private val TEST_TODAY = LocalDateTime.parse("2025-01-29T01:39:42.814468")
private const val TEST_CONTENT = "서울시청의 한 사진입니다."
private const val TEST_LATITUDE = 37.566535
private const val TEST_LONGITUDE = 126.9779692
private const val TEST_CATEGORY_NAME = "test category"
private val TEST_CATEGORY_COLOR = CategoryColor.NAVY

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ExampleInstrumentedTest {

    private lateinit var context: Context
    private lateinit var gson: Gson
    private lateinit var userRepository: UserRepository
    private lateinit var diaryRepository: DiaryRepository

    @Before
    fun prepareTest() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        gson = GsonModule.provideGson()
        val authPreference = AuthPreferenceModule.provideAuthPreference(context)
        val serverApi = ServerApiModule.provideServerApi(authPreference, gson)
        userRepository = UserRepositoryModule.provideUserRepository(serverApi, authPreference)
        diaryRepository = DiaryRepositoryModule.provideDiaryRepository(serverApi, authPreference)
    }

    @Test
    fun test01_Join() = runTest {
        userRepository.join(
            id = TEST_ID,
            password = TEST_PASSWORD,
            name = TEST_NAME,
            nickname = TEST_NICKNAME,
            email = TEST_EMAIL,
        )
    }

    @Test
    fun test02_AccessingMyInfo() = runTest {
        login()

        val myInfo = userRepository.getUserInfo()
        userRepository.modifyUserInfo(myInfo.copy(name = "hello"))
        val modifiedInfo = userRepository.getUserInfo()
        userRepository.modifyUserInfo(myInfo)
        val recoveredInfo = userRepository.getUserInfo()
        assert(myInfo.name == recoveredInfo.name && myInfo.name != modifiedInfo.name)

        logout()
    }

    @Test
    fun test03_CheckingExistingId() = runTest {
        val isExist = userRepository.isIdAlreadyOccupied(TEST_ID)
        assert(isExist)
    }

    @Test
    fun test04_Diary() = runTest {
        login()

        // 일기 업로드
        val randomCategory = diaryRepository.getAllCategoryInfo().random()
        uploadDiary(randomCategory.id)

        // 일기 조회
        println(
            diaryRepository.getDiaries(
                page = 0,
                date = TEST_TODAY.toLocalDate(),
            )
        )
        // println(
        //     diaryRepository.getDiaries(
        //         page = 0,
        //         searchWord = TEST_CONTENT.substring(0 until 5),
        //     )
        // )
        // println(
        //     diaryRepository.getDiaries(
        //         page = 0,
        //         latitude = TEST_LATITUDE,
        //         longitude = TEST_LONGITUDE,
        //     )
        // )

        // 일기 수정
        val originalDiary = diaryRepository.getDiaries(
            page = 0,
            date = TEST_TODAY.toLocalDate(),
        ).first()
        diaryRepository.modifyDiary(
            diaryId = originalDiary.id,
            content = "modified"
        )
        val modifiedDiary = diaryRepository.getDiaries(
            page = 0,
            date = TEST_TODAY.toLocalDate(),
        ).first()
        assert(originalDiary.content != modifiedDiary.content)

        // 일기 삭제
        diaryRepository.deleteDiary(originalDiary.id)

        logout()
    }

    @Test
    fun test05_Category() = runTest {
        login()

        // 카테고리 조회
        val originalCategories = diaryRepository.getAllCategoryInfo()

        // 카테고리 생성
        diaryRepository.createCategory(
            name = TEST_CATEGORY_NAME,
            color = TEST_CATEGORY_COLOR,
        )
        val newCategory = diaryRepository.getAllCategoryInfo().find {
            it.name == TEST_CATEGORY_NAME && it.color == TEST_CATEGORY_COLOR
        }!!

        // 카테고리 수정
        diaryRepository.modifyCategory(
            categoryId = newCategory.id,
            name = "modified category",
            color = CategoryColor.RED,
        )
        val modifiedCategory = diaryRepository.getAllCategoryInfo().find {
            it.id == newCategory.id
        }!!
        assert(newCategory.name != modifiedCategory.name)

        // 카테고리 삭제
        diaryRepository.deleteCategory(modifiedCategory.id)
        val categories = diaryRepository.getAllCategoryInfo()
        assert(originalCategories.map { it.id }.sorted() == categories.map { it.id }.sorted())

        logout()
    }

    @Test
    fun test06_DeletingCategoryAndAllIncludedDiaries() = runTest {
        login()

        // 전체 일기 조회
        val originalDiaries = diaryRepository.getDiaries(
            page = 0,
            date = TEST_TODAY.toLocalDate()
        )

        // 카테고리 생성
        diaryRepository.createCategory(
            name = TEST_CATEGORY_NAME,
            color = TEST_CATEGORY_COLOR,
        )
        val newCategory = diaryRepository.getAllCategoryInfo().find {
            it.name == TEST_CATEGORY_NAME && it.color == TEST_CATEGORY_COLOR
        }!!

        // 일기 업로드
        repeat(3) { uploadDiary(newCategory.id) }

        // 카테고리 및 하위 일기 삭제
        diaryRepository.deleteCategoryAndAllIncludedDiaries(newCategory.id)
        val diaries = diaryRepository.getDiaries(
            page = 0,
            date = TEST_TODAY.toLocalDate()
        )
        assert(originalDiaries.map { it.id }.sorted() == diaries.map { it.id }.sorted())

        logout()
    }

    @Test
    fun test07_Quit() = runTest {
        login()
        val myInfo = userRepository.getUserInfo()
        userRepository.leaveUser()
        assert(!userRepository.isIdAlreadyOccupied(id = myInfo.name))
    }

    private suspend fun login() {
        userRepository.login(
            id = TEST_ID,
            password = TEST_PASSWORD,
        )
    }

    private suspend fun logout() {
        userRepository.logout()
    }

    private suspend fun uploadDiary(categoryId: Long) {
        diaryRepository.createDiary(
            categoryId = categoryId,
            date = TEST_TODAY,
            content = TEST_CONTENT,
            image = File(
                context.cacheDir,
                "test_image.jpg"
            ).apply {
                FileOutputStream(this).use {
                    context.resources.openRawResource(R.raw.img_seoul_city_hall).copyTo(it)
                }
            },
            latitude = TEST_LATITUDE,
            longitude = TEST_LONGITUDE,
            locationName = "서울시청",
        )
    }
}