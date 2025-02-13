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
import androidx.lifecycle.viewModelScope
import com.umc.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    // 상태 변수
    private val _idState = MutableStateFlow("")
    val idState: StateFlow<String> = _idState.asStateFlow()

    private val _pwState = MutableStateFlow("")
    val pwState: StateFlow<String> = _pwState.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isButtonActive = MutableStateFlow(false)
    val isButtonActive: StateFlow<Boolean> = _isButtonActive.asStateFlow()

    fun onIdChange(newId: String) {
        _idState.value = newId
        updateButtonState()
    }

    fun onPwChange(newPw: String) {
        _pwState.value = newPw
        updateButtonState()
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    private fun updateButtonState() {
        _isButtonActive.value = _idState.value.isNotBlank() && _pwState.value.isNotBlank()
    }

    fun onLoginClick(
        onSucceed: () -> Unit,
        onFailed: () -> Unit,
    ) {
        viewModelScope.launch {
            try {
                userRepository.login(
                    id = idState.value,
                    password = pwState.value
                )
                onSucceed()
            } catch (e: Exception) {
                onFailed()
            }
        }
    }
}