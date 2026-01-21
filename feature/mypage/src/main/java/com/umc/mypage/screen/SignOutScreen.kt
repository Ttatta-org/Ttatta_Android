package com.umc.mypage.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.umc.design.component.CustomButton
import com.umc.design.component.CustomHeader
import com.umc.design.component.CustomPopup
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme
import com.umc.design.theme.ThemeProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignOutScreen(
    onLeaveUser: (String) -> Unit,
    onCancel: () -> Unit
) {
    val density = LocalDensity.current

    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedReason by rememberSaveable { mutableStateOf<LeaveReason?>(null) }
    var etcText by rememberSaveable { mutableStateOf("") }
    var agreed by rememberSaveable { mutableStateOf(false) }

    var topBarHeight by remember { mutableStateOf(66.dp) }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Column {
                Spacer(modifier = Modifier.height(topBarHeight))
                Spacer(Modifier.height(30.dp))
                SignOutHeader()
                Spacer(Modifier.height(30.dp))
                ReasonSection(
                    selected = selectedReason,
                    onSelect = { selectedReason = it },
                    etcText = etcText,
                    onEtcChange = { etcText = it },
                )
            }
            Spacer(modifier = Modifier.padding(30.dp))
            Column {
                SignOutNotice(
                    agreed = agreed,
                    onAgreedChange = { agreed = it },
                )
                Spacer(modifier = Modifier.height(20.dp))
                SignOutButtonRow(
                    isConfirmButtonEnabled = agreed,
                    onCancel = onCancel,
                    showConfirmDialog = {
                        if (agreed && selectedReason != null) {
                            showConfirmDialog = true
                        }
                    },
                )
                Spacer(
                    modifier = Modifier.height(
                        22.dp +
                                max(
                                    WindowInsets.navigationBars
                                        .asPaddingValues()
                                        .calculateBottomPadding(),
                                    WindowInsets.ime
                                        .asPaddingValues()
                                        .calculateBottomPadding()
                                )
                    ),
                )
            }
        }

        Box(
            modifier = Modifier.onSizeChanged {
                topBarHeight = with(density) { it.height.toDp() }
            }
        ) {
            CustomHeader(
                showLogo = false,
                centerText = "탈퇴하기",
                backgroundColor = LocalColorTheme.current.secondary[100],
                onBackButtonClicked = onCancel,
            )
        }
    }
    // ✅ 확인 Dialog (제출 → 확인 누르면 탈퇴 실행)
    if (showConfirmDialog) {
        CustomPopup(
            title = "정말 계정을 탈퇴하겠습니까?",
            message = "계정을 탈퇴하면 기록, 발자국, 포인트 등\n" +
                    "모든 활동 정보가 삭제됩니다.",
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
private fun SignOutHeader() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "그동안 따따를 이용해주셔서 \n감사합니다.",
            fontSize = 22.sp,
            fontWeight = FontWeight.W800,
            color = Color.Black,
            lineHeight = 29.sp,
        )
        Text(
            text = "따따를 이용하며 느끼신 불편함을 공유해주시면\n" +
                    "더욱 발전된 서비스를 제공할 수 있도록 노력하겠습니다.",
            fontSize = 15.sp,
            fontWeight = FontWeight.W400,
            color = Color(0xFF8E8E8E),
            lineHeight = 20.5.sp,
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

@OptIn(ExperimentalFoundationApi::class)
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

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            LeaveReason.entries.forEach { reason ->
                ReasonRadioRow(
                    text = reason.label,
                    value = reason,
                    selected = selected,
                    onSelect = onSelect,
                )
            }
        }

        // ETC 선택되면 자동 포커스 + bringIntoView
        LaunchedEffect(selected) {
            if (selected == LeaveReason.ETC) {
                // 약간의 지연을 주면 레이아웃이 확정된 뒤 동작해서 튐 현상이 줄어듭니다.
                delay(80)
                focusRequester.requestFocus()
                keyboard?.show()
                bivRequester.bringIntoView()
            }
        }

        if (selected == LeaveReason.ETC) {
            BasicTextField(
                value = etcText,
                onValueChange = {
                    onEtcChange(it)
                    // 입력 중에도 키보드가 가려버리면 계속 따라오도록
                    scope.launch { bivRequester.bringIntoView() }
                },
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = LocalFontTheme.current.font,
                    lineHeight = 20.sp,
                    color = Color.Black,
                ),
                maxLines = 10,
                modifier = Modifier
                    .focusRequester(focusRequester)                         // 포커스 요청
                    .onFocusEvent {
                        if (it.isFocused) scope.launch { bivRequester.bringIntoView() }
                    }
                    .bringIntoViewRequester(bivRequester),
            ) { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = LocalColorTheme.current.secondary[200],
                            shape = RoundedCornerShape(14.dp)
                        )
                        .background(
                            color = LocalColorTheme.current.secondary[100],
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 13.dp)
                ) {
                    Text(
                        text = "탈퇴 사유를 적어주세요.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W400,
                        color = LocalColorTheme.current.secondary[400],
                        modifier = Modifier.alpha(if (etcText.isEmpty()) 1f else 0f)
                    )
                    innerTextField.invoke()
                }
            }
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
            .clickable(
                indication = null,
                interactionSource = null,
                onClick = { onSelect(value) },
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomRadioButton(selected = selected == value)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = Color.Black,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.W400,
        )
    }
}

// 유의사항 안내
@Composable
private fun SignOutNotice(
    agreed: Boolean,
    onAgreedChange: (Boolean) -> Unit
) {
    Column(
        Modifier
            .background(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(16.dp))
            .border(1.dp, color = Color(0xFFE1E1E1), shape = RoundedCornerShape(16.dp))
            .padding(13.dp),
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "탈퇴 안내 및 유의사항",
            fontSize = 14.sp,
            fontWeight = FontWeight.W800,
            color = Color.Black,
            lineHeight = 10.sp,
            modifier = Modifier.padding(start = 8.dp),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.padding(horizontal = 4.dp)) { SignOutNoticeText() }
        Spacer(modifier = Modifier.height(10.dp))
        SignOutConsentCheckbox(checked = agreed, onCheckedChange = onAgreedChange)

    }
}

@Composable
private fun SignOutNoticeText() {
    Column {
        SignOutNoticeRow(
            number = 1,
            text = AnnotatedString("탈퇴 아이디는 복구와 재사용이 불가능합니다."),
        )
        SignOutNoticeRow(
            number = 2,
            text = AnnotatedString("삭제된 데이터는 복구되지 않습니다.")
        )
        SignOutNoticeRow(
            number = 3,
            text = AnnotatedString("소셜 로그인 회원의 경우 서비스에서 관리하는 모든 정보가 삭제되며, 같은 소셜 아이디로 재가입시 신규회원으로 가입됩니다.")
        )
    }
}

@Composable
private fun SignOutNoticeRow(number: Int, text: AnnotatedString) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "$number.",
            fontSize = 12.sp,
            lineHeight = 18.sp,
            modifier = Modifier
                .alignBy(FirstBaseline)
                .padding(end = 3.dp),
            color = Color(0xFF4B4B4B),
            fontWeight = FontWeight.W400
        )
        Text(
            text = text,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            modifier = Modifier.alignBy(FirstBaseline),
            color = Color(0xFF4B4B4B),
            fontWeight = FontWeight.W400
        )
    }
}

// 동의 체크박스
@Composable
private fun SignOutConsentCheckbox(
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
private fun CustomCheckboxWithText(
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
            .clickable(
                indication = null,
                interactionSource = null,
                onClick = { onCheckedChange(!checked) },
            ),
    ) {
        Box(
            modifier = Modifier
                .size(17.dp)
                .clip(RoundedCornerShape(6.dp))
                .let {
                    if (checked) it
                    else it.border(
                        width = 1.dp,
                        color = Color(0xFFE1E1E1),
                        shape = RoundedCornerShape(6.dp),
                    )
                }
                .background(if (checked) Color(0xFFB1B1B1) else Color.White),
            contentAlignment = Alignment.Center,
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
private fun SignOutButtonRow(
    isConfirmButtonEnabled: Boolean,
    onCancel: () -> Unit,
    showConfirmDialog: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 취소 버튼 (오렌지 200)
        Box(modifier = Modifier.weight(1f)) {
            CustomButton(
                "취소",
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = LocalColorTheme.current.primary[200],
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = LocalColorTheme.current.primary[200],
                ),
                onClick = onCancel,
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            CustomButton(
                text = "제출",
                isEnabled = isConfirmButtonEnabled,
                onClick = { showConfirmDialog(true) },
            )
        }
    }
}

@Composable
private fun CustomRadioButton(
    selected: Boolean,
) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = LocalColorTheme.current.primary[300],
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(LocalColorTheme.current.primary[400]),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSignOutScreen() {
    ThemeProvider {
        SignOutScreen(
            onLeaveUser = {},
            onCancel = {}
        )
    }
}