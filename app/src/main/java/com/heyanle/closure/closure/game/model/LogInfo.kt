package com.heyanle.closure.closure.game.model

import androidx.compose.ui.graphics.Color
import com.squareup.moshi.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by heyanlin on 2024/1/17 17:28.
 */
class LogInfo (
    @Json(name = "hasMore")
    val hasMore: Boolean,
    @Json(name = "logs")
    val logList: List<LogItem>,
)

data class LogItem (
    @Json(name = "id")
    val id: Long,

    @Json(name = "ts")
    val ts: Long,

    @Json(name = "name")
    val name: String,

    @Json(name = "logLevel")
    val logLevel: Long,

    @Json(name = "content")
    val content: String
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
            if(content.matches(it.first)){
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