package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.*
import com.umc.data.preference.AuthPreference
import okhttp3.MultipartBody
import retrofit2.http.*

interface ServerApi {
    // 유저 관련 API
    @POST("/users/signup")
    suspend fun join(
        @Body body: SignUpRequestDTO
    ): BaseResponse<UserSignUpResultDTO>

    @POST("/users/signup/kakao")
    suspend fun joinWithKakao(
        @Body body: SignUpKakaoRequestDTO
    ): BaseResponse<UserSignUpResultDTO>

    @POST("/users/signin")
    suspend fun login(
        @Body body: SignInRequestDTO
    ): BaseResponse<UserSignInResultDTO>

    @POST("/users/signin/kakao")
    suspend fun loginWithKakao(
        @Body body: SignInKakaoRequestDTO
    ): BaseResponse<UserSignInResultDTO>

    @POST("/users/refresh")
    suspend fun refreshToken(
        @Header("RefreshToken") refreshToken: String,
        @Header("AccessToken") accessToken: String
    ): BaseResponse<RefreshResultDTO>

    @POST("/users/code")
    suspend fun sendVerificationCode(
        @Body body: SendVerificationCodeRequestDTO
    ): BaseResponse<SendVerificationCodeResultDTO>

    @GET("/users/{userId}")
    suspend fun getUserInfo(
        @Path("userId") userId: Long
    ): BaseResponse<UserInfoResultDTO>

    @PATCH("/users/{userId}")
    suspend fun updateUserInfo(
        @Path("userId") userId: Long,
        @Body body: UpdateRequestDTO
    ): BaseResponse<UserInfoResultDTO>

    @DELETE("/users/{userId}")
    suspend fun deleteUser(
        @Path("userId") userId: Long
    ): BaseResponse<Unit>

    @GET("/users/verify/pw")
    suspend fun verifyPasswordCode(
        @Query("verificationCode") code: Int
    ): BaseResponse<VerifyVerificationCodeForPasswordResultDTO>

    @GET("/users/verify/id")
    suspend fun verifyUsernameCode(
        @Query("verificationCode") code: Int
    ): BaseResponse<VerifyVerificationCodeForUsernameResultDTO>

    @GET("/users/signup/verify/overlap")
    suspend fun checkUsername(
        @Query("username") username: String
    ): BaseResponse<VerifyUsernameOverlapResultDTO>

    @DELETE("/users/logout")
    suspend fun logout(
        @Body body: LogoutRequestDTO
    ): BaseResponse<Unit>

    // 일기 관련 API
    @POST("/diaries/post")
    @Multipart
    suspend fun postDiary(
        @Part("request") request: PostDTO,
        @Part image: MultipartBody.Part
    ): BaseResponse<PostResultDTO>

    @PATCH("/diaries/edit/{diaryId}")
    suspend fun editDiary(
        @Path("diaryId") diaryId: Long,
        @Body body: EditDTO
    ): BaseResponse<EditResultDTO>

    @DELETE("/diaries/delete/{diaryId}")
    suspend fun deleteDiary(
        @Path("diaryId") diaryId: Long
    ): BaseResponse<Unit>

    @GET("/diaries/search/{requestNum}")
    suspend fun searchDiary(
        @Path("requestNum") requestNum: Int,
        @Query("request") request: SearchDTO
    ): BaseResponse<SearchResultDTO>

    @GET("/diaries/map/{requestNum}")
    suspend fun getMapDiary(
        @Path("requestNum") requestNum: Int,
        @Query("request") request: MapDTO
    ): BaseResponse<MapResultDTO>

    @GET("/diaries/keep/{requestNum}")
    suspend fun getKeepDiary(
        @Path("requestNum") requestNum: Int,
        @Query("request") request: KeepDTO
    ): BaseResponse<KeepResultDTO>

    // 카테고리 관련 API
    @POST("/categories/")
    suspend fun createCategory(
        @Body body: CreateCategoryDTO
    ): BaseResponse<CreateCategoryResultDTO>

    @DELETE("/categories/{categoryId}")
    suspend fun deleteCategory(
        @Path("categoryId") categoryId: Long
    ): BaseResponse<Unit>

    @DELETE("/categories/all/{categoryId}")
    suspend fun deleteCategoryWithDiaries(
        @Path("categoryId") categoryId: Long
    ): BaseResponse<Unit>

    @PATCH("/categories/{categoryId}")
    suspend fun modifyCategory(
        @Path("categoryId") categoryId: Long,
        @Body body: ModifyCategoryDTO
    ): BaseResponse<ModifyCategoryResultDTO>

    @GET("/categories/diary-counts")
    suspend fun getCategoryCounts(): BaseResponse<GetAllCategoryCountResultDTO>
}

suspend fun <T> ServerApi.withCheck(
    getter: suspend ServerApi.() -> BaseResponse<T>
): T {
    val response = getter()
    if (!response.isSuccess) throw Exception(response.message)
    return response.result
}

suspend fun <T> ServerApi.withAuth(
    authPreference: AuthPreference,
    routine: suspend ServerApi.() -> BaseResponse<T>,
): T {
    try {
        return withCheck { routine() }
    } catch (_: Exception) {
        val response = withCheck {
            refreshToken(
                refreshToken = authPreference.refreshToken!!,
                accessToken = authPreference.accessToken!!
            )
        }
        authPreference.refreshToken = response.refreshToken
        authPreference.accessToken = response.accessToken
        return withCheck { routine() }
    }
}