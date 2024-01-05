package com.heyanle.closure.net

import com.heyanle.closure.utils.stringRes

/**
 * Created by HeYanLe on 2023/8/13 16:36.
 * https://github.com/heyanLE
 */

data class NetResponse<R>(
    var code: Int,
    var data: R? = null,
    var message: String,
    var isNetError: Boolean = false, // 是否是网络问题
    var throwable: Throwable? = null,
) {

    companion object {
        fun <T> netError(code: Int, message: String? = null, throwable: Throwable? = null): NetResponse<T> {
            return NetResponse(
                code = code,
                message = message ?: stringRes(com.heyanle.i18n.R.string.net_error),
                isNetError = true,
                throwable = throwable
            )
        }
    }

    inline fun okNullable(block: (NetResponse<R>) -> Unit): NetResponse<R> {
        if (code == 1) {
            block(this)
        }
        return this
    }

    inline fun okWithData(block: (R) -> Unit): NetResponse<R> {
        val data = data
        if (code == 1 && data != null) {
            block(data)
        }
        return this
    }

    inline fun error(block: (NetResponse<R>) -> Unit): NetResponse<R> {
        if (code != 1) {
            block(this)
        }
        return this
    }
}

