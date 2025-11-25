package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.ChallengeListResultDTO
import com.umc.data.api.dto.server.CreateChallengeRequestDTO
import com.umc.data.api.dto.server.CreateChallengeResultDTO
import com.umc.data.api.dto.server.FailChallengeListResultDTO
import com.umc.data.api.dto.server.SuccessChallengeResultDTO
import com.umc.data.api.dto.server.GetAllPastChallengeListResultDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ChallengeApi {
    // 금일 챌린지 조회
    @GET("/challenges")
    suspend fun getChallenges(): BaseResponse<ChallengeListResultDTO>

    // 챌린지 생성
    @POST("/challenges")
    suspend fun createChallenge(
        @Body body: CreateChallengeRequestDTO
    ): BaseResponse<CreateChallengeResultDTO>

    // 챌린지 성공
    @PATCH("/challenges/{challengeId}")
    suspend fun successChallenge(
        @Path("challengeId") challengeId: Long
    ): BaseResponse<SuccessChallengeResultDTO>

    // 가장 최근에 실패한 챌린지 5개 조회
    @GET("/challenges/fail")
    suspend fun getFailChallenges(): BaseResponse<FailChallengeListResultDTO>

    // 지난 챌린지 전체 조회
    @GET("/challenges/all")
    suspend fun getAllPastChallenges(): BaseResponse<GetAllPastChallengeListResultDTO>
}