package com.umc.footprint.core

import android.location.Location

interface LocationHandler {
    suspend fun getCurrentLocation(): Location
    fun addLocationChangeListener(listener: (Location) -> Unit)
    fun removeLocationChangeListener(listener: (Location) -> Unit)
}