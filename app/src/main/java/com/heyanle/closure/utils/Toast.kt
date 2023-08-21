package com.heyanle.closure.utils

import android.widget.Toast
import com.heyanle.closure.APP
/**
 * Created by HeYanLe on 2022/12/23 17:50.
 * https://github.com/heyanLE
 */
fun <T> T.toast(len: Int = Toast.LENGTH_SHORT): T = apply {
    Toast.makeText(APP, toString(), len).show()
}