package com.heyanle.closure.closure.auth.model

import com.squareup.moshi.Json

/**
 * Created by heyanlin on 2023/12/31.
 */
data class LoginBody(
    @Json(name = "email")
    val email: String,

    @Json(name = "password")
    val password: String,
)

data class LoginResp(

    @Json(name = "token")
    val token: String,
)