package com.heyanle.closure.closure.net

import com.heyanle.closure.utils.stringRes
import com.heyanle.i18n.R

/**
 * Created by HeYanLe on 2023/8/13 16:36.
 * https://github.com/heyanLE
 */

data class NetResponse<R>(
    val isSuss: Boolean,
    var code: Int,
    var data: R? = null,
    var message: String?,
    var throwable: Throwable? = null,
) {

    companion object {

        fun <T> netOk(code: Int, message: String?, date: T ): NetResponse<T> {
            return NetResponse(
                true,
                code,
                date,
                message,
            )
        }

        fun <T> netError(code: Int, message: String? = null, throwable: Throwable? = null): NetResponse<T> {
            return NetResponse(
                false,
                code = code,
                message = message + stringRes(R.string.net_error),
                throwable = throwable
            )
        }
    }

    inline fun okNullable(block: (NetResponse<R>) -> Unit): NetResponse<R> {
        if (isSuss) {
            block(this)
        }
        return this
    }

    inline fun okWithData(block: (R) -> Unit): NetResponse<R> {
        val data = data
        if (isSuss && data != null) {
            block(data)
        }
        return this
    }

    inline fun error(block: (NetResponse<R>) -> Unit): NetResponse<R> {
        if (!isSuss) {
            block(this)
        }
        return this
    }
}

