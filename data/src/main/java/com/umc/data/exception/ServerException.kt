package com.umc.data.exception

class ServerException(
    val code: String,
    message: String,
): RuntimeException(message)