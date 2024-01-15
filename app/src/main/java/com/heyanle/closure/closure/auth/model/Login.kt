package com.heyanle.closure.closure.auth.model

/**
 * Created by heyanlin on 2023/12/31.
 */
data class LoginBody(
    val email: String,
    val password: String,
)

data class LoginResp(
    val token: String,
)