package com.umc.data.api.dto.naver

data class GeocodingResponse(
    val status: String,
    val meta: GMeta,
    val addresses: List<GAddress>,
    val errorMessage: String
)

data class GMeta(
    val totalCount: Int,
    val page: Int,
    val count: Int
)

data class GAddress(
    val roadAddress: String,
    val jibunAddress: String,
    val englishAddress: String,
    val addressElements: List<GAddressElement>,
    val x: String,
    val y: String,
    val distance: Double
)

data class GAddressElement(
    val types: List<String>,
    val longName: String,
    val shortName: String,
    val code: String
)
