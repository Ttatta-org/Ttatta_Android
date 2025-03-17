package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.CreateCategoryDTO
import com.umc.data.api.dto.server.CreateCategoryResultDTO
import com.umc.data.api.dto.server.GetAllCategoryCountResultDTO
import com.umc.data.api.dto.server.ModifyCategoryDTO
import com.umc.data.api.dto.server.ModifyCategoryResultDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoryApi {
    // 카테고리 생성
    @POST("/categories")
    suspend fun createCategory(
        @Body body: CreateCategoryDTO
    ): BaseResponse<CreateCategoryResultDTO>

    // 카테고리만 삭제
    @DELETE("/categories/{categoryId}")
    suspend fun deleteCategoryOnly(
        @Path("categoryId") categoryId: Long
    ): BaseResponse<Any?>

    // 카테고리 수정
    @PATCH("/categories/{categoryId}")
    suspend fun updateCategory(
        @Path("categoryId") categoryId: Long,
        @Body body: ModifyCategoryDTO
    ): BaseResponse<ModifyCategoryResultDTO>

    // 카테고리별 일기 개수 조회
    @GET("/categories/diary-counts")
    suspend fun getDiaryCount(): BaseResponse<GetAllCategoryCountResultDTO>

    // 카테고리 및 모든 기록 삭제
    @DELETE("/categories/all/{categoryId}")
    suspend fun deleteCategoryAndAllIncludedDiaries(
        @Path("categoryId") categoryId: Long
    ): BaseResponse<Any?>
}