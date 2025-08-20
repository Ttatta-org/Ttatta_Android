package com.umc.ttatta.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager

val Activity.isCameraPermissionGranted get() =
    checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED