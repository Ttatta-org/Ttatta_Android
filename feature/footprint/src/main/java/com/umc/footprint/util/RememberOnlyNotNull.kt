package com.umc.footprint.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun <T> rememberOnlyNotNull(value: T?): T? {
    var state by remember { mutableStateOf<T?>(null) }
    if (value != null && value != state) state = value
    return state
}