package com.umc.core.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun <VM : ViewModel, T> VM.runWithScope(block: suspend VM.() -> T) {
    this.viewModelScope.launch(Dispatchers.IO) { block() }
}