package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.CreateCategoryDTO
import com.umc.data.api.dto.server.CreateCategoryResultDTO
import com.umc.data.api.dto.server.EditDTO
import com.umc.data.api.dto.server.EditResultDTO
import com.umc.data.api.dto.server.FootprintDiaryListDTO
import com.umc.data.api.dto.server.GetAllCategoryCountResultDTO
import com.umc.data.api.dto.server.KeepDiaryListDTO
import com.umc.data.api.dto.server.MapResultDTO
import com.umc.data.api.dto.server.ModifyCategoryDTO
import com.umc.data.api.dto.server.ModifyCategoryResultDTO
import com.umc.data.api.dto.server.PostDTO
import com.umc.data.api.dto.server.PostResultDTO
import com.umc.data.api.dto.server.RefreshResultDTO
import com.umc.data.api.dto.server.SearchDiaryListDTO
import com.umc.data.api.dto.server.SendVerificationCodeRequestDTO
import com.umc.data.api.dto.server.SendVerificationCodeResultDTO
import com.umc.data.api.dto.server.SignInKakaoRequestDTO
import com.umc.data.api.dto.server.SignInRequestDTO
import com.umc.data.api.dto.server.SignUpKakaoRequestDTO
import com.umc.data.api.dto.server.SignUpRequestDTO
import com.umc.data.api.dto.server.UpdateRequestDTO
import com.umc.data.api.dto.server.UserInfoResultDTO
import com.umc.data.api.dto.server.UserSignInResultDTO
import com.umc.data.api.dto.server.UserSignUpResultDTO
import com.umc.data.api.dto.server.VerifyUsernameOverlapResultDTO
import com.umc.data.api.dto.server.VerifyVerificationCodeForPasswordResultDTO
import com.umc.data.api.dto.server.VerifyVerificationCodeForUsernameResultDTO
import com.umc.data.preference.AuthPreference
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

interface ServerApi {
    // 회원가입
    @POST("/users/signup")
    suspend fun signUp(
        @Body body: SignUpRequestDTO
    ): BaseResponse<UserSignUpResultDTO>

    // 카카오 회원가입
    @POST("/users/signup/kakao")
    suspend fun signUpKakao(
        @Body body: SignUpKakaoRequestDTO
    ): BaseResponse<UserSignUpResultDTO>

    // 로그인
    @POST("/users/signin")
    suspend fun signIn(
        @Body body: SignInRequestDTO
    ): BaseResponse<UserSignInResultDTO>

    // 카카오 로그인
    @POST("/users/signin/kakao")
    suspend fun signInKakao(
        @Body body: SignInKakaoRequestDTO
    ): BaseResponse<UserSignInResultDTO>

    // 토큰 갱신
    @POST("/users/refresh")
    suspend fun refreshToken(
        @Header("RefreshToken") refreshToken: String
    ): BaseResponse<RefreshResultDTO>

    // 인증번호 발송
    @POST("/users/code")
    suspend fun sendVerificationCode(
        @Body body: SendVerificationCodeRequestDTO
    ): BaseResponse<SendVerificationCodeResultDTO>

    // 일기 작성
    @Multipart
    @POST("/diaries/post")
    suspend fun createDiary(
        @Part("request") request: PostDTO,
        @Part image: MultipartBody.Part
    ): BaseResponse<PostResultDTO>

    // 카테고리 생성
    @POST("/categories")
    suspend fun createCategory(
        @Body body: CreateCategoryDTO
    ): BaseResponse<CreateCategoryResultDTO>

    // 회원 정보 조회
    @GET("/users/info")
    suspend fun getUserInfo(): BaseResponse<UserInfoResultDTO>

    // 회원 정보 수정
    @PATCH("/users/info")
    suspend fun updateUserInfo(
        @Body body: UpdateRequestDTO
    ): BaseResponse<UserInfoResultDTO>

    // 일기 수정
    @Multipart
    @PATCH("/diaries/edit/{diaryId}")
    suspend fun updateDiary(
        @Path("diaryId") diaryId: Long,
        @Part("request") request: EditDTO,
        @Part editPhoto: MultipartBody.Part?
    ): BaseResponse<EditResultDTO>

    // 카테고리 수정
    @PATCH("/categories/{categoryId}")
    suspend fun updateCategory(
        @Path("categoryId") categoryId: Long,
        @Body body: ModifyCategoryDTO
    ): BaseResponse<ModifyCategoryResultDTO>

    // 인증번호 확인 (비밀번호 찾기)
    @GET("/users/verify/pw")
    suspend fun verifyVerificationCodeForPassword(
        @Query("verificationCode") verificationCode: Int
    ): BaseResponse<VerifyVerificationCodeForPasswordResultDTO>

    // 인증번호 확인 (아이디 찾기)
    @GET("/users/verify/id")
    suspend fun verifyVerificationCodeForUsername(
        @Query("verificationCode") verificationCode: Int
    ): BaseResponse<VerifyVerificationCodeForUsernameResultDTO>

    // 아이디 중복 확인
    @GET("/users/signup/verify/overlap")
    suspend fun checkUsernameSame(
        @Query("username") username: String
    ): BaseResponse<VerifyUsernameOverlapResultDTO>

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
        @Query("clusterId") clusterId: Long
    ): BaseResponse<MapResultDTO>

    // 일기 보관함 조회
    @GET("/diaries/keep/{requestNum}")
    suspend fun getKeepDiaryList(
        @Path("requestNum") requestNum: Int,
        @Query("date") date: LocalDateTime?
    ): BaseResponse<KeepDiaryListDTO>

    // 발자국 전체 조회
    @GET("/diaries/footprint")
    suspend fun getFootprintDiaryList(): BaseResponse<FootprintDiaryListDTO>

    // 카테고리별 일기 개수 조회
    @GET("/categories/diary-counts")
    suspend fun getDiaryCount(): BaseResponse<GetAllCategoryCountResultDTO>

    // 회원 탈퇴
    @DELETE("/users")
    suspend fun deleteUser(): BaseResponse<Any?>

    // 로그아웃
    @DELETE("/users/logout")
    suspend fun logout(): BaseResponse<Any?>

    // 일기 삭제
    @DELETE("/diaries/delete/{diaryId}")
    suspend fun deleteDiary(
        @Path("diaryId") diaryId: Long
    ): BaseResponse<Any?>

    // 카테고리 및 모든 기록 삭제
    @DELETE("/categories/all/{categoryId}")
    suspend fun deleteCategoryAndAllIncludedDiaries(
        @Path("categoryId") categoryId: Long
    ): BaseResponse<Any?>

    // 카테고리만 삭제
    @DELETE("/categories/{categoryId}")
    suspend fun deleteCategoryOnly(
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