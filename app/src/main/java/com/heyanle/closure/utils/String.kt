package com.heyanle.closure.utils

import com.heyanle.closure.app

/**
 * Created by HeYanLe on 2022/12/23 17:53.
 * https://github.com/heyanLE
 */

fun stringRes(resId: Int, vararg formatArgs: Any): String{
    return app.getString(resId, formatArgs)
}