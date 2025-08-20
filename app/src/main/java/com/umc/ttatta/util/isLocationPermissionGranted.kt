package com.umc.ttatta.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager

val Activity.isLocationPermissionGranted get() =
    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED