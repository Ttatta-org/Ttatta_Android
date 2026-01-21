package com.umc.mypage.util

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class LoadingViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    protected suspend fun <T> runWithLoading(block: suspend () -> T): T {
        _isLoading.value = true

        try {
            return block()
        } finally {
            _isLoading.value = false
        }
    }
}