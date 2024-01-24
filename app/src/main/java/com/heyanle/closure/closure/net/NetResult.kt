package com.heyanle.closure.closure.net

import com.heyanle.closure.closure.quota.module.CreateGameBody
import com.heyanle.closure.ui.common.moeSnackBar
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
    // body 原文，存一份让业务可以解析
    var respBody: String ="",
    var throwable: Throwable? = null,
    var isTimeout: Boolean = false,
) {

    companion object {

        fun <T> netOk(code: Int, message: String?, date: T ,respBody: String): NetResponse<T> {
            return NetResponse(
                true,
                code,
                date,
                message,
                respBody
            )
        }

        fun <T> netError(code: Int, message: String? = null, body: String, throwable: Throwable? = null, isTimeout: Boolean = false): NetResponse<T> {
            return NetResponse(
                false,
                code = code,
                message = message + stringRes(R.string.net_error),
                throwable = throwable,
                respBody = body
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

    fun snackWhenError(){
        if(!isSuss){
            "${stringRes(com.heyanle.i18n.R.string.net_error)} ${message} ${code} ${throwable?.message}"
                .moeSnackBar()
        }
    }
}



