package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.ChangePinRequestDTO
import com.umc.data.api.dto.server.ChangePinResultDTO
import com.umc.data.api.dto.server.CheckVerificationCodeRequestDTO
import com.umc.data.api.dto.server.DeleteRequestDTO
import com.umc.data.api.dto.server.EditRequestDTO
import com.umc.data.api.dto.server.FindIdResultDTO
import com.umc.data.api.dto.server.FindPwRequestDTO
import com.umc.data.api.dto.server.GetPinResultDTO
import com.umc.data.api.dto.server.IsPendingResultDTO
import com.umc.data.api.dto.server.KaKaoFinalSignUpResultDTO
import com.umc.data.api.dto.server.RefreshResultDTO
import com.umc.data.api.dto.server.SendVerificationMailFindIdRequestDTO
import com.umc.data.api.dto.server.SendVerificationMailFindPwRequestDTO
import com.umc.data.api.dto.server.SendVerificationMailSignUpRequestDTO
import com.umc.data.api.dto.server.SetPinRequestDTO
import com.umc.data.api.dto.server.SetPinResultDTO
import com.umc.data.api.dto.server.SignInRequestDTO
import com.umc.data.api.dto.server.SignUpKakaoRequestDTO
import com.umc.data.api.dto.server.SignUpRequestDTO
import com.umc.data.api.dto.server.UserDeleteResultDTO
import com.umc.data.api.dto.server.UserInfoEditResultDTO
import com.umc.data.api.dto.server.UserInfoResultDTO
import com.umc.data.api.dto.server.UserKaKaoOpenIdResultDTO
import com.umc.data.api.dto.server.UserSignInResultDTO
import com.umc.data.api.dto.server.UserSignUpResultDTO
import com.umc.data.api.dto.server.VerifyUsernameOverlapResultDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
    // 카카오 로그인
    @POST("/users/signup/kakao")
    suspend fun loginWithKakao(
        @Header("OpenId") idToken: String
    ): BaseResponse<UserKaKaoOpenIdResultDTO>

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
    suspend fun checkVerificationCode(
        @Body body: CheckVerificationCodeRequestDTO
    ): BaseResponse<Any?>

    // 카카오 회원가입
    @POST("/users/kakao/signup/nickname")
    suspend fun signUpKakao(
        @Body body: SignUpKakaoRequestDTO
    ): BaseResponse<KaKaoFinalSignUpResultDTO>

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

    // 핀번호 불러오기
    @GET("/users/pin")
    suspend fun getPin(): BaseResponse<GetPinResultDTO>

    // 핀번호 설정하기
    @POST("/users/pin")
    suspend fun setPin(
        @Body body: SetPinRequestDTO
    ): BaseResponse<SetPinResultDTO>

    // 핀번호 변경하기
    @PATCH("/users/pin")
    suspend fun changePin(
        @Body body: ChangePinRequestDTO
    ): BaseResponse<ChangePinResultDTO>

    // 핀번호 삭제하기
    @DELETE("/users/pin")
    suspend fun clearPin(): BaseResponse<Any?>

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

    // 사용자 상태 검증
    @GET("/users/status")
    suspend fun getUserStatus(): BaseResponse<IsPendingResultDTO>

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
    @HTTP(method = "DELETE", path = "/users", hasBody = true)
    suspend fun deleteUser(
        @Body body: DeleteRequestDTO
    ): BaseResponse<UserDeleteResultDTO>

    // 로그아웃
    @DELETE("/users/logout")
    suspend fun logout(): BaseResponse<Any?>
}