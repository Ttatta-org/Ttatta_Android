package com.umc.data.implementation

import com.google.gson.GsonBuilder
import com.umc.core.Geocoder
import com.umc.data.BuildConfig
import com.umc.data.api.GeocodingApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GeocoderImpl: Geocoder {

    private val client by lazy {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return@lazy Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GeocodingApi::class.java)
    }

    override suspend fun convertAddressToCoordinate(address: String): Pair<Double, Double> {
        val response = client.getCoordinates(
            address = address,
            keyId = BuildConfig.NAVER_SDK_CLIENT_ID,
            key = BuildConfig.NAVER_SDK_CLIENT_SECRET,
        )
        if (response.status != "OK") throw Exception(response.errorMessage)
        return with(response.addresses.first()) { y.toDouble() to x.toDouble() }
    }

    override suspend fun convertCoordinateToAddress(latitude: Double, longitude: Double): String {
        val response = client.getAddress(
            coordinates = "$longitude,$latitude",
            keyId = BuildConfig.NAVER_SDK_CLIENT_ID,
            key = BuildConfig.NAVER_SDK_CLIENT_SECRET,
        )
        if (response.status.code != 0) throw Exception(response.status.message)
        val address = with(response.results.first()) {
            listOf(
                region.area1.name,
                region.area2.name,
                region.area3.name,
                region.area4.name,
                land?.number1,
                land?.number2,
                land?.addition0?.value,
            ).filter { it?.isNotBlank() ?: false }.joinToString(" ")
        }
        return address
    }
}