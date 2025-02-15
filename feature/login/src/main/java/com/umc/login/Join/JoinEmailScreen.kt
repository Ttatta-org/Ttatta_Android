package com.umc.login.Join

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.umc.login.R
@Composable
fun JoinEmailScreen(navController: NavHostController,viewModel: JoinViewModel = viewModel()) {
    Column(modifier = Modifier.wrapContentSize()) {
        JoinEmailView(viewModel = viewModel, onNext = { navController.navigate("join_pw") })
    }
}

@Composable
fun JoinEmailView(viewModel: JoinViewModel, onNext: () -> Unit) {
    val emailLocalPart by viewModel.emailLocalPartState.collectAsState()
    val emailDomain by viewModel.emailDomainState.collectAsState()
    val isCustomDomain by viewModel.isCustomDomain.collectAsState()
    val isEmailValid by viewModel.isEmailValid.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val domains = listOf("naver.com", "gmail.com", "yahoo.com", "직접입력")

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.join_email),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.yellow_200)
        )
        Spacer(modifier = Modifier.height(35.dp))

        // 이메일 입력 (localPart + @ + 도메인 선택)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(310.dp)
        ) {
            EmailInputTextField(
                value = emailLocalPart,
                onValueChange =viewModel::onEmailLocalPartChange,
                placeholder = "이메일 입력"
            )
            Text(
                text = "@",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Box {
                Row(
                    modifier = Modifier.clickable { expanded = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmailInputTextField(
                        value = emailDomain,
                        onValueChange = { if (isCustomDomain) viewModel.onCustomDomainChange(it) },
                        placeholder = "직접입력",
                        readOnly = !isCustomDomain
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dropdown),
                        contentDescription = "Dropdown",
                        tint = Color.Gray,
                        modifier = Modifier.size(11.dp).clickable { expanded = true }
                    )
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    domains.forEach { domain ->
                        DropdownMenuItem(text = { Text(domain) }, onClick = {
                            viewModel.onEmailDomainChange(domain)
                            expanded = false
                        })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(101.dp))

        // 다음 버튼
        Button(
            enabled = isEmailValid,
            onClick = { if (isEmailValid) onNext() },
            modifier = Modifier
                .width(310.dp)
                .height(45.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isEmailValid) colorResource(R.color.orange_200) else colorResource(R.color.yellow_300),
                disabledContainerColor = colorResource(R.color.yellow_300)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 1.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text(
                text = stringResource(R.string.verifiy_button),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}



@Composable
fun EmailInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        readOnly = readOnly,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight(600),
            lineHeight = 20.sp,
            color = colorResource(R.color.gray_500)
        ),
        placeholder = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(600),
                    color = colorResource(R.color.gray_500)
                )
            }
        },
        keyboardOptions = KeyboardOptions.Default,
        modifier = Modifier.width(140.dp).height(51.dp).then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = colorResource(R.color.gray_500),
            unfocusedIndicatorColor = colorResource(R.color.gray_500),
            cursorColor = Color.Black
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewJoinEmailScreen() {
    JoinEmailScreen(navController = NavHostController(LocalContext.current))
}
