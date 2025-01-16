package com.umc.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun JoinScreen() {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        JoinTopView(currentStep = 2, totalSteps = 4)


    }
}

@Composable
fun JoinTopView(currentStep: Int, totalSteps: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 92.dp)
            .fillMaxWidth()

    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_ttatta_logo),
            modifier = Modifier
                .size(55.36.dp, 48.dp)
                .padding(bottom = 10.dp),
            contentDescription = "main_logo_join"
        )
        //상태에 따른 bar 만들어야하는데 지금 상태는 똥임 ㅠ
        ProgressBar(currentStep = currentStep, totalSteps = totalSteps)
        Text(
            text = stringResource(R.string.join_nickname),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .width(280.dp)
                .padding(top = 15.dp),
            color = colorResource(R.color.yellow_200)
        )
    }

}
@Composable
fun ProgressBar(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { step ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .background(
                        color = if (step < currentStep) Color(0xFFFF9681) else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
            if (step < totalSteps - 1) {
                Spacer(modifier = Modifier.width(4.dp)) // 각 단계 간 간격
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewJoinScreen() {
    JoinScreen()
}