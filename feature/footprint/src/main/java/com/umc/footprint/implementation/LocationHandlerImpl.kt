package com.umc.footprint.implementation

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.umc.footprint.core.LocationHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationHandlerImpl @Inject constructor(
    private val locationSource: FusedLocationProviderClient,
) : LocationHandler {

    private var isCalculatingLocation = false

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location {
        if (!isCalculatingLocation) {
            isCalculatingLocation = true
            CoroutineScope(Dispatchers.IO).launch {
                locationSource.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                ).addOnCompleteListener {
                    isCalculatingLocation = false
                }
            }
        }
        return locationSource.lastLocation.await()
    }

    @SuppressLint("MissingPermission")
    override fun addLocationChangeListener(listener: (Location) -> Unit) {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000
        ).apply {
            setMinUpdateDistanceMeters(3f)
            setWaitForAccurateLocation(true)
        }.build()

        locationSource.requestLocationUpdates(request, listener, Looper.getMainLooper())
    }

    override fun removeLocationChangeListener(listener: (Location) -> Unit) {
        locationSource.removeLocationUpdates(listener)
    }
}