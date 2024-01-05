package com.heyanle.closure.net.model

import com.google.gson.annotations.SerializedName

/**
 * Created by HeYanLe on 2022/12/23 15:22.
 * https://github.com/heyanLE
 */
data class CaptchaReq(
    val challenge: String,
    @SerializedName("geetest_challenge")
    val geetestChallenge: String,
    @SerializedName("geetest_seccode")
    val geetestSeccode: String,
    @SerializedName("geetest_validate")
    val geetestValidate: String,
    val success: Boolean,
)