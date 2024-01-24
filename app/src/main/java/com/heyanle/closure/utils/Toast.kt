package com.heyanle.closure.utils

import android.app.Application
import android.widget.Toast
import com.heyanle.injekt.core.Injekt

/**
 * Created by HeYanLe on 2022/12/23 17:50.
 * https://github.com/heyanLE
 */
fun <T> T.toast(len: Int = Toast.LENGTH_SHORT): T = apply {
    val app: Application by Injekt.injectLazy()
    Toast.makeText(app, toString(), len).show()
}