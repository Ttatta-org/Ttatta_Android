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
 * @param categoryId
 * @param categoryName
 * @param categoryColor
 * @param updatedAt
 */
data class ModifyCategoryResultDTO(

    val categoryId: Long? = null,
    val categoryName: String? = null,
    val categoryColor: CategoryColor? = null,
    val updatedAt: java.time.LocalDateTime? = null
) {
    /**
     *
     * Values: RED,ORANGE,YELLOW,GREEN,SKYBLUE,BLUE,INDIGO,VIOLET,BROWN,WHITE,PINK,BLACK
     */
    enum class CategoryColor(val value: String) {
        RED("RED"),
        ORANGE("ORANGE"),
        YELLOW("YELLOW"),
        GREEN("GREEN"),
        SKYBLUE("SKYBLUE"),
        BLUE("BLUE"),
        INDIGO("INDIGO"),
        VIOLET("VIOLET"),
        BROWN("BROWN"),
        WHITE("WHITE"),
        PINK("PINK"),
        BLACK("BLACK");
    }
}