package com.umc.core.model

enum class EmailRequestResult {
    SENT,
    TOO_MANY_REQUESTS,
    MALFORMED_EMAIL,
    INVALID_EMAIL,
    NO_MATCHED,
    DUPLICATED,
    ERROR,
}