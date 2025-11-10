@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package com.umc.data.api.dto.server

import com.squareup.moshi.Json

data class GetSummaryResultDTO (
    @Json(name = "createdAt")
    val createdAt: String? = null, // "2025-11-08T15:05:25.299Z"

    @Json(name = "summaryDiary")
    val summaryDiary: String? = null
)