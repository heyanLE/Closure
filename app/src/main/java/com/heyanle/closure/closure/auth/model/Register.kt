package com.heyanle.closure.closure.auth.model

import com.squareup.moshi.Json

/**
 * Created by heyanlin on 2023/12/31.
 */
data class RegisterBody(
    @Json(name = "email")
    val email: String,

    @Json(name = "password")
    val password: String,

    @Json(name = "sign")
    val sign: String,

    @Json(name = "noise")
    val noise: String,
)

data class RegisterResp(
    @Json(name = "token")
    val token: String,
)