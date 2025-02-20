package com.umc.login.FindId

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.login.R

@Composable
fun FindIdEndScreen() {
    Column (
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        EndTopView()
        EndBottomView()
    }
}

@Composable
fun EndTopView() {
    Box(
        modifier = Modifier
            .shadow(
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 40.dp, bottomEnd = 40.dp),
                elevation = 4.dp,
                spotColor = Color(0x14000000),
                ambientColor = Color(0x14000000)
            )
            .fillMaxWidth()
            .height(609.dp)
            .background(colorResource(R.color.yellow_100), shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 40.dp, bottomEnd = 40.dp)),
        contentAlignment = Alignment.TopCenter
    ){
        Image(
            painter = painterResource(R.drawable.img_textballoon),
            modifier = Modifier
                .size(467.dp, 397.dp)
                .offset(0.dp, -40.dp),
            contentDescription = "TextBalloon"
        )
        Image(
            painter = painterResource(R.drawable.ic_lean_ttuttu),
            modifier = Modifier
                .size(390.dp, 617.dp)
                .offset(-5.dp, 10.dp),
            contentDescription = "leanTTuTTu"
        )
        Image(
            painter = painterResource(R.drawable.ic_leanttotto),
            modifier = Modifier
                .size(390.dp, 482.dp)
                .offset(5.dp,130.dp),
            contentDescription = "leanTToTTo"
        )
        Text(
            modifier = Modifier
                .padding(top = 58.dp),
            text = "아이디 찾기",
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 5.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFFFF8072),

                textAlign = TextAlign.Center,
            )
        )

        Text(
            modifier = Modifier
                .padding(top = 112.dp),
            text = "아이디 찾기가 완료되었어요!",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight(800),
                color = Color(0xFFFF8072),
                textAlign = TextAlign.Center,
            )
        )

        Row (modifier = Modifier
            .wrapContentSize()
            .padding(top = 170.dp))
        {
            Text(text = "가입자")
            Spacer(modifier = Modifier.width(25.dp))
            Text(text = "김아무개", color = Color.Gray)
        }

        Row (modifier = Modifier
            .wrapContentSize()
            .padding(top = 199.dp))
        {
            Text(text = "아이디")
            Spacer(modifier = Modifier.width(25.dp))
            Text(text = "ddadda1225", color = Color.Gray)
        }

    }
}

@Composable
fun EndBottomView() {
    Column(
        modifier= Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(33.dp))
        OutlinedButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .shadow(elevation = 4.dp, spotColor = Color(0x0D000000), ambientColor = Color(0x0D000000))
                .width(310.dp)
                .height(45.dp),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp , colorResource(R.color.orange_200)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
            )
        ) {
            Text(
                text = "비밀번호 찾기",
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.orange_200),
                    textAlign = TextAlign.Center,
                )
            )
        }
        Spacer(modifier = Modifier.height(11.dp))

        Button(
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 1.dp, // 기본 그림자
                pressedElevation = 0.dp, // 버튼을 눌렀을 때 그림자
                disabledElevation = 0.dp // enabled가 false일때 그림자
            ),
            onClick = { /* TODO */ },
            modifier = Modifier
                .width(310.dp)
                .height(45.dp),
            shape = RoundedCornerShape(24.dp), // 라운딩 처리
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.orange_200)
            )
        ){
            Text(text = "로그인 하러가기",
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

@Preview
@Composable
fun PreviewFindIdEndScreen() {
    FindIdEndScreen()
}