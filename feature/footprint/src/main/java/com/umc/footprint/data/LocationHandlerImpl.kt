package com.umc.footprint.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.umc.footprint.core.LocationHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationHandlerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationSource: FusedLocationProviderClient,
) : LocationHandler {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location {
        return doWithLocationPermission {
            locationSource.getCurrentLocation(
                Priority.PRIORITY_PASSIVE, null
            )
        }.await()
    }

    @SuppressLint("MissingPermission")
    override fun addLocationChangeListener(listener: (Location) -> Unit) {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000
        ).apply {
            setMinUpdateDistanceMeters(3f)
            setWaitForAccurateLocation(true)
        }.build()

        doWithLocationPermission {
            locationSource.requestLocationUpdates(request, listener, Looper.getMainLooper())
        }
    }

    override fun removeLocationChangeListener(listener: (Location) -> Unit) {
        locationSource.removeLocationUpdates(listener)
    }

    private fun <T> doWithLocationPermission(routine: () -> T): T {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw Exception("Location permission is not granted")
        }

        return routine()
    }
}