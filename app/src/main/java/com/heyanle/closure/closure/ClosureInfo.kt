package com.heyanle.closure.closure

import androidx.loader.content.Loader
import com.heyanle.closure.closure.LoadableData
import com.heyanle.closure.closure.game.model.GetGameInfo
import com.heyanle.closure.closure.game.model.LogInfo
import com.heyanle.closure.closure.game.model.LogItem
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.closure.quota.module.Account
import com.squareup.moshi.Json

/**
 * 总线结构
 * Created by heyanlin on 2024/1/18 17:14.
 */
data class ClosureInfo (

    @Json(name = "token")
    val token: LoadableData<String> = LoadableData(),

    @Json(name = "username")
    val username: String,

    @Json(name = "password")
    val password: String,

    @Json(name = "account")
    val account: LoadableData<Account> = LoadableData(),

    @Json(name = "webGameList")
    val webGameList: LoadableData<List<WebGame>> = LoadableData(),

    @Json(name = "gameMap")
    val gameMap: Map<String, LoadableData<GetGameInfo>> = mapOf(),

    @Json(name = "logList")
    val logList: List<LogItem> = listOf(),
)