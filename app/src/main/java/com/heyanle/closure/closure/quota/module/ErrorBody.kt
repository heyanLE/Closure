package com.heyanle.closure.closure.quota.module

import com.heyanle.closure.closure.net.NetResponse
import com.heyanle.closure.utils.jsonTo
import com.squareup.moshi.Json

/**
 * 错误返回
 * Created by heyanlin on 2024/1/17 16:06.
 */
data class ErrorBody (
    @Json(name = "err")
    val err: String,

    @Json(name = "code")
    val code: Int,
){

    companion object {
        fun fromNetResult(netResult: NetResponse<*>): ErrorBody? {
            val body = netResult.respBody
            return body.jsonTo()
        }
    }
}