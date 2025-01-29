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
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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
private val TEST_TODAY = LocalDateTime.parse("2025-01-29T01:39:42.814468")

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var context: Context
    private lateinit var gson: Gson
    private lateinit var userRepository: UserRepository
    private lateinit var diaryRepository: DiaryRepository

    @Before
    fun setRepositories() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        gson = GsonModule.provideGson()
        val authPreference = AuthPreferenceModule.provideAuthPreference(context)
        val serverApi = ServerApiModule.provideServerApi(authPreference, gson)
        userRepository = UserRepositoryModule.provideUserRepository(serverApi, authPreference)
        diaryRepository = DiaryRepositoryModule.provideDiaryRepository(serverApi, authPreference)
    }

    @Test
    fun testJoin() = runTest {
        userRepository.join(
            id = TEST_ID,
            password = TEST_PASSWORD,
            name = "kim",
            nickname = "tester",
            email = "tester_kim@test.com",
        )
        assert(true)
    }

    private suspend fun login() {
        userRepository.login(
            id = TEST_ID,
            password = TEST_PASSWORD,
        )
    }

    @Test
    fun testLogin() = runTest {
        login()
        assert(true)
    }

    @Test
    fun testPostingDiary() = runTest {
        val image = File(
            context.cacheDir,
            "test_image.jpg"
        ).apply {
            FileOutputStream(this).use {
                context.resources.openRawResource(R.raw.img_seoul_city_hall).copyTo(it)
            }
        }

        login()
        val randomCategory = diaryRepository.getAllCategoryInfo().random()
        diaryRepository.createDiary(
            categoryId = randomCategory.id,
            date = TEST_TODAY,
            content = "test",
            image = image,
            latitude = 37.566535,
            longitude = 126.9779692,
            locationName = "서울시청",
        )
        assert(true)
    }

    @Test
    fun testGettingDiary() = runTest {
        login()
        val diary = diaryRepository.getDiaries(
            page = 0,
            date = null,
        ).first()
        println(diary)
        assert(true)
    }
}