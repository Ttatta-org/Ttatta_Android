package com.umc.login.FindPw

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.umc.login.FindId.FindIdMainView
import com.umc.login.FindId.FindIdScreen
import com.umc.login.Join.NicknameInputTextField
import com.umc.login.R

@Composable
fun FindPwScreen1(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        FindPwTopView(
            onNext = { navController.navigate("find_pw2") },
            onBack = { navController.navigate("login") })
    }
}

@Composable
fun FindPwTopView(onNext: () -> Unit, onBack: () -> Unit) {
    var nameState by remember { mutableStateOf("") }
    var isWarningVisible by remember { mutableStateOf(false) }
    val isButtonEnabled = nameState.isNotEmpty() && nameState.length <= 8

    Column (modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            horizontalArrangement = Arrangement.Absolute.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 58.dp),
        ) {
            Spacer(modifier = Modifier.width(30.dp))
            Image(
                painter = painterResource(R.drawable.ic_back),
                modifier = Modifier
                    .size(15.dp, 21.dp)
                    .clickable { onBack() },
                contentScale = ContentScale.None,
                contentDescription = "back_button"
            )
            Spacer(modifier = Modifier.width(122.dp)) // 가운데 정렬 방법 찾아야함 ㅠ
            Text(
                text = stringResource(R.string.find_pw),
                fontSize = 15.sp,
                fontWeight = FontWeight(600),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.orange_500)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            modifier = Modifier.wrapContentSize(),
            text = stringResource(R.string.find_pw_comment),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            color = colorResource(R.color.gray_600)


        )
        Spacer(modifier = Modifier.height(70.dp))

        FindPwIdInputTextField(
            value = nameState,
            onValueChange = {
                if (it.length <= 9) {
                    nameState = it
                    isWarningVisible = (it.length == 9)
                }
            },
            placeholder = stringResource(R.string.nickname_comment),
            isWarning = isWarningVisible
        )
        Spacer(modifier = Modifier.height(186.dp))
        Button(
            enabled = isButtonEnabled,
            onClick = { if (isButtonEnabled) { onNext() } },
            modifier = Modifier
                .width(310.dp)
                .height(45.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.orange_200),
                disabledContainerColor = colorResource(R.color.yellow_300)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 1.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text(
                text = stringResource(R.string.next_button),
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(600),
                color = Color.White
            )
        }
    }
}

@Composable
fun FindPwIdInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isWarning: Boolean
) {
    TextField(
        value = value,
        onValueChange = { if (it.length <= 9) onValueChange(it) },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = if (isWarning) colorResource(R.color.negativeRed) else Color.Black
        ),
        placeholder = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.gray_500)
                )
            }
        },
        keyboardOptions = KeyboardOptions.Default,
        modifier = Modifier.width(310.dp).height(51.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = colorResource(R.color.gray_500),
            unfocusedIndicatorColor = colorResource(R.color.gray_500),
            cursorColor = if (isWarning) colorResource(R.color.negativeRed) else Color.Black
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewFindIdScreen() {
    FindPwScreen1(navController = NavHostController(LocalContext.current))
}