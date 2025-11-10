@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package com.umc.data.api.dto.server

import com.squareup.moshi.Json

data class SummaryRequestDTO (
    @Json(name = "date")
    val date: String // "2025-11-08" 형식
)