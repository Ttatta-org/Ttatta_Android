package com.umc.data.api

import com.umc.data.BuildConfig
import com.umc.data.api.dto.naver.LocationSearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface LocationSearchApi {
    @GET("v1/search/local.json")
    suspend fun searchLocationByKeyword(
        @Query("query") keyword: String,
        @Query("display") display: Int = 5,
        @Query("start") start: Int = 1,
        @Query("sort") sort: String = "random",
        @Header("X-Naver-Client-Id") clientId: String = BuildConfig.NAVER_OPEN_API_CLIENT_ID,
        @Header("X-Naver-Client-Secret") clientSecret: String = BuildConfig.NAVER_OPEN_API_CLIENT_SECRET,
    ): LocationSearchResponse
}