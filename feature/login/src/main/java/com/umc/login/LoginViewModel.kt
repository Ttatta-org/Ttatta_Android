package com.umc.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    // 상태 변수
    var idState = mutableStateOf("")
        private set

    var pwState = mutableStateOf("")
        private set

    var passwordVisible = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf("")
        private set

    // 상태 변경 로직
    fun onIdChange(newId: String) {
        idState.value = newId
    }

    fun onPwChange(newPw: String) {
        pwState.value = newPw
    }

    fun togglePasswordVisibility() {
        passwordVisible.value = !passwordVisible.value
    }

    // 버튼 활성화 여부 계산
    fun isButtonActive(): Boolean {
        return idState.value.isNotBlank() && pwState.value.isNotBlank()
    }

    // 로그인 로직
    fun onLoginClick(): Boolean {
        return if (idState.value == "correctId" && pwState.value == "correctPw") {
            errorMessage.value = ""
            true
        } else {
            errorMessage.value = "아이디 또는 비밀번호를 다시 확인해주세요"
            false
        }
    }
    //LoginIdScreen.Kt
    // 아이디 입력 상태
    var joinidState = mutableStateOf("")
        private set

    // 경고 메시지 상태
    var isWarningVisible = mutableStateOf(false)
        private set

    // 아이디 입력값 변경
    fun onJoinIdChange(newId: String) {
        idState.value = newId
        isWarningVisible.value = newId.length == 15 // 길이가 15일 때 경고 메시지 표시
    }
}
