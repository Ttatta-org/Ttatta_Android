package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.*
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

    // (개발용) 테스트 유저 생성
    @POST("/users/testuser")
    suspend fun createTestUser(): BaseResponse<UserSignUpResultDTO>

    // 아이템 생성
    @POST("/items")
    suspend fun makeItem(
        @Body body: MakeItemDTO
    ): BaseResponse<MakeItemResultDTO>

    // 일기 작성
    @Multipart
    @POST("/diaries/post")
    suspend fun createDiary(
        @Part("request") request: PostDTO,
        @Part image: MultipartBody.Part
    ): BaseResponse<PostResultDTO>

    // 챌린지 생성
    @POST("/challenges")
    suspend fun createChallenge(
        @Body body: CreateChallengeRequestDTO
    ): BaseResponse<CreateChallengeResultDTO>

    // 카테고리 생성
    @POST("/categories")
    suspend fun createCategory(
        @Body body: CreateCategoryDTO
    ): BaseResponse<CreateCategoryResultDTO>

    // 회원 정보 조회
    @GET("/users/info")
    suspend fun getUserInfo(): BaseResponse<UserInfoResultDTO>

    // 미소유 아이템 (shop) 조회
    @GET("/items/shop")
    suspend fun getShopItems(): BaseResponse<ItemShopListDTO>

    // 소유 아이템 조회
    @GET("/items/owned")
    suspend fun getOwnedItems(): BaseResponse<ItemMyItemListDTO>

    // 착용한 아이템 조회
    @GET("/items/equipped")
    suspend fun getEquippedItems(): BaseResponse<IdListDTO>

    // 인증메일 발송 (비밀번호 찾기)
    @GET("/users/find/send-pw")
    suspend fun verifyVerificationCodeForPassword(
        @Body body: SendVerificationMailFindPwRequestDTO,
        @Query("verificationCode") verificationCode: Int
    ): BaseResponse<Any?>

    // 인증번호 확인 (아이디 찾기)
    @GET("/users/find/send-id")
    suspend fun verifyVerificationCodeForUsername(
        @Body body: CheckVerificationCodeRequestDTO
    ): BaseResponse<FindIdResultDTO>

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

    // 금일 챌린지 조회
    @GET("/challenges")
    suspend fun getChallenges(): BaseResponse<ChallengeListResultDTO>

    // 가장 최근에 실패한 챌린지 5개 조회
    @GET("/challenges/fail")
    suspend fun getFailChallenges(): BaseResponse<FailChallengeListResultDTO>

    // 카테고리별 일기 개수 조회
    @GET("/categories/diary-counts")
    suspend fun getDiaryCount(): BaseResponse<GetAllCategoryCountResultDTO>

    // 회원 정보 수정
    @PATCH("/users/info")
    suspend fun updateUserInfo(
        @Body body: EditRequestDTO
    ): BaseResponse<UserInfoEditResultDTO>

    // 아이템 구매
    @PATCH("/items/{itemId}")
    suspend fun buyItem(
        @Path("itemId") itemId: Long
    ): BaseResponse<ItemBuyResultDTO>

    // 아이템 착용
    @PATCH("/items/equip/{itemId}")
    suspend fun equipItem(
        @Path("itemId") itemId: Long
    ): BaseResponse<ItemEquipResultDTO>

    // 아이템 해제
    @PATCH("/items/disrobe/{itemId}")
    suspend fun disrobeItem(
        @Path("itemId") itemId: Long
    ): BaseResponse<ItemDisrobeResultDTO>

    // 일기 수정
    @Multipart
    @PATCH("/diaries/edit/{diaryId}")
    suspend fun updateDiary(
        @Path("diaryId") diaryId: Long,
        @Part("request") request: EditDTO,
        @Part editPhoto: MultipartBody.Part?
    ): BaseResponse<EditResultDTO>

    // 챌린지 성공
    @PATCH("/challenges/{challengeId}")
    suspend fun successChallenge(
        @Path("challengeId") challengeId: Long
    ): BaseResponse<SuccessChallengeResultDTO>

    // 카테고리 수정
    @PATCH("/categories/{categoryId}")
    suspend fun updateCategory(
        @Path("categoryId") categoryId: Long,
        @Body body: ModifyCategoryDTO
    ): BaseResponse<ModifyCategoryResultDTO>

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

    // 카카오 토큰 검증
    @POST("/users/verificate/kakao")
    suspend fun validKakaoToken(
        @Header("OpneId") idToken: String
    ): BaseResponse<TokenValidationResultDTO>

    // 회원가입 인증메일 발송
    @POST("/users/signup/verify/send")
    suspend fun sendVerificationMailSignUp(
        @Body body: SendVerificationMailSignUpRequestDTO
    ): BaseResponse<Any?>

    // 회원가입 인증번호 확인
    @POST("/users/signup/verify/check")
    suspend fun checkVerificationCodeSignUp(
        @Body body: CheckVerificationCodeRequestDTO
    ): BaseResponse<Any?>

    // ID 찾기용 인증메일 발송
    @POST("/users/find/send-id")
    suspend fun sendVerificationMailFindId(
        @Body body: SendVerificationMailFindIdRequestDTO
    ): BaseResponse<Any?>

    // PW 찾기용 인증메일 발송
    @POST("/users/find/send-pw")
    suspend fun sendVerificationMailFindPw(
        @Body body: SendVerificationMailFindPwRequestDTO
    ): BaseResponse<Any?>

    // ID 찾기
    @POST("/users/find/id")
    suspend fun findId(
        @Body body: CheckVerificationCodeRequestDTO
    ): BaseResponse<FindIdResultDTO>

    // PW 재설정
    @POST("/users/find/pw")
    suspend fun findPw(
        @Body body: FindPwRequestDTO
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
        authPreference.refreshToken = response.refreshToken!!
        authPreference.accessToken = response.accessToken!!
        return withCheck { routine() }
    }
}