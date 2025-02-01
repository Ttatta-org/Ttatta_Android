package com.umc.login.FindPw


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.umc.login.FindId.FindCertiInputTextField
import com.umc.login.FindId.FindDomainInputTextField
import com.umc.login.FindId.FindEmailInputTextField
import com.umc.login.Join.EmailInputTextField
import com.umc.login.Join.JoinBackButton
import com.umc.login.Join.NameInputTextField
import com.umc.login.R

@Composable
fun FindPwScreen2(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        FindPw2TopView(onBack = { navController.navigate("") })
        FindPw2MainView(onNext = { navController.navigate("login") }, onBack = { navController.navigate("login") })
    }
}

@Composable
fun FindPw2TopView(onBack: () -> Unit) {

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
            text = stringResource(R.string.find_id_comment),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            color = colorResource(R.color.gray_600)


        )
    }
}

@Composable
fun FindPw2MainView(onNext: () -> Unit, onBack: () -> Unit) {
    var localPart by remember { mutableStateOf("") }
    var selectedDomain by remember { mutableStateOf("직접입력") }
    var customDomain by remember { mutableStateOf("") }
    var isCustomDomain by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var nameState by remember { mutableStateOf("") }
    var isWarningVisible by remember { mutableStateOf(false) }
    val domains = listOf("naver.com", "gmail.com", "yahoo.com", "직접입력")
    var certiCode by remember { mutableStateOf("") }
    val isButtonEnabled = nameState.isNotEmpty() && nameState.length <= 8
    Spacer(modifier = Modifier.height(49.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        FindNameInputTextField(
            value = nameState,
            onValueChange = {
                if (it.length <= 9) {
                    nameState = it
                    isWarningVisible = (it.length == 9)
                }
            },
            placeholder = stringResource(R.string.find_id_name),
            isWarning = isWarningVisible
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(318.dp)
        ) {
            FindEmailInputTextField(
                value = localPart,
                onValueChange = { localPart = it },
                placeholder = "이메일 입력"
            )
            Text(
                text = "@",
                fontSize = 14.sp,
                fontWeight = FontWeight(600),
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            Box {
                Row(
                    modifier = Modifier.clickable { expanded = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FindDomainInputTextField(
                        value = if (isCustomDomain) customDomain else selectedDomain,
                        onValueChange = { if (isCustomDomain) customDomain = it },
                        placeholder = "직접입력",
                        readOnly = !isCustomDomain
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dropdown),
                        contentDescription = "Dropdown",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(9.dp, 6.dp)
                            .clickable { expanded = true }
                    )
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    domains.forEach { domain ->
                        DropdownMenuItem(text = { Text(domain) }, onClick = {
                            selectedDomain = domain
                            isCustomDomain = domain == "직접입력"
                            expanded = false
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.width(20.dp))

            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.find_id_certibutton),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.orange_500),
                    modifier = Modifier.clickable { })
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(318.dp)
        ) {
            FindCertiInputTextField(
                value = certiCode,
                onValueChange = { if (it.length <= 6) certiCode = it },
                placeholder = stringResource(R.string.join_certification_comment)

            )
            Spacer(modifier = Modifier.width(20.dp))

            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.find_id_confirmbutton),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.gray_600),
                    modifier = Modifier.clickable { })
            }
        }
        Spacer(modifier = Modifier.height(82.dp))

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
                text = stringResource(R.string.find_pw),
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(600),
                color = Color.White
            )
        }
    }
}
@Composable
fun FindNameInputTextField(
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
            textAlign = TextAlign.Start,
            fontSize = 14.sp,
            fontWeight = FontWeight(600),
            lineHeight = 20.sp,
            color = if (isWarning) colorResource(R.color.negativeRed) else Color.Black
        ),
        placeholder = {
            Box(
                modifier = Modifier.fillMaxWidth(),
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
        modifier = Modifier
            .width(318.dp)
            .height(51.dp),
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
fun PreviewFindPwScreen2() {
    FindPwScreen2(navController = NavHostController(LocalContext.current))
}
