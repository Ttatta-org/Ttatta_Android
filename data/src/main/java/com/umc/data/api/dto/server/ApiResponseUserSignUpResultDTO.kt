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
 * @param isSuccess 
 * @param code 
 * @param message 
 * @param result 
 */
data class ApiResponseUserSignUpResultDTO (

    val isSuccess: Boolean? = null,
    val code: String? = null,
    val message: String? = null,
    val result: UserSignUpResultDTO? = null
)