package com.heyanle.closure.closure.game.model

import com.squareup.moshi.Json

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
)