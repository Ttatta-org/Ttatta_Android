package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.CheckVerificationCodeRequestDTO
import com.umc.data.api.dto.server.EditRequestDTO
import com.umc.data.api.dto.server.FindIdResultDTO
import com.umc.data.api.dto.server.FindPwRequestDTO
import com.umc.data.api.dto.server.RefreshResultDTO
import com.umc.data.api.dto.server.SendVerificationMailFindIdRequestDTO
import com.umc.data.api.dto.server.SendVerificationMailFindPwRequestDTO
import com.umc.data.api.dto.server.SendVerificationMailSignUpRequestDTO
import com.umc.data.api.dto.server.SignInRequestDTO
import com.umc.data.api.dto.server.SignUpKakaoRequestDTO
import com.umc.data.api.dto.server.SignUpRequestDTO
import com.umc.data.api.dto.server.TokenValidationResultDTO
import com.umc.data.api.dto.server.UserInfoEditResultDTO
import com.umc.data.api.dto.server.UserInfoResultDTO
import com.umc.data.api.dto.server.UserSignInResultDTO
import com.umc.data.api.dto.server.UserSignUpResultDTO
import com.umc.data.api.dto.server.VerifyUsernameOverlapResultDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
    // 카카오 토큰 검증
    @POST("/users/verificate/kakao")
    suspend fun validKakaoToken(
        @Header("OpenId") idToken: String
    ): BaseResponse<TokenValidationResultDTO>

    // 회원가입
    @POST("/users/signup")
    suspend fun signUp(
        @Body body: SignUpRequestDTO
    ): BaseResponse<UserSignUpResultDTO>

    // 회원가입 인증메일 발송
    @POST("/users/signup/verify/send")
    suspend fun sendVerificationMailForSignUp(
        @Body body: SendVerificationMailSignUpRequestDTO
    ): BaseResponse<Any?>

    // 회원가입 인증번호 확인
    @POST("/users/signup/verify/check")
    suspend fun checkVerificationCodeForSignUp(
        @Body body: CheckVerificationCodeRequestDTO
    ): BaseResponse<Any?>

    // 카카오 회원가입
    @POST("/users/signup/kakao")
    suspend fun signUpKakao(
        @Header("OpenId") idToken: String,
        @Body body: SignUpKakaoRequestDTO
    ): BaseResponse<UserSignUpResultDTO>

    // 로그인
    @POST("/users/signin")
    suspend fun signIn(
        @Body body: SignInRequestDTO
    ): BaseResponse<UserSignInResultDTO>

    // 토큰 갱신
    @POST("/users/refresh")
    suspend fun refreshToken(
        @Header("RefreshToken") refreshToken: String
    ): BaseResponse<RefreshResultDTO>

    // PW 찾기용 인증메일 발송
    @POST("/users/find/send-pw")
    suspend fun sendVerificationMailForFindingPassword(
        @Body body: SendVerificationMailFindPwRequestDTO
    ): BaseResponse<Any?>

    // ID 찾기용 인증메일 발송
    @POST("/users/find/send-id")
    suspend fun sendVerificationMailForFindingId(
        @Body body: SendVerificationMailFindIdRequestDTO
    ): BaseResponse<Any?>

    // PW 재설정
    @POST("/users/find/pw")
    suspend fun findPassword(
        @Body body: FindPwRequestDTO
    ): BaseResponse<Any?>

    // ID 찾기
    @POST("/users/find/id")
    suspend fun findId(
        @Body body: CheckVerificationCodeRequestDTO
    ): BaseResponse<FindIdResultDTO>

    // 회원 정보 조회
    @GET("/users/info")
    suspend fun getUserInfo(): BaseResponse<UserInfoResultDTO>

    // 회원 정보 수정
    @PATCH("/users/info")
    suspend fun updateUserInfo(
        @Body body: EditRequestDTO
    ): BaseResponse<UserInfoEditResultDTO>

    // 아이디 중복 확인
    @GET("/users/signup/verify/overlap")
    suspend fun checkIdDuplication(
        @Query("username") username: String
    ): BaseResponse<VerifyUsernameOverlapResultDTO>

    // 아이디 확인(비밀번호 찾기용)
    @GET("/users/find/verify/id")
    suspend fun checkIdDuplicationOnFindingPassword(
        @Query("username") id: String,
    ): BaseResponse<Any?>

    // 회원 탈퇴
    @DELETE("/users")
    suspend fun deleteUser(): BaseResponse<Any?>

    // 로그아웃
    @DELETE("/users/logout")
    suspend fun logout(): BaseResponse<Any?>
}