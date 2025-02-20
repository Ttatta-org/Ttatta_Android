package com.umc.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.login.R

@Composable
fun LoginInputTextField( // 로그인 화면 텍스트필드
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean,
    passwordVisible: Boolean = false,
    onPasswordToggleClick: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val lineColor = colorResource(R.color.gray_500)
    // 전체를 감싸는 박스
    Box(
        modifier = Modifier
            .width(310.dp)
            .height(52.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1) BasicTextField: 텍스트와 커서가 중앙 정렬
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Black
            ),
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = if (isPassword) {
                KeyboardOptions(keyboardType = KeyboardType.Password)
            } else {
                KeyboardOptions.Default
            },
            cursorBrush = SolidColor(Color.Black),
            modifier = Modifier
                .matchParentSize() // 박스와 같은 크기로 잡아서 중앙정렬 효과
                .onFocusChanged { isFocused = it.isFocused }
                // 밑줄(또는 테두리) 등은 직접 꾸며야 함
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(x = size.width, y = y),
                        strokeWidth = strokeWidth
                    )
                },
            // decorationBox를 이용해 placeholder 표시
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    if (value.isEmpty() && !isFocused) {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            fontWeight = FontWeight(600),
                            color = colorResource(R.color.gray_500),
                            textAlign = TextAlign.Center
                        )
                    }
                    innerTextField()
                }
            }
        )

        // 2) 비밀번호 토글 아이콘(오른쪽 끝)
        if (isPassword) {
            IconButton(
                onClick = { onPasswordToggleClick?.invoke() },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Image(
                    painter = painterResource(
                        id = if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                    ),
                    contentDescription = "Toggle Password Visibility"
                )
            }
        }
    }
}

@Composable
fun NicknameInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onImeAction: () -> Unit,
    placeholder: String,
    isWarning: Boolean,
    errorMessage: String?,
    isLoading: Boolean
) {
    var isFocused by remember { mutableStateOf(false) } // ✅ 포커스 상태 추가

    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = if (isWarning) colorResource(R.color.negativeRed) else Color.Black
        ),
        placeholder = {
            if (!isFocused && value.isEmpty()) { // ✅ 포커스가 없을 때만 플레이스홀더 표시
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
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onImeAction() }),
        modifier = Modifier
            .width(310.dp)
            .height(51.dp)
            .onFocusChanged { isFocused = it.isFocused },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = colorResource(R.color.gray_500),
            unfocusedIndicatorColor = colorResource(R.color.gray_500),
            cursorColor = if (isWarning) colorResource(R.color.negativeRed) else Color.Black
        ),
    )
}

@Composable
fun IdInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onImeAction: () -> Unit,
    placeholder: String,
    isWarning: Boolean,
    errorMessage: String?,
    isLoading: Boolean,
    isCheckButtonVisible: Boolean
) {
    var isFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // @Composable 함수들은 drawBehind 내에서 직접 쓸 수 없으므로, 미리 가져오기
    val negativeRed = colorResource(R.color.negativeRed)
    val orange200 = colorResource(R.color.orange_200)
    val gray500 = colorResource(R.color.gray_500)

    // 전체를 감싸는 Box (디자인에 맞춰 사이즈·정렬 지정)
    Box(
        modifier = Modifier
            .width(310.dp)
            .height(51.dp),
        contentAlignment = Alignment.Center
    ) {
        // ① BasicTextField: 텍스트와 커서를 "가운데 정렬"하도록 설정
        BasicTextField(
            value = value,
            onValueChange = {
                // 글자수 제한
                if (it.length <= 16) {
                    onValueChange(it)
                }
            },
            singleLine = true,
            // IME 액션 설정
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    onImeAction()
                }
            ),
            // 경고 모드(isWarning)이면 빨간색, 아니면 기본 검정색
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = if (isWarning) negativeRed else Color.Black
            ),
            cursorBrush = SolidColor(
                if (isWarning) negativeRed else Color.Black
            ),
            modifier = Modifier
                // Box의 크기에 맞춰서 꽉 채움 -> 중앙 정렬 효과
                .matchParentSize()
                // 포커스 감지
                .onFocusChanged { isFocused = it.isFocused }
                // (선택) 밑줄 등 직접 그릴 때 사용
                .drawBehind {
                    val strokeWidth = 1.5.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = gray500,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                },
            // decorationBox로 placeholder를 직접 표시
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    // 값이 비어 있고 포커스가 없을 때 플레이스홀더
                    if (value.isEmpty() && !isFocused) {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            fontWeight = FontWeight(600),
                            color = gray500,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(bottom = 7.dp) // 기존 코드와 동일
                        )
                    }
                    // 실제 텍스트 입력 필드
                    innerTextField()
                }
            }
        )

        // ② 오른쪽 아이콘 영역: 로딩 or "중복 확인"
        // BasicTextField 자체가 화면 전체를 차지하고 있으므로,
        // 아이콘만 따로 Box 정렬해서 배치
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = orange200,
                        strokeWidth = 2.dp
                    )
                }
                isCheckButtonVisible -> {
                    Text(
                        text = "중복 확인",
                        fontSize = 12.sp,
                        fontWeight = FontWeight(400),
                        color = gray500,
                        modifier = Modifier.clickable {
                            // 중복 확인 버튼 클릭 시
                            keyboardController?.hide()
                            onImeAction()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PwInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean,
    passwordVisible: Boolean = false,
    onPasswordToggleClick: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    // Composable 영역에서 리소스 색상 미리 가져오기
    val gray500 = colorResource(R.color.gray_500)

    // 전체 래핑하는 Box
    Box(
        modifier = Modifier
            .width(310.dp)
            .height(51.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1) BasicTextField: 텍스트와 커서를 "가운데 정렬"
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Black
            ),
            // 비밀번호 모드 & passwordVisible 여부에 따라 비주얼 변환
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            // 비밀번호 모드면 키보드 유형을 Password로 설정
            keyboardOptions = if (isPassword) {
                KeyboardOptions(keyboardType = KeyboardType.Password)
            } else {
                KeyboardOptions.Default
            },
            // 커서 색상 지정
            cursorBrush = SolidColor(Color.Black),
            modifier = Modifier
                // Box의 크기에 맞춰서 꽉 채움 -> 중앙 정렬 효과
                .matchParentSize()
                // 포커스 감지
                .onFocusChanged { isFocused = it.isFocused }
                // (선택) 밑줄 등 직접 그릴 때 사용
                .drawBehind {
                    val strokeWidth = 1.5.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = gray500,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // 값이 없고 포커스가 없으면 placeholder 출력
                    if (value.isEmpty() && !isFocused) {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(600),
                            color = gray500,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 7.dp)
                        )
                    }
                    // 실제 입력 텍스트 필드
                    innerTextField()
                }
            }
        )

        // 2) 비밀번호 토글 아이콘: 오른쪽 끝
        if (isPassword) {
            IconButton(
                onClick = { onPasswordToggleClick?.invoke() },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            ) {
                Image(
                    painter = painterResource(
                        if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                    ),
                    contentDescription = "Toggle Password Visibility"
                )
            }
        }
    }
}

@Composable
fun NameInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorMessage: String?
) {
    var isFocused by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = { if (it.length <= 9) onValueChange(it) },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = if (errorMessage != null) colorResource(R.color.negativeRed) else Color.Black
        ),
        placeholder = {
            if (!isFocused && value.isEmpty()){
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(R.color.gray_500)
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text, // 텍스트 입력만 가능 (숫자 제외)
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .width(310.dp)
            .height(51.dp)
            .onFocusChanged { isFocused = it.isFocused },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = colorResource(R.color.gray_500),
            unfocusedIndicatorColor = colorResource(R.color.gray_500),
            cursorColor = if (errorMessage != null) Color.Black else Color.Black
        )
    )
}

@Composable
fun EmailInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        readOnly = readOnly,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = Color.Black
        ),
        placeholder = {
            if (!isFocused&&value.isEmpty()) {
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
            }
        },
        keyboardOptions = KeyboardOptions.Default,
        modifier = Modifier
            .width(140.dp)
            .height(51.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = colorResource(R.color.gray_500),
            unfocusedIndicatorColor = colorResource(R.color.gray_500),
            cursorColor = Color.Black
        )
    )
}

@Composable
fun CertiInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    timer: Int,
    errorMessage: String?
) {
    // Composable 스코프에서 리소스 색상 미리 가져오기
    val gray500 = colorResource(R.color.gray_500)
    val negativeRed = colorResource(R.color.negativeRed)

    var isFocused by remember { mutableStateOf(false) }

    // 전체를 감싸는 박스
    Box(
        modifier = Modifier
            .width(310.dp)
            .height(51.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1) BasicTextField: 가운데 정렬, 플레이스홀더 표시
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color.Black
            ),
            cursorBrush = SolidColor(Color.Black),
            modifier = Modifier
                // 부모(Box)와 동일한 크기로 맞춰, 중앙정렬 효과
                .matchParentSize()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
                // 밑줄(하단 라인) 직접 그리기
                .drawBehind {
                    val strokeWidth = 1.5.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = gray500,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                },
            // decorationBox로 placeholder를 표시
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    if (!isFocused && value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(600),
                            color = gray500,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    // 실제 입력 텍스트
                    innerTextField()
                }
            }
        )

        // 2) 오른쪽 끝 타이머 표시 (분:초)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 6.dp)
        ) {
            Text(
                text = "${timer / 60}:${String.format("%02d", timer % 60)}",
                color = if (timer > 0) gray500 else negativeRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // 3) 에러 메시지가 있다면, 아래쪽에 표시(선택 사항)
    if (!errorMessage.isNullOrBlank()) {
        Text(
            text = errorMessage,
            color = negativeRed,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(top = 4.dp)
        )
    }
}