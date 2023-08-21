package com.heyanle.closure.net.model

import android.util.Log
import androidx.compose.ui.graphics.Color
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

    companion object {
        val colors = listOf<Pair<Regex, Color>>(
            Regex(".*完成.*|.*战斗结束.*") to Color(0xff39c832),
            Regex(".*战斗开启成功.*|.*仓库识别.*") to Color(0xff01d0db),
            Regex(".*错误.*|.*失败.*") to Color.Red,
            Regex(".*5x.*|.*6x.*") to Color(0xffd8a71e),
            Regex(".*计划.*|.*下次.*") to Color(0xffed3f7c),
        )
    }

    fun getColor(): Color {
        colors.forEach {
            if(info.matches(it.first)){
                return it.second
            }
        }
        return Color.Unspecified
    }

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