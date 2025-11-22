package com.umc.data.exception

import java.io.IOException

class TokenExpiredException(
    val accessToken: String,
): IOException()