package com.heyanle.closure.utils


import android.util.Log
import com.heyanle.closure.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by HeYanLe on 2022/12/30 16:12.
 * https://github.com/heyanLE
 */
object APUtils {

    // 6 分钟
    const val AP_UP_TIME = 6*60*1000


    // 时间单位 毫秒
    fun getAPMaxTime(now: Long, max: Long, startTime: Long, maxApString: String = stringRes(com.heyanle.i18n.R.string.ap_has_max)): String{
        //Log.d("APUtils", "$now $max $startTime")
        val y = (max-now) * AP_UP_TIME
        val time = startTime*1000 + y
        if(time <= System.currentTimeMillis() || now >= max){
            return maxApString
        }
        val date = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

    fun getNowAp(lastAp: Long, max: Long, lastTime: Long): Long{
        return if(lastAp > max){
            lastAp
        }else{
            val during = System.currentTimeMillis() - lastTime*1000
            max.coerceAtMost(lastAp + (during / AP_UP_TIME))
        }
    }
}