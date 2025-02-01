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
import androidx.navigation.NavHostController
import com.umc.login.R
import kotlinx.coroutines.delay

@Composable
fun JoinCertiScreen(navController: NavHostController) {
    Column(modifier = Modifier.wrapContentSize()) {
        JoinCertiView(
            onNext = { navController.navigate("join_pw") },
            onBack = { navController.navigate("join") }
        )
    }
}

@Composable
fun JoinCertiView(onNext: () -> Unit, onBack: () -> Unit) {
    var certiCode by remember { mutableStateOf("") }
    var timer by remember { mutableStateOf(600  ) }
    val isCodeValid = certiCode.length == 6

    LaunchedEffect(timer) {
        while (timer > 0) {
            delay(1000L)
            timer--
        }
    }

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
            onValueChange = { if (it.length <= 6) certiCode = it },
            placeholder = stringResource(R.string.join_certification_comment),
            timer = timer
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
            onClick = { if (isCodeValid) onNext() },
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

@Composable
fun CertiInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    timer: Int
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(600),
                color = Color.Black
            ),
            placeholder = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(600),
                        color = colorResource(R.color.gray_500),
                        textAlign = TextAlign.Center
                    )
                }
            },
            trailingIcon = {
                Text(
                    text = "${timer / 60}:${String.format("%02d", timer % 60)}",
                    color = if (timer > 0) colorResource(R.color.gray_500) else colorResource(R.color.negativeRed),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 6.dp)
                )
            },
            modifier = Modifier
                .width(310.dp)
                .height(51.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colorResource(R.color.gray_500),
                unfocusedIndicatorColor = colorResource(R.color.gray_500),
                cursorColor = Color.Black
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewJoinCertiScreen() {
    JoinCertiScreen(navController = NavHostController(LocalContext.current))
}
