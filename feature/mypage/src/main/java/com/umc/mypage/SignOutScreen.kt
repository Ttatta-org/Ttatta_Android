package com.umc.mypage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.umc.mypage.components.Dialog
import com.umc.mypage.components.TopBarComponent

@Composable
fun SignOutScreen(
    name: String,
    onLeaveUser: (String) -> Unit,
    onCancel: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val backgroundColor = Color(0xFFFFFFFF) // 상태바 배경색 (배경과 맞춤)

    var showConfirmDialog by remember { mutableStateOf(false) }
    var reason by remember { mutableStateOf("") }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor, // ✅ 상태바를 앱 배경색과 동일하게 설정
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .weight(1f) // ✅ BottomNavigation을 밀어내지 않도록 LazyColumn에 weight 적용
            ) {
                // ✅ 2. LazyColumn (스크롤 가능한 콘텐츠)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .padding(top = 60.dp)
                        .background(Color(0xFFFFFFFF))
                        .padding(horizontal = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Spacer(modifier = Modifier.height(42.dp)) }
                    item { SignOutHeader() }
                    item { Spacer(modifier = Modifier.height(60.dp)) }
                    item { SignOutReasonInput(name = name, reason = reason, onReasonChange = {reason = it}) }
                    item { Spacer(modifier = Modifier.height(58.dp)) }
                    item { SignOutNotice() }
                    item { Spacer(modifier = Modifier.height(23.dp)) }
                    item { SignOutConsentCheckbox(checked = false, onCheckedChange = {}) }
                    item { Spacer(modifier = Modifier.height(51.dp)) }
                    item { SignOutButtonRow(onCancel = onCancel, showConfirmDialog = { showConfirmDialog = it }) }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }

                // ✅ 3. TopBar (스크롤 가능한 LazyColumn 위에 배치)
                TopBarComponent()
            }

            // ✅ 4. BottomNavigationBarWithFAB (항상 하단에 고정)
//            BottomNavigationBarWithFAB(
//                selectedTab = "mypage",
//                onTabSelected = { /* 탭 변경 로직 */ },
//                onFabClick = onFabClick
//            )

        }
    }
    // ✅ 확인 Dialog (제출 → 확인 누르면 탈퇴 실행)
    if (showConfirmDialog) {
        Dialog(
            message = "계정을 삭제하면 기록, 발자국, 포인트 등\n모든 활동 정보가 삭제됩니다.",
            onDismiss = { showConfirmDialog = false },
            onConfirm = {
                showConfirmDialog = false
                onLeaveUser(reason)
            }
        )
    }
}
// 상단 제목
@Composable
fun SignOutHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "그동안 따따를 이용해주셔서 감사합니다.",
            fontSize = 20.sp,
            fontWeight = FontWeight.W800,
            lineHeight = 20.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "서비스를 이용하시는데 느끼신 불편함을\n저희에게 공유해주시면 더욱 발전된 서비스를\n제공할 수 있도록 노력하겠습니다.",
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            fontWeight = FontWeight.W400,
            color = Color(0xFF4B4B4B)
        )
    }
}
//탈퇴 이유 입력
@Composable
fun SignOutReasonInput(name: String, reason: String, onReasonChange: (String) -> Unit) {
    Column {
        Text(
            text = "${name}님이 탈퇴하려는 이유가 궁금해요",
            fontSize = 16.sp,
            fontWeight = FontWeight.W800,
            color = Color.Black,
            modifier = Modifier.padding(start = 10.dp)
        )

        Spacer(modifier = Modifier.height(17.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = Color(0xDE806E33),
                    spotColor = Color(0xFFDE806E)
                )
        ) {
            OutlinedTextField(
                value = reason,
                onValueChange = onReasonChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                placeholder = {
                    Text(
                        text = "이유 적기",
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFFFDDDC1)
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                ),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    cursorColor = Color(0xFFFF9888),
                    focusedContainerColor = Color(0xFFFFF6F2),
                    unfocusedContainerColor = Color(0xFFFFF6F2)
                ),
                singleLine = false,
                maxLines = 5
            )
        }
    }
}
// 유의사항 안내
@Composable
fun SignOutNotice() {
    Column {
        Text(
            text = "탈퇴 안내 및 유의사항",
            fontSize = 16.sp,
            fontWeight = FontWeight.W800,
            color = Color.Black,
            modifier = Modifier.padding(start = 10.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Divider(
            color = Color(0xFFFFEFE4),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(18.dp))

        SignOutNoticeText()

    }
}
@Composable
fun SignOutNoticeText() {
    Column(
        modifier = Modifier.padding(horizontal = 14.dp)
    ) {
        SignOutNoticeRow(
            number = 1,
            text = buildAnnotatedString {
                append("탈퇴 아이디는 ")
                withStyle(SpanStyle(color = Color(0xFFFF8072))) { append("복구") }
                append("와 ")
                withStyle(SpanStyle(color = Color(0xFFFF8072))) { append("재사용") }
                append("이 불가능합니다.")
            }
        )

        Spacer(modifier = Modifier.height(15.dp))

        SignOutNoticeRow(
            number = 2,
            text = AnnotatedString("삭제된 데이터는 복구되지 않습니다.")
        )

        Spacer(modifier = Modifier.height(18.dp))

        SignOutNoticeRow(
            number = 3,
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color(0xFFFF8072))) {
                    append("소셜 로그인 회원")
                }
                append("의 경우, 서비스에서 관리하는 모든 정보가 삭제되며, 같은 소셜 아이디로 재가입 시 신규회원으로 가입됩니다.")
            }
        )
    }
}
@Composable
fun SignOutNoticeRow(number: Int, text: AnnotatedString) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "$number.",
            fontSize = 13.sp,
            modifier = Modifier
                .alignBy(FirstBaseline)
                .padding(end = 3.dp),
            color = Color(0xFF4B4B4B),
            fontWeight = FontWeight.W400
        )

        Text(
            text = text,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            modifier = Modifier.alignBy(FirstBaseline),
            color = Color(0xFF4B4B4B),
            fontWeight = FontWeight.W400
        )
    }
}
// 동의 체크박스
@Composable
fun SignOutConsentCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    var agreed by remember { mutableStateOf(false) }

    CustomCheckboxWithText(
        checked = agreed,
        onCheckedChange = { agreed = it },
        text = "위 안내사항을 확인했으며 이에 동의합니다."
    )
}
@Composable
fun CustomCheckboxWithText(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFFFEFE4), RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 12.dp)
            .clickable { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .size(25.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, Color(0xFFFFEFE4), RoundedCornerShape(6.dp))
                .background(if (checked) Color(0xFFFFD0C8) else Color.White)
                .clickable { onCheckedChange(!checked) },
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.W300,
            color = Color(0xFF4B4B4B),
            modifier = Modifier
                .padding(start = 10.dp)
        )
    }
}
// 하단 버튼
@Composable
fun SignOutButtonRow(
    onCancel: () -> Unit,
    showConfirmDialog: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 취소 버튼 (오렌지 200)
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(13.dp),
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, Color(0xFFFFD0C8)),
            contentPadding = PaddingValues(vertical = 13.dp),
        ) {
            Text(text = "취소", fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.W600, color = Color(0xFFFFD0C8))
        }

        // 확인 버튼 (오랜지 400)
        Button(
            onClick = { showConfirmDialog(true) } ,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9888)),
            shape = RoundedCornerShape(13.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 13.dp)
        ) {
            Text(text = "제출", fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.W600, color = Color.White)
        }
    }
}





@Preview(showBackground = true)
@Composable
fun PreviewSignOutScreen() {
    SignOutScreen(
        name = "김따따",
        onLeaveUser = {},
        onCancel = {}
    )
}