package com.heyanle.closure.net.model

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by HeYanLe on 2022/12/23 15:33.
 * https://github.com/heyanLE
 */
class GameLogItem(
    var ts: Double,
    var info: String,
){

    fun getData(): String {
        val date = Date((ts).toLong()*1000)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(date)
    }

    fun getTime(): String {
        val date = Date((ts).toLong()*1000)
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

}