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
 * @param kakaoToken 
 * @param nickname 
 */
data class SignUpKakaoRequestDTO (

    val kakaoToken: String? = null,
    val nickname: String? = null
)