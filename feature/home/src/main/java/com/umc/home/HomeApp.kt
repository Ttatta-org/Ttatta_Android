package com.umc.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.umc.home.HomeScreen
import com.umc.home.navigation.AppNavHost

@OptIn(UnstableApi::class)
@Composable
fun HomeApp(viewModel: HomeViewModel) {

    // HomeViewModel의 상태는 mutableStateOf로 관리되고 있으므로,
    // 예를 들어 diaryList와 searchResults는 viewModel.diaryList, viewModel.searchResults로 읽어옵니다.
    // 검색어는 viewModel.searchQuery.value를 읽거나 쓸 수 있습니다.

    // ✅ 앱이 실행될 때 자동으로 전체 다이어리 로드
    LaunchedEffect(Unit) {
        viewModel.loadDiaries(page = 0,isFiltered = false, date = null)
        //viewModel.loadAllDiaries()
        viewModel.loadAllRecordedDates()
    }

    val navController = rememberNavController()

    AppNavHost(navController = navController, viewModel = viewModel)

}