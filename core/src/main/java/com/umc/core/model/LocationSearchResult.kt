package com.umc.core.model

data class LocationSearchResult(
    val title: String,
    val description: String,
    val category: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
)
