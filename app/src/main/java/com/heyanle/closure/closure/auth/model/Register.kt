package com.heyanle.closure.closure.auth.model

/**
 * Created by heyanlin on 2023/12/31.
 */
data class RegisterBody(
    val email: String,
    val password: String,
    val sign: String,
    val noise: String,
)

data class RegisterResp(
    val token: String,
)