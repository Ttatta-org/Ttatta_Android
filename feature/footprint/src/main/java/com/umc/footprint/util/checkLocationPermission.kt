package com.umc.footprint.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat

fun ActivityResultLauncher<String>.checkLocationPermission(
    context: Activity,
    requestOnNotGranted: Boolean = ActivityCompat.shouldShowRequestPermissionRationale(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
): Boolean {
    val result = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!result && requestOnNotGranted) launch(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    return result
}