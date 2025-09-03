package com.umc.ttatta.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

fun <VM: ViewModel, T> VM.runWithScope(block: suspend VM.() -> T) = this.viewModelScope.launch { block() }