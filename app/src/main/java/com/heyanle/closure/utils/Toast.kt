package com.heyanle.closure.utils

import android.app.Application
import android.widget.Toast

/**
 * Created by HeYanLe on 2022/12/23 17:50.
 * https://github.com/heyanLE
 */
fun <T> T.toast(len: Int = Toast.LENGTH_SHORT): T = apply {
    val app: Application by koin.inject()
    Toast.makeText(app, toString(), len).show()
}