package com.heyanle.closure.net.model

/**
 * Created by HeYanLe on 2022/12/23 14:51.
 * https://github.com/heyanLE
 */
data class WebsiteUser(
    var bindQQ: Long,
    var email: String,
    var isAdmin: Boolean,
    var password: String,
    var status: Int,
    var token: String,
) {
}