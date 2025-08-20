package com.umc.ttatta.util

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat

fun ComponentActivity.setStatusBarTransparent() {
    window.apply {
        WindowCompat.setDecorFitsSystemWindows(this, false)
        setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}