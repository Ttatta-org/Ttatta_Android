package com.umc.core

import com.umc.core.model.LocationSearchResult

interface Geocoder {
    suspend fun convertCoordinateToAddress(latitude: Double, longitude: Double): String
    suspend fun convertAddressToCoordinate(address: String): Pair<Double, Double>
    suspend fun searchLocationByKeyword(keyword: String): List<LocationSearchResult>
}