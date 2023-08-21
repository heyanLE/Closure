package com.heyanle.closure.net.model

import com.google.gson.annotations.SerializedName

/**
 * Created by HeYanLe on 2022/12/23 15:41.
 * https://github.com/heyanLE
 */
data class ScreenshotRsp(
    @SerializedName("UTCTime")
    val utcTime: Long,
    val type: Long,
    val host: String,
    var fileName: List<String>? = arrayListOf(),
    val url: String,
)