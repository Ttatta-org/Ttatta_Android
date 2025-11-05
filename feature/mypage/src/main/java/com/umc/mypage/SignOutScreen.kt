@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.bringIntoViewRequester
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
// Focus & Keyboard
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

// Bring-into-view (자동 스크롤)
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.CircleShape
// Coroutine
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// LaunchedEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp


@Composable
fun SignOutScreen(
    name: String,
    onLeaveUser: (String) -> Unit,
    onCancel: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val backgroundColor = Color(0xFFFFFFFF) // 상태바 배경색 (배경과 맞춤)

    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedReason by rememberSaveable { mutableStateOf<LeaveReason?>(null) }
    var etcText by rememberSaveable { mutableStateOf("") }
    var agreed by rememberSaveable { mutableStateOf(false) }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor, // ✅ 상태바를 앱 배경색과 동일하게 설정
        )
    }
    Column(Modifier.fillMaxSize()) {
        // ───────────── 스크롤 영역(위쪽) ─────────────
        Box(Modifier.weight(1f, fill = true)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp)
                    .background(Color.White)
                    .padding(horizontal = 22.dp),
                //horizontalAlignment = Alignment.CenterHorizontally,
                // 하단 고정 영역과 겹치지 않도록 약간의 여백
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item { Spacer(Modifier.height(42.dp)) }
                item { SignOutHeader() }
                item { Spacer(Modifier.height(30.dp)) }
                item {
                    ReasonSection(
                        selected = selectedReason,
                        onSelect = { selectedReason = it },
                        etcText = etcText,
                        onEtcChange = { etcText = it }
                    )
                }
                // ⚠️ 여기서 더 이상 SignOutNotice/SignOutButtonRow 넣지 않음
            }

            // 상단 TopBar 고정
            TopBarComponent()
        }

        // ───────────── 하단 고정 영역 ─────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 22.dp, vertical = 12.dp)
                .navigationBarsPadding()
                .imePadding(), // 키보드 올라올 때 가리지 않게
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SignOutNotice(
                agreed = agreed,
                onAgreedChange = { agreed = it }
            )
            SignOutButtonRow(
                onCancel = onCancel,
                showConfirmDialog = {
                    if (agreed && selectedReason != null) {
                        showConfirmDialog = true
                    }
                }
            )
        }
    }
    // ✅ 확인 Dialog (제출 → 확인 누르면 탈퇴 실행)
    if (showConfirmDialog) {
        Dialog(
            message = "계정을 삭제하면 기록, 발자국, 포인트 등\n모든 활동 정보가 삭제됩니다.",
            onDismiss = { showConfirmDialog = false },
            onConfirm = {
                showConfirmDialog = false
                val reasonText =
                    if (selectedReason == LeaveReason.ETC) etcText
                    else selectedReason?.label ?: ""
                onLeaveUser(reasonText)
            }
        )
    }
}
// 상단 제목
@Composable
fun SignOutHeader() {
    Column() {
        Text(
            text = "그동안 따따를 이용해주셔서 \n감사합니다.",
            fontSize = 22.sp,
            fontWeight = FontWeight.W800,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "따따를 이용하며 느끼신 불편함을 공유해주시면\n"+
                    "더욱 발전된 서비스를 제공할 수 있도록 노력하겠습니다.",
            fontSize = 15.sp,
            fontWeight = FontWeight.W400,
            color = Color(0xFF8E8E8E)
        )
    }
}
//탈퇴 이유 입력
private enum class LeaveReason(val label: String) {
    OTHER_SERVICE("다른 유사 서비스를 이용해요."),
    HARD_TO_USE("사용을 잘 안하게 돼요."),
    FOUND_BUG("잦은 오류와 장애가 발생해요."),
    MAKE_NEW_ACCOUNT("새 계정을 만들고 싶어요."),
    ETC("기타")
}

@Composable
private fun ReasonSection(
    selected: LeaveReason?,
    onSelect: (LeaveReason) -> Unit,
    etcText: String,
    onEtcChange: (String) -> Unit
) {
    val bivRequester = remember { BringIntoViewRequester() }   // ← 이름 변경
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    Column {
        ReasonRadioRow("다른 유사 서비스를 이용해요.", LeaveReason.OTHER_SERVICE, selected, onSelect)
        ReasonRadioRow("사용을 잘 안하게 돼요.", LeaveReason.HARD_TO_USE, selected, onSelect)
        ReasonRadioRow("잦은 오류와 장애가 발생해요.", LeaveReason.FOUND_BUG, selected, onSelect)
        ReasonRadioRow("새 계정을 만들고 싶어요.", LeaveReason.MAKE_NEW_ACCOUNT, selected, onSelect)
        // 기타
        ReasonRadioRow("기타", LeaveReason.ETC, selected, onSelect)

        // ETC 선택되면 자동 포커스 + bringIntoView
        LaunchedEffect(selected) {
            if (selected == LeaveReason.ETC) {
                // 약간의 지연을 주면 레이아웃이 확정된 뒤 동작해서 튐 현상이 줄어듭니다.
                kotlinx.coroutines.delay(80)
                focusRequester.requestFocus()
                keyboard?.show()
                bivRequester.bringIntoView()
            }
        }

        if (selected == LeaveReason.ETC) {
            OutlinedTextField(
                value = etcText,
                onValueChange = {
                    onEtcChange(it)
                    // 입력 중에도 키보드가 가려버리면 계속 따라오도록
                    scope.launch { bivRequester.bringIntoView() }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFFFEFE4),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .background(
                        color = Color(0xFFFEF6F2),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .focusRequester(focusRequester)                         // 포커스 요청
                    .onFocusEvent {
                        if (it.isFocused) scope.launch { bivRequester.bringIntoView() }
                    }
                    .bringIntoViewRequester(bivRequester),
                placeholder = {
                    Text(
                        "탈퇴 사유를 적어주세요.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W400,
                        color = Color(0xFFFFD2AC)
                    )
                },
                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color(0xFFFF9888)
                ),
                singleLine = false,
                maxLines = 10
            )
        }
    }
}

@Composable
private fun ReasonRadioRow(
    text: String,
    value: LeaveReason,
    selected: LeaveReason?,
    onSelect: (LeaveReason) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(value) }
            .padding(bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomRadioButton(
            selected = selected == value
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(text = text, fontSize = 15.sp)
    }
}

// 유의사항 안내
@Composable
fun SignOutNotice(
    agreed: Boolean,
    onAgreedChange: (Boolean) -> Unit
) {
    Column(
        Modifier
            .background(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(16.dp))
            .border(1.dp, color = Color(0xFFE1E1E1), RoundedCornerShape(16.dp))
            .padding(start = 21.dp, top = 21.dp, end = 21.dp, bottom = 13.dp)
    ) {
        Text(
            text = "탈퇴 안내 및 유의사항",
            fontSize = 14.sp,
            fontWeight = FontWeight.W800,
            color = Color.Black,
            lineHeight = 10.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))

        SignOutNoticeText()

        Spacer(modifier = Modifier.height(10.dp))

        SignOutConsentCheckbox(checked = agreed, onCheckedChange = onAgreedChange)

    }
}
@Composable
fun SignOutNoticeText() {
    Column()
    {
        SignOutNoticeRow(
            number = 1,
            text = buildAnnotatedString {
                append("탈퇴 아이디는 복구와 재사용이 불가능합니다.")
            }
        )

        SignOutNoticeRow(
            number = 2,
            text = AnnotatedString("삭제된 데이터는 복구되지 않습니다.")
        )

        SignOutNoticeRow(
            number = 3,
            text = AnnotatedString ("소셜 로그인 회원의 경우 서비스에서 관리하는 모든 정보가 삭제되며, 같은 소셜 아이디로 재가입시 신규회원으로 가입됩니다.")
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
            fontSize = 12.sp,
            modifier = Modifier
                .alignBy(FirstBaseline)
                .padding(end = 3.dp),
            color = Color(0xFF4B4B4B),
            fontWeight = FontWeight.W400
        )

        Text(
            text = text,
            fontSize = 12.sp,
            lineHeight = 20.sp,
            modifier = Modifier.alignBy(FirstBaseline),
            color = Color(0xFF4B4B4B),
            fontWeight = FontWeight.W400
        )
    }
}
// 동의 체크박스
@Composable
fun SignOutConsentCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    CustomCheckboxWithText(
        checked = checked,
        onCheckedChange = onCheckedChange,
        text = "위 안내사항을 확인했으며, 이에 동의합니다."
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
            .background(color = Color.White, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE1E1E1), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 11.dp)
            .clickable { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .size(17.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, Color(0xFFE1E1E1), RoundedCornerShape(6.dp))
                .background(if (checked) Color(0xFFB1B1B1) else Color.White)
                .clickable { onCheckedChange(!checked) },
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.W400,
            color = Color.Black,
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

@Composable
fun CustomRadioButton(
    selected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp, // 라디오 버튼의 전체 크기
    selectedColor: Color = Color(0xFFFF9888), // 선택 시 색상
    unselectedColor: Color = Color(0xFFFFB1A5) // 미선택 시 색상
) {
    // 1. 바깥 원 (테두리)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = if (selected) selectedColor else unselectedColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // 2. 선택됐을 때만 안쪽 원을 그림
        if (selected) {
            Box(
                modifier = Modifier
                    .size(size / 2) // 안쪽 원은 바깥 원의 절반 크기
                    .clip(CircleShape)
                    .background(selectedColor)
            )
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