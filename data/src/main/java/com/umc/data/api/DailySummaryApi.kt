package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.ChatGPTResponseDTO
import com.umc.data.api.dto.server.DiaryReSummarizeResponseDTO
import com.umc.data.api.dto.server.DiarySummaryResultDTO
import com.umc.data.api.dto.server.SummarizeDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface DailySummaryApi {
    // 하루 요약 재생성
    @PUT("/gpt/summary/reSummary")
    suspend fun regenerateDailySummary(
        @Body body: SummarizeDTO
    ): BaseResponse<DiaryReSummarizeResponseDTO>

    // 하루 요약 생성
    @POST("/gpt/summary")
    suspend fun generateDailySummary(
        @Body body: SummarizeDTO
    ): BaseResponse<ChatGPTResponseDTO>

    // 하루 요약 조회
    @GET("/gpt/get/summary")
    suspend fun getDailySummary(
        @Query("date") date: String
    ): BaseResponse<DiarySummaryResultDTO>
}