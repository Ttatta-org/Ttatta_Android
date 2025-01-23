package com.umc.core

interface Geocoder {
    suspend fun convertCoordinateToAddress(latitude: Double, longitude: Double): String
    suspend fun convertAddressToCoordinate(address: String): Pair<Double, Double>
}