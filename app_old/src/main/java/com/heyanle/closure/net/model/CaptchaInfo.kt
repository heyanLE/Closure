package com.heyanle.closure.net.model

import com.google.gson.annotations.SerializedName

/**
 * Created by HeYanLe on 2022/12/23 15:13.
 * https://github.com/heyanLE
 */
class CaptchaInfo (
    val challenge: String,
    val gt: String,
    val created: Long,
    @SerializedName("captcha_type")
    val captchaType: String,
){
}