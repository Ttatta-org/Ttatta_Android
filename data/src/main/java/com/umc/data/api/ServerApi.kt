package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.*
import com.umc.data.preference.AuthPreference
import okhttp3.MultipartBody
import retrofit2.http.*
import java.time.LocalDateTime

interface ServerApi {
    // User 관련 API
    @POST("/users/signup")
    suspend fun signUp(
        @Body body: SignUpRequestDTO
    ): BaseResponse<UserSignUpResultDTO>

    @POST("/users/signup/kakao")
    suspend fun signUpKakao(
        @Body body: SignUpKakaoRequestDTO
    ): BaseResponse<UserSignUpResultDTO>

    @POST("/users/signin")
    suspend fun signIn(
        @Body body: SignInRequestDTO
    ): BaseResponse<UserSignInResultDTO>

    @POST("/users/signin/kakao")
    suspend fun signInKakao(
        @Body body: SignInKakaoRequestDTO
    ): BaseResponse<UserSignInResultDTO>

    @POST("/users/refresh")
    suspend fun refreshToken(
        @Header("RefreshToken") refreshToken: String
    ): BaseResponse<RefreshResultDTO>

    @POST("/users/code")
    suspend fun sendVerificationCode(
        @Body body: SendVerificationCodeRequestDTO
    ): BaseResponse<SendVerificationCodeResultDTO>

    @GET("/users/info")
    suspend fun getUserInfo(): BaseResponse<UserInfoResultDTO>

    @PATCH("/users/info")
    suspend fun updateUserInfo(
        @Body body: UpdateRequestDTO
    ): BaseResponse<UserInfoResultDTO>

    @GET("/users/verify/pw")
    suspend fun verifyVerificationCodeForPassword(
        @Query("verificationCode") verificationCode: Int
    ): BaseResponse<VerifyVerificationCodeForPasswordResultDTO>

    @GET("/users/verify/id")
    suspend fun verifyVerificationCodeForUsername(
        @Query("verificationCode") verificationCode: Int
    ): BaseResponse<VerifyVerificationCodeForUsernameResultDTO>

    @GET("/users/signup/verify/overlap")
    suspend fun checkUsernameSame(
        @Query("username") username: String
    ): BaseResponse<VerifyUsernameOverlapResultDTO>

    @DELETE("/users")
    suspend fun deleteUser(): BaseResponse<Any?>

    @DELETE("/users/logout")
    suspend fun logout(): BaseResponse<Any?>

    // Diary 관련 API
    @Multipart
    @POST("/diaries/post")
    suspend fun diarySave(
        @Part("request") body: PostDTO,
        @Part image: MultipartBody.Part
    ): BaseResponse<PostResultDTO>

    @Multipart
    @PATCH("/diaries/edit/{diaryId}")
    suspend fun editDiary(
        @Path("diaryId") diaryId: Long,
        @Part("request") body: EditDTO,
        @Part editPhoto: MultipartBody.Part?
    ): BaseResponse<EditResultDTO>

    @GET("/diaries/search/{requestNum}")
    suspend fun getSearchDiary(
        @Path("requestNum") requestNum: Int,
        @Query("searchContent") searchContent: String
    ): BaseResponse<SearchDiaryListDTO>

    @GET("/diaries/map/{requestNum}")
    suspend fun getMapDiary(
        @Path("requestNum") requestNum: Int,
        @Query("request") body: MapDTO
    ): BaseResponse<MapResultDTO>

    @GET("/diaries/keep/{requestNum}")
    suspend fun getKeepDiaryList(
        @Path("requestNum") requestNum: Int,
        @Query("date") date: LocalDateTime?
    ): BaseResponse<KeepDiaryListDTO>

    @DELETE("/diaries/delete/{diaryId}")
    suspend fun deleteDiary(
        @Path("diaryId") diaryId: Long
    ): BaseResponse<Any?>

    // Category 관련 API
    @POST("/categories")
    suspend fun createCategory(
        @Body body: CreateCategoryDTO
    ): BaseResponse<CreateCategoryResultDTO>

    @PATCH("/categories/{categoryId}")
    suspend fun modifyCategory(
        @Path("categoryId") categoryId: Long,
        @Body body: ModifyCategoryDTO
    ): BaseResponse<ModifyCategoryResultDTO>

    @GET("/categories/diary-counts")
    suspend fun getDiaryCount(): BaseResponse<GetAllCategoryCountResultDTO>

    @DELETE("/categories/{categoryId}")
    suspend fun deleteCategory(
        @Path("categoryId") categoryId: Long
    ): BaseResponse<Any?>

    @DELETE("/categories/all/{categoryId}")
    suspend fun deleteAllInCategory(
        @Path("categoryId") categoryId: Long
    ): BaseResponse<Any?>
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
        val response = withCheck { refreshToken(authPreference.refreshToken!!) }
        authPreference.refreshToken = response.refreshToken
        authPreference.accessToken = response.accessToken
        return withCheck { routine() }
    }
}