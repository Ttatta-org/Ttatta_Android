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
 * @param diaryId 
 * @param date 
 * @param content 
 * @param image 
 * @param locationName 
 */
data class KeepDiaryDTO (

    val diaryId: Long? = null,
    val date: java.time.LocalDateTime? = null,
    val content: String? = null,
    val image: String? = null,
    val locationName: String? = null
)