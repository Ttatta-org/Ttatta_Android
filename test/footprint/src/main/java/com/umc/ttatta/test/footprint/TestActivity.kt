package com.umc.ttatta.test.footprint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umc.category.CategoryApp
import com.umc.core.repository.DiaryRepository
import com.umc.core.repository.UserRepository
import com.umc.design.BottomNavigationBar
import com.umc.design.CategoryColor
import com.umc.design.NavigationItem
import com.umc.footprint.FootprintApp
import com.umc.footprint.FootprintViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class TestActivity : ComponentActivity() {
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var diaryRepository: DiaryRepository

    private val viewModel: FootprintViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareTest()

        enableEdgeToEdge()
        setContent {
            val navigator = rememberNavController()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                NavHost(
                    navController = navigator,
                    startDestination = "footprint",
                    modifier = Modifier.weight(1f)
                ) {
                    composable(
                        route = "footprint"
                    ) {
                        FootprintApp(
                            viewModel = viewModel,
                            onNavigateToCategoryApp = {
                                navigator.navigate("category")
                            }
                        )
                    }

                    composable(
                        route = "category"
                    ) {
                        CategoryApp(
                            viewModel = hiltViewModel(),
                            showTopBar = true,
                        )
                    }
                }
                BottomNavigationBar(
                    selectedTab = NavigationItem.FOOTPRINT,
                    onTabSelected = {},
                    onFabClick = {}
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
                    name = "test category 1",
                    color = CategoryColor.NAVY,
                )

                diaryRepository.createCategory(
                    name = "test category 2",
                    color = CategoryColor.GREEN,
                )
                
                diaryRepository.getAllCategoryInfo().forEach { category ->
                    repeat(3) { index ->
                        val place = TestValues.PLACE[index]
                        diaryRepository.createDiary(
                            categoryId = category.id,
                            date = LocalDateTime.now().minusMonths(index.toLong()),
                            content = "test content $index",
                            image = File(
                                cacheDir,
                                "test_image_${place.name}.jpg"
                            ).apply {
                                FileOutputStream(this).use {
                                    resources.openRawResource(place.imageId).copyTo(it)
                                }
                            },
                            latitude = place.latitude,
                            longitude = place.longitude,
                            locationName = place.name
                        )
                    }
                }

                userRepository.logout()
            }

            userRepository.login(
                id = TestValues.ID,
                password = TestValues.PASSWORD
            )
        }
    }
}