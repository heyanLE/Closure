package com.heyanle.closure.net.model

/**
 * Created by HeYanLe on 2022/12/23 14:49.
 * https://github.com/heyanLE
 */
class Response<R>(
    var code: Int,
    var data: R? = null,
    var message: String,
) {
}