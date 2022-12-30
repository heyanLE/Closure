package com.heyanle.closure.utils

/**
 * Created by HeYanLe on 2022/12/30 18:56.
 * https://github.com/heyanLE
 */

fun todo(other: String? = null){
    runCatching {
        // 弹窗
        "在写了在写了 ${other}".toast()
    }
}