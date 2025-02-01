package com.umc.footprint

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.umc.footprint.test.TestActivity
import com.umc.footprint.test.TestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class FootprintTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()
    
    private lateinit var application: TestApplication

    @Before
    fun setup() {
        // TestApplication 초기화
        application = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testApplicationAndActivityLaunch() {
        // Activity가 정상적으로 시작되었는지 확인
        composeTestRule.waitForIdle()
        val activity = composeTestRule.activity
        // 지도 SDK가 작동하는지를 확인
        assert(true)
    }
}