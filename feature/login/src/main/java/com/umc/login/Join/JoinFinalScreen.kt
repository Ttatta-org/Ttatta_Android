package com.umc.login.Join

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.umc.login.R

@Composable
fun JoinFinalScreen(navController: NavHostController, viewModel: JoinViewModel = viewModel()) {
    val nickname by viewModel.nickNameState.collectAsState()

    Decoration()
    Column (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(modifier = Modifier
            .padding(top = 174.dp),
            text = "환영해요 ${nickname}님!", // 닉네임으로 할건지 아니면 이름으로 할건지 결정해야함!
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight(800),
                color = Color(0xCF4B4B4B),
                textAlign = TextAlign.Center,
            )
        )
        Spacer(modifier = Modifier.size(17.dp))

        Text(modifier = Modifier,
            text = "지금부터 따따와 함께\n소중한 일상을 기록해볼까요?",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFFFF8072),

                textAlign = TextAlign.Center,
            )
        )
        Spacer(modifier = Modifier.height(440.dp))
        Button(
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 1.dp, // 기본 그림자
                pressedElevation = 0.dp, // 버튼을 눌렀을 때 그림자
                disabledElevation = 0.dp // enabled가 false일때 그림자
            ),
            onClick = {
                navController.navigate("login") {
                    popUpTo("join") { inclusive = true } // 회원가입 스택 정리
                }
            },
            modifier = Modifier
                .width(310.dp)
                .height(45.dp),
            shape = RoundedCornerShape(28.dp), // 라운딩 처리
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.orange_200)
            )
        ) {
            Text(
                text = "로그인하러 가기",
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFFFFFFFF),

                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}

@Composable
fun Decoration() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ){
        Image(
            painter = painterResource(id = R.drawable.img_gradient_join),
            modifier = Modifier
                .size(519.dp, 519.dp)
                .offset(x = 40.dp, y =-30.dp),
            contentDescription = "gradient1"
        )
        Image(
            painter = painterResource(id = R.drawable.img_cloud_join),
            modifier = Modifier
                .padding(top = 303.dp, start = 30.dp),
            contentDescription = "cloud1"
        )
        Image(
            painter = painterResource(id = R.drawable.ic_ttuttuandttotto),
            modifier = Modifier
                .size(336.dp, 281.dp)
                .align(Alignment.Center),
            contentDescription = "ttuttuandttotto"
        )
        Image(
            painter = painterResource(id = R.drawable.img_cloud_join2),
            modifier = Modifier
                .padding(top = 607.dp, start = 305.dp),
            contentDescription = "cloud2"
        )
        Image(
            painter = painterResource(id = R.drawable.img_gradient_join2),
            modifier = Modifier
                .size(366.dp, 366.dp)
                .offset(x =-65.dp, y =400.dp),
            contentDescription = "gradient2"
        )
    }
}

@Preview
@Composable
fun PreviewJoinFinalScreen() {
    JoinFinalScreen(navController = NavHostController(LocalContext.current))
}