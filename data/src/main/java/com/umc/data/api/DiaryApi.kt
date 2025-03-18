package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.DairyDateListResultDTO
import com.umc.data.api.dto.server.EditDTO
import com.umc.data.api.dto.server.EditResultDTO
import com.umc.data.api.dto.server.FootprintDiaryListDTO
import com.umc.data.api.dto.server.KeepDiaryListDTO
import com.umc.data.api.dto.server.MapResultDTO
import com.umc.data.api.dto.server.PostDTO
import com.umc.data.api.dto.server.PostResultDTO
import com.umc.data.api.dto.server.SearchDiaryListDTO
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

interface DiaryApi {
    // 일기 작성
    @Multipart
    @POST("/diaries/post")
    suspend fun createDiary(
        @Part("request") request: PostDTO,
        @Part image: MultipartBody.Part
    ): BaseResponse<PostResultDTO>

    // 일기 수정
    @Multipart
    @PATCH("/diaries/edit/{diaryId}")
    suspend fun updateDiary(
        @Path("diaryId") diaryId: Long,
        @Part("request") request: EditDTO,
        @Part editPhoto: MultipartBody.Part?
    ): BaseResponse<EditResultDTO>

    // 일기 검색
    @GET("/diaries/search/{requestNum}")
    suspend fun getSearchDiaryList(
        @Path("requestNum") requestNum: Int,
        @Query("searchContent") searchContent: String
    ): BaseResponse<SearchDiaryListDTO>

    // 일기 지도
    @GET("/diaries/map/{requestNum}")
    suspend fun getMapDiary(
        @Path("requestNum") requestNum: Int,
        @Query("clusterId") clusterId: Long,
        @Query("diaryCategoryId") diaryCategoryId: Long?
    ): BaseResponse<MapResultDTO>

    // 일기 보관함 조회
    @GET("/diaries/keep/{requestNum}")
    suspend fun getKeepDiaryList(
        @Path("requestNum") requestNum: Int,
        @Query("date") date: LocalDateTime?
    ): BaseResponse<KeepDiaryListDTO>

    // 발자국 전체 조회
    @GET("/diaries/footprint")
    suspend fun getFootprintDiaryList(
        @Query("diaryCategoryId") diaryCategoryId: Long?
    ): BaseResponse<FootprintDiaryListDTO>

    // 전체 일기 날짜 조회
    @GET("/diaries/date")
    suspend fun getDiariesDate(): BaseResponse<DairyDateListResultDTO>

    // 일기 삭제
    @DELETE("/diaries/delete/{diaryId}")
    suspend fun deleteDiary(
        @Path("diaryId") diaryId: Long
    ): BaseResponse<Any?>
}