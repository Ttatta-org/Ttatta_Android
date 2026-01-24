package com.umc.record

import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.umc.record.navigation.editlocation.EditLocationNavGraph.addEditLocationNavGraph
import com.umc.record.navigation.record.RecordNavGraph
import com.umc.record.navigation.record.RecordNavGraph.addRecordNavGraph
import com.umc.record.navigation.record.RecordViewModel
import java.io.File

@Composable
fun RecordApp(
    viewModel: RecordViewModel,
    image: File?,
    onBackToHome: () -> Unit,
    onNavigateToCategoryApp: () -> Unit,
    onDone: () -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RecordNavGraph.Route,
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        addRecordNavGraph(
            viewModel = viewModel,
            navController = navController,
            image = image,
            onBackToHome = onBackToHome,
            onNavigateToCategoryApp = onNavigateToCategoryApp,
            onDiaryUploadDone = onDone,
        )

        addEditLocationNavGraph(
            navController = navController,
        )
    }
}
