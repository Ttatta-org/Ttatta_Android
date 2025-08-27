package com.umc.ttatta.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

val Activity.isMediaPermissionGranted @RequiresApi(Build.VERSION_CODES.Q) get() =
    checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED