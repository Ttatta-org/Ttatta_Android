package com.umc.login.Join

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.umc.login.R
import com.umc.login.component.CertiInputTextField
import kotlinx.coroutines.delay

@Composable
fun JoinCertiScreen(navController: NavHostController,viewModel: JoinViewModel = viewModel()) {
    Column(modifier = Modifier.wrapContentSize()) {
        JoinCertiView(viewModel =viewModel,onNext = { navController.navigate("join_pw") })
    }
}

@Composable
fun JoinCertiView(viewModel: JoinViewModel, onNext: () -> Unit) {
    val certiCode by viewModel.certiCodeState.collectAsState()
    val timer by viewModel.timerState.collectAsState()
    val isCodeValid by viewModel.isCertiCodeValid.collectAsState()
    val certiError by viewModel.certiError.collectAsState()


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.join_certification),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(600),
            color = colorResource(R.color.yellow_200)
        )
        Spacer(modifier = Modifier.height(35.dp))

        CertiInputTextField(
            value = certiCode,
            onValueChange = viewModel::onCertiCodeChange,
            placeholder = stringResource(R.string.join_certification_comment),
            timer = timer,
            errorMessage = certiError
        )

        Spacer(modifier = Modifier.height(103.dp))

        Text(
            text = stringResource(R.string.join_certification_small_comment),
            color = colorResource(R.color.orange_500),
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(400)
        )

        Spacer(modifier = Modifier.height(9.dp))

        Button(
            enabled = isCodeValid,
            onClick = {  viewModel.verifyCertiCode(
                onSuccess = { onNext() },
                onFailure = { }
            ) },
            modifier = Modifier
                .width(310.dp)
                .height(45.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.orange_200),
                disabledContainerColor = colorResource(R.color.orange_200)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 1.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text(
                text = stringResource(R.string.re_verifiy_button),
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(600),
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewJoinCertiScreen() {
    JoinCertiScreen(navController = NavHostController(LocalContext.current))
}
