package com.heyanle.closure.closure.game.model

import com.heyanle.closure.closure.auth.model.AuthResp
import com.squareup.moshi.Json

/**
 * Created by heyanlin on 2024/1/17 16:27.
 */
data class GameResp<R>(
    @Json(name = "code")
    val code: Int,

    @Json(name = "message")
    val message: String? = null,

    @Json(name = "data")
    val data: R?,
){

    inline fun okNullable(block: (GameResp<R>) -> Unit): GameResp<R> {
        if (code == 1) {
            block(this)
        }
        return this
    }

    inline fun okWithData(block: (R) -> Unit): GameResp<R> {
        val data = data
        if (code == 1 && data != null) {
            block(data)
        }
        return this
    }

    inline fun error(block: (GameResp<R>) -> Unit): GameResp<R> {
        if (code != 1) {
            block(this)
        }
        return this
    }

}