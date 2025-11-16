package com.umc.ttatta.app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import kotlinx.coroutines.suspendCancellableCoroutine

object PermissionManager {

    val Context.isCameraPermissionGranted
        get() = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    val Context.isLocationPermissionGranted
        get() = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    val Context.isMediaPermissionGranted
        @RequiresApi(Build.VERSION_CODES.Q) get() = checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED

    val Context.isNotificationPermissionGranted: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || checkSelfPermission(
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    fun Context.checkPermission(vararg permissions: String): Boolean {
        return permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
    }

    suspend fun ComponentActivity.checkPermissionAndTryRequest(vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) continue

            val result = suspendCancellableCoroutine { continuation ->
                val activityResult = registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    continuation.resumeWith(Result.success(isGranted))
                }

                activityResult.launch(permission)
            }

            if (!result) return false
        }

        return true
    }
}