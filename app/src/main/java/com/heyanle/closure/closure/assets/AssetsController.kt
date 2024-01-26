package com.heyanle.closure.closure.assets

import com.heyanle.closure.closure.net.Net
import com.squareup.moshi.Json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * Created by heyanlin on 2024/1/26 15:10.
 */
class AssetsController(
    private val net: Net,
    private val rootFolder: String,
) {

    data class Item(
        @Json(name = "name")
        val name: String,

        @Json(name = "icon")
        val icon: String,
    )

    data class Stage(
        @Json(name = "name")
        val name: String,

        @Json(name = "code")
        val code: String,

        @Json(name = "ap")
        val ap: Int,

        @Json(name = "items")
        val items: List<String>
    )

    private val itemFile = File(rootFolder, "items.json")
    private val itemBKFile = File(rootFolder, "items.json.bk")

    private val stageFile = File(rootFolder, "")

    private val _itemsMap  = MutableStateFlow<Map<String, Item>>(emptyMap())
    val itemsMap = _itemsMap.asStateFlow()

    private val _stagesMap = MutableStateFlow<Map<String, Stage>>(emptyMap())
    val stagesMap = _stagesMap.asStateFlow()


    private fun loadFromAssets(){}

    private fun loadFromNet(){}


}