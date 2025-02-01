package com.umc.login.Join

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umc.login.AnimatedProgressBar
import com.umc.login.R

@Composable
fun JoinParentScreen(navController: NavHostController) {
    val localNavController = rememberNavController()
    var currentStep by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        JoinBackButton(onBack = {
            if (localNavController.previousBackStackEntry != null) {
                localNavController.popBackStack()
            } else {
                navController.popBackStack()
            }
        })
        JoinTopView(currentStep = currentStep, totalSteps = 5)
        NavHost(
            navController = localNavController,
            startDestination = "nickname",
            modifier = Modifier.weight(1f)
        ) {
            composable("nickname") {
                LaunchedEffect(Unit) { currentStep = 1 }
                JoinNicknameView(onNext = { localNavController.navigate("id") })
            }
            composable("id") {
                LaunchedEffect(Unit) { currentStep = 2 }
                JoinIdView(
                    onNext = { localNavController.navigate("password") },
                    onBack = { localNavController.popBackStack() }
                )
            }
            composable("password") {
                LaunchedEffect(Unit) { currentStep = 3 }
                JoinPwView(
                    onNext = { localNavController.navigate("name") })
            }
            composable("name") {
                LaunchedEffect(Unit) { currentStep = 4 }
                JoinNameView(
                    onNext = {localNavController.navigate("email")}
                )
            }
            composable("email") {
                LaunchedEffect(Unit) { currentStep = 5 }
                JoinEmailView(
                    onNext = {localNavController.navigate("certification")},
                    onBack = {}
                )
            }
            composable("certification") {
                LaunchedEffect(Unit) { currentStep = 5 }
                JoinCertiView(
                    onNext = {},
                    onBack = {}
                )
            }
        }
    }
}

@Composable
fun JoinBackButton(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 50.dp, start = 30.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_back),
            modifier = Modifier
                .size(15.dp, 21.dp)
                .clickable { onBack() },
            contentScale = ContentScale.None,
            contentDescription = "back_button"
        )
    }
}

@Composable
fun JoinTopView(currentStep: Int, totalSteps: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_ttatta_logo),
            modifier = Modifier.size(55.35961.dp, 48.00011.dp),
            contentDescription = "main_logo_join"
        )
        Spacer(modifier = Modifier.height(35.dp))
        AnimatedProgressBar(currentStep = currentStep, totalSteps = totalSteps)
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewJoinParentScreen() {
    JoinParentScreen(navController = NavHostController(LocalContext.current))
}
