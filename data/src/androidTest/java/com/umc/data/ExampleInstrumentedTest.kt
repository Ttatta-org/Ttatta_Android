package com.umc.data

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.umc.core.repository.DiaryRepository
import com.umc.core.repository.UserRepository
import com.umc.data.di.preference.AuthPreferenceModule
import com.umc.data.di.repository.DiaryRepositoryModule
import com.umc.data.di.GsonModule
import com.umc.data.di.api.ServerApiModule
import com.umc.data.di.repository.UserRepositoryModule
import com.umc.design.CategoryColor
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.File
import java.io.FileOutputStream

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */



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
            id = TestValue.ID,
            password = TestValue.PASSWORD,
            name = TestValue.NAME,
            nickname = TestValue.NICKNAME,
            email = TestValue.EMAIL,
        )
    }

    @Test
    fun test02_AccessingMyInfo() = runTest {
        login()

        val myInfo = userRepository.getUserInfo()
        userRepository.modifyUserInfo(name = "hello")
        val modifiedInfo = userRepository.getUserInfo()
        userRepository.modifyUserInfo(name = myInfo.name)
        val recoveredInfo = userRepository.getUserInfo()
        assert(myInfo.name == recoveredInfo.name && myInfo.name != modifiedInfo.name)

        logout()
    }

    @Test
    fun test03_CheckingExistingId() = runTest {
        val isExist = userRepository.isIdAlreadyOccupied(TestValue.ID)
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
                date = TestValue.TODAY.toLocalDate(),
            )
        )
        println(
            diaryRepository.getDiaries(
                page = 0,
                searchWord = TestValue.CONTENT.substring(0 until 5),
            )
        )

        // 발자국 조회
        val footprints = diaryRepository.getAllFootprints()
        println(
            diaryRepository.getDiaries(
                page = 0,
                clusterId = footprints.first().clusterId
            )
        )

        // 일기 수정
        val originalDiary = diaryRepository.getDiaries(
            page = 0,
            date = TestValue.TODAY.toLocalDate(),
        ).first()
        diaryRepository.modifyDiary(
            diaryId = originalDiary.id,
            content = "modified"
        )
        val modifiedDiary = diaryRepository.getDiaries(
            page = 0,
            date = TestValue.TODAY.toLocalDate(),
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
            name = TestValue.CATEGORY_NAME,
            color = TestValue.CATEGORY_COLOR,
        )
        val newCategory = diaryRepository.getAllCategoryInfo().find {
            it.name == TestValue.CATEGORY_NAME && it.color == TestValue.CATEGORY_COLOR
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
            date = TestValue.TODAY.toLocalDate()
        )

        // 카테고리 생성
        diaryRepository.createCategory(
            name = TestValue.CATEGORY_NAME,
            color = TestValue.CATEGORY_COLOR,
        )
        val newCategory = diaryRepository.getAllCategoryInfo().find {
            it.name == TestValue.CATEGORY_NAME && it.color == TestValue.CATEGORY_COLOR
        }!!

        // 일기 업로드
        repeat(3) { uploadDiary(newCategory.id) }

        // 카테고리 및 하위 일기 삭제
        diaryRepository.deleteCategoryAndAllIncludedDiaries(newCategory.id)
        val diaries = diaryRepository.getDiaries(
            page = 0,
            date = TestValue.TODAY.toLocalDate()
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
            id = TestValue.ID,
            password = TestValue.PASSWORD,
        )
    }

    private suspend fun logout() {
        userRepository.logout()
    }

    private suspend fun uploadDiary(categoryId: Long) {
        diaryRepository.createDiary(
            categoryId = categoryId,
            date = TestValue.TODAY,
            content = TestValue.CONTENT,
            image = File(
                context.cacheDir,
                "test_image.jpg"
            ).apply {
                FileOutputStream(this).use {
                    context.resources.openRawResource(R.raw.img_seoul_city_hall).copyTo(it)
                }
            },
            latitude = TestValue.LATITUDE,
            longitude = TestValue.LONGITUDE,
            locationName = "서울시청",
        )
    }
}