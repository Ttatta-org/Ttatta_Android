package com.umc.login

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun JoinParentScreen(navController: NavHostController) {
    val navController = rememberNavController()

    var currentStep by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BackButton(navController)
        JoinTopView(currentStep = currentStep, totalSteps = 5)
        NavHost(
            navController = navController,
            startDestination = "nickname",
            modifier = Modifier.weight(1f)
        ) {
            composable("back") {
                // 화면 진입 시 step=1로 설정하여 ProgressBar 25% 표시
                LaunchedEffect(Unit) {
                    // (선택) 약간 delay를 줘서 "초기 0% → 25%" 애니메이션을 볼 수도 있음
                    // delay(100)
                    currentStep = currentStep -1
                }
                JoinBackButton(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("nickname") {
                // 화면 진입 시 step=1로 설정하여 ProgressBar 25% 표시
                LaunchedEffect(Unit) {
                    // (선택) 약간 delay를 줘서 "초기 0% → 25%" 애니메이션을 볼 수도 있음
                    // delay(100)
                    currentStep = 1
                }
                JoinNicknameView(
                    onNext = {
                        // 다음 화면으로 이동
                        navController.navigate("id")
                    }
                )
            }
            composable("id") {
                LaunchedEffect(Unit) {
                    currentStep = 2
                }
                JoinIdView(
                    onNext = {
                        navController.navigate("password")
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("password") {
                LaunchedEffect(Unit) {
                    currentStep = 3
                }
                JoinPwView(
                    onNext = {
                        navController.navigate("name")
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("name") {
                LaunchedEffect(Unit) {
                    currentStep = 4
                }
                JoinNameView(
                    onNext = {

                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
@Composable
fun JoinBackButton(onBack: () -> Unit) {
    Box (
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 50.dp, start = 30.dp)
    ){
        Image(
            painter = painterResource(R.drawable.ic_back),
            modifier = Modifier
                .size(15.dp, 21.dp)
                .clickable { onBack },
            contentDescription = "back_button",

            )
    }
}

@Composable
fun JoinTopView(currentStep: Int, totalSteps: Int) {
    val nickname = remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()

    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_ttatta_logo),
            modifier = Modifier
                .size(55.36.dp, 48.dp),
            contentDescription = "main_logo_join"
        )
        Spacer(modifier = Modifier.height(35.dp))
        AnimatedProgressBar(currentStep = currentStep, totalSteps = totalSteps)
        Spacer(modifier = Modifier.height(12.dp))

    }
}


@Preview(showBackground = true)
@Composable
fun PreviewJoinParentScreen() {
    JoinParentScreen(navController = NavHostController(LocalContext.current))
}