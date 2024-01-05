package com.heyanle.closure.net.model

/**
 * Created by HeYanLe on 2023/3/11 22:37.
 * https://github.com/heyanLE
 */
data class Announcement(
    var allowGameLogin: Boolean = false,
    var allowLogin: Boolean = false,
    var announcement: String = "",
    var isMaintain: Boolean = false,
)