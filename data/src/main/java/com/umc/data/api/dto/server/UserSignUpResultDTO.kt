/**
 * Ttatta BE API
 * 따따 백엔드 API 명세서
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
package com.umc.data.api.dto.server


/**
 * 
 * @param userId 
 * @param nickname 
 * @param loginType 
 * @param createdAt 
 */
data class UserSignUpResultDTO (

    val userId: Long? = null,
    val nickname: String? = null,
    val loginType: LoginType? = null,
    val createdAt: java.time.LocalDateTime? = null
) {
    /**
    * 
    * Values: REGULAR,KAKAO
    */
    enum class LoginType(val value: String){
        REGULAR("REGULAR"),
        KAKAO("KAKAO");
    }
}