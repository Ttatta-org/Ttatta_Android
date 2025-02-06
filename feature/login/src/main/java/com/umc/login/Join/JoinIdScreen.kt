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
import androidx.compose.ui.focus.onFocusChanged
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
import com.umc.login.R

@Composable
fun JoinIdScreen(navController: NavHostController) {
    Column(modifier = Modifier.wrapContentSize()) {
        JoinIdView(
            onNext = { navController.navigate("join_pw") },
            onBack = { navController.navigate("join") }
        )
    }
}

@Composable
fun JoinIdView(onNext: () -> Unit, onBack: () -> Unit) {
    var idState by remember { mutableStateOf("") }
    var isWarningVisible by remember { mutableStateOf(false) }
    val isButtonEnabled = idState.isNotEmpty() && idState.length <= 15

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.join_id),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(600),
            color = colorResource(R.color.yellow_200)
        )
        Spacer(modifier = Modifier.height(35.dp))

        IdInputTextField(
            value = idState,
            onValueChange = { newText ->
                if (newText.length <= 16) {
                    idState = newText
                    isWarningVisible = (newText.length == 16)
                }
            },
            placeholder = stringResource(R.string.join_id_comment),
            isWarning = isWarningVisible
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = stringResource(R.string.join_id_small_comment),
            color = if (isWarningVisible) colorResource(R.color.negativeRed) else colorResource(R.color.gray_400),
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(400)
        )
        Spacer(modifier = Modifier.height(101.dp))

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
fun IdInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isWarning: Boolean
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = value,
            onValueChange = {
                if (it.length <= 16) {
                    onValueChange(it)
                }
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = if (isWarning) colorResource(R.color.negativeRed) else Color.Black
            ),
            trailingIcon = {
                Text(
                    text = stringResource(R.string.duplicate_check),
                    color = if (isWarning) colorResource(R.color.negativeRed) else colorResource(R.color.gray_500),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clickable {
                            // TODO: 중복 확인 로직 추가
                        }
                        .padding(end = 6.dp)
                )
            },
            keyboardOptions = KeyboardOptions.Default,
            modifier = Modifier
                .width(310.dp)
                .height(51.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colorResource(R.color.gray_500),
                unfocusedIndicatorColor = colorResource(R.color.gray_500),
                cursorColor = if (isWarning) colorResource(R.color.negativeRed) else Color.Black
            )
        )

        if (value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center ,
            ) {
                Text(
                    text = placeholder,
                    lineHeight = 20.sp,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.gray_500)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewJoinIdScreen() {
    JoinIdScreen(navController = NavHostController(LocalContext.current))
}
