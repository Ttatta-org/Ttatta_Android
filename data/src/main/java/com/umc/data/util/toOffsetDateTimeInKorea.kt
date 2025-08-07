package com.umc.data.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

fun LocalDateTime.toOffsetDateTimeInKorea(): OffsetDateTime = atOffset(ZoneId.of("Asia/Seoul").rules.getOffset(Instant.now()))
