package com.heyanle.closure.closure.auth.model

import com.heyanle.closure.closure.net.NetResponse
import com.squareup.moshi.Json

/**
 * Created by heyanlin on 2024/1/15 15:43.
 */
data class AuthResp<R>(
    @Json(name = "code")
    val code: Int,

    @Json(name = "message")
    val message: String? = null,

    @Json(name = "data")
    val data: R,
){

    inline fun okNullable(block: (AuthResp<R>) -> Unit): AuthResp<R> {
        if (code == 1) {
            block(this)
        }
        return this
    }

    inline fun okWithData(block: (R) -> Unit): AuthResp<R> {
        val data = data
        if (code == 1 && data != null) {
            block(data)
        }
        return this
    }

    inline fun error(block: (AuthResp<R>) -> Unit): AuthResp<R> {
        if (code != 1) {
            block(this)
        }
        return this
    }

}