package com.umc.data.api

import com.umc.data.api.dto.GeocodingResponse
import com.umc.data.api.dto.ReverseGeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GeocodingApi {
    @GET("map-geocode/v2/geocode")
    suspend fun getCoordinates(
        @Query(value = "query") address: String,
        @Header(value = "x-ncp-apigw-api-key-id") keyId: String,
        @Header(value = "x-ncp-apigw-api-key") key: String,
        @Header(value = "Accept") accept: String = "application/json",
    ): GeocodingResponse

    @GET("map-reversegeocode/v2/gc")
    suspend fun getAddress(
        @Query(value = "coords") coordinates: String,
        @Header(value = "x-ncp-apigw-api-key-id") keyId: String,
        @Header(value = "x-ncp-apigw-api-key") key: String,
        @Query(value = "output") format: String = "json",
        @Query(value = "orders") orders: String = "roadaddr",
    ): ReverseGeocodingResponse
}