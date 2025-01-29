package com.umc.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.umc.home.FilteredDiaryScreen
import com.umc.home.HomeScreen
import com.umc.home.HomeViewModel
import java.time.LocalDate
import androidx.navigation.compose.composable


@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: HomeViewModel
) {
    NavHost(navController = navController, startDestination = "home") {
        // Home 화면
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onFabClick = { /* 플로팅 버튼 동작 */ },
                onCalendarToggle = { /* 캘린더 토글 */ },
                onNavigateToFilteredDiaryScreen = { selectedDate ->
                    navController.navigate("filtered/${selectedDate}")
                }
            )
        }
        // Filtered Diary 화면
        composable(
            route = "filtered/{selectedDate}",
            arguments = listOf(navArgument("selectedDate") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedDate = LocalDate.parse(backStackEntry.arguments?.getString("selectedDate"))
            val uiState by viewModel.uiState.collectAsState() // StateFlow를 Compose에서 안전하게 사용

            FilteredDiaryScreen(
                viewModel = viewModel,
                selectedDate = selectedDate,
                diaries = uiState.diaries, // collectAsState를 통해 가져온 diaries
                onBack = { navController.popBackStack() },
                onFabClick = { /* 플로팅 버튼 동작 */ }
            )
        }

    }
}
