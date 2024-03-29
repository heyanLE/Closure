package com.heyanle.closure.closure.assets

import android.content.Context
import com.heyanle.closure.closure.net.Net
import com.heyanle.closure.utils.jsonTo
import com.squareup.moshi.Json
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * Created by heyanlin on 2024/1/26 15:10.
 */
class AssetsController(
    private val net: Net,
    private val rootFolder: String,
    private val context: Context,
) {

    companion object {
        // 理智图标https://assets.closure.setonink.com/dst/
        const val AP_ICON_URL = "https://assets.closure.setonink.com/dst/items/AP_GAMEPLAY.webp"

        // 源石图标
        const val DIAMOND_ICON_URL = "https://assets.closure.setonink.com/dst/items/DIAMOND.webp"

        // 合成玉图标
        const val DIAMOND_SHD_ICON_URL = "https://assets.closure.setonink.com/dst/items/DIAMOND_SHD.webp"

        // 龙门币图标
        const val GOLD_ICON_URL = "https://assets.closure.setonink.com/dst/items/GOLD.webp"
    }

    data class Item(
        @Json(name = "name")
        val name: String,

        @Json(name = "icon")
        val icon: String,
    )

    data class Stage(
        @Json(name = "name")
        val name: String = "",

        @Json(name = "code")
        val code: String = "",

        @Json(name = "ap")
        val ap: Int = 0,

        @Json(name = "items")
        val items: List<String> = listOf()
    )

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val itemAssets = AssetsItem(context, net, scope, "${net.assetsUrl}/data/items.json", rootFolder, "items.json", "items.json")
    private val stageAssets = AssetsItem(context, net, scope, "${net.assetsUrl}/data/stages.json", rootFolder, "stages.json", "stages.json")


    val itemsMap = itemAssets.res.map { it.jsonTo<Map<String, Item>>()?: emptyMap() }.stateIn(scope, SharingStarted.Lazily, emptyMap())
    val stageMap = stageAssets.res.map {
        it.jsonTo<Map<String, Stage>>()?: emptyMap()
    }.stateIn(scope, SharingStarted.Lazily, emptyMap())

    init {
        itemAssets.init()
        stageAssets.init()
    }


}