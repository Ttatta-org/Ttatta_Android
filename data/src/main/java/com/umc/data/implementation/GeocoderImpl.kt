package com.umc.data.implementation

import com.google.gson.GsonBuilder
import com.umc.core.Geocoder
import com.umc.core.model.LocationSearchResult
import com.umc.data.api.GeocodingApi
import com.umc.data.api.LocationSearchApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.pow

class GeocoderImpl: Geocoder {

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val naverCloudClient by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.apigw.ntruss.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GeocodingApi::class.java)
    }

    private val naverOpenApiClient by lazy {
        Retrofit.Builder()
            .baseUrl("https://openapi.naver.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(LocationSearchApi::class.java)
    }

    override suspend fun convertAddressToCoordinate(address: String): Pair<Double, Double> {
        val response = naverCloudClient.getCoordinates(address = address)
        if (response.status != "OK") throw Exception(response.errorMessage)
        return with(response.addresses.first()) { y.toDouble() to x.toDouble() }
    }

    override suspend fun convertCoordinateToAddress(latitude: Double, longitude: Double): String {
        val response = naverCloudClient.getAddress(coordinates = "$longitude,$latitude")
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

    override suspend fun searchLocationByKeyword(keyword: String): List<LocationSearchResult> {
        val response = naverOpenApiClient.searchLocationByKeyword(keyword = keyword)
        return response.items.map {
            LocationSearchResult(
                title = it.title.replace("<b>", "").replace("</b>", ""),
                description = it.description.split(">").last(),
                category = it.category,
                address = it.roadAddress,
                latitude = it.mapy.toLong() / 10.0.pow(it.mapy.length - 2),
                longitude = it.mapx.toLong() / 10.0.pow(it.mapx.length - 3),
            )
        }
    }
}