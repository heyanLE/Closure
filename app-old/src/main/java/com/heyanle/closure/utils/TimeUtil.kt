package com.heyanle.closure.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by HeYanLe on 2022/12/31 23:56.
 * https://github.com/heyanLE
 */

fun Long.timeToString(format: String = "yyyy-MM-dd HH:mm:ss"): String{
    val date = Date(this)
    val form = SimpleDateFormat(format, Locale.getDefault())
    return form.format(date)
}