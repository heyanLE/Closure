package com.heyanle.closure.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Created by HeYanLe on 2022/12/30 13:07.
 * https://github.com/heyanLE
 */
object GsonUtil {

    val gson: Gson by lazy {
        GsonBuilder().create()
    }

}