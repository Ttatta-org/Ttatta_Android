package com.umc.core.util

import android.content.Context
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun Context.showToast(message: String) {
    MainScope().launch {
        val toast = android.widget.Toast.makeText(
            this@showToast,
            message,
            android.widget.Toast.LENGTH_SHORT
        )

        toast.show()
    }
}