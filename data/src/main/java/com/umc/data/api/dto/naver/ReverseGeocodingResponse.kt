package com.umc.data.api.dto.naver

data class ReverseGeocodingResponse(
    val status: RGStatus,
    val results: List<RGResult>
)

data class RGStatus(
    val code: Int,
    val name: String,
    val message: String
)

data class RGResult(
    val name: String,
    val code: RGCode,
    val region: RGRegion,
    val land: RGLand? = null // Optional field, as "land" might not always be present
)

data class RGCode(
    val id: String,
    val type: String,
    val mappingId: String
)

data class RGRegion(
    val area0: RGArea,
    val area1: RGAreaWithAlias,
    val area2: RGArea,
    val area3: RGArea,
    val area4: RGArea
)

data class RGArea(
    val name: String,
    val coords: RGCoords
)

data class RGAreaWithAlias(
    val name: String,
    val coords: RGCoords,
    val alias: String
)

data class RGCoords(
    val center: RGCenter
)

data class RGCenter(
    val crs: String,
    val x: Double,
    val y: Double
)

data class RGLand(
    val type: String,
    val number1: String,
    val number2: String,
    val addition0: RGAddition,
    val addition1: RGAddition,
    val addition2: RGAddition,
    val addition3: RGAddition,
    val addition4: RGAddition,
    val name: String? = null, // Optional as it might not always be present
    val coords: RGCoords
)

data class RGAddition(
    val type: String,
    val value: String
)
