package com.heyanle.closure.net.model

import com.google.gson.JsonObject

/**
 * Created by HeYanLe on 2023/3/11 22:30.
 * https://github.com/heyanLE
 */
data class BindQQResponseWait(
    var email: String = "",
    var expireTimestamp: Long = 0,
    var verifyCode: String = "",
){
    companion object {
        fun fromJsonObject(jsonObject: JsonObject): BindQQResponseWait{
            return BindQQResponseWait(
                email = jsonObject.get("email").asString?:"",
                expireTimestamp = jsonObject.get("expireTimestamp").asBigInteger?.toLong()?:0L,
                verifyCode = jsonObject.get("verifyCode").asString?:""

            )
        }
    }
}