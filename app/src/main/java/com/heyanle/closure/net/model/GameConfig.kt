package com.heyanle.closure.net.model

import com.google.gson.annotations.SerializedName

/**
 * Created by HeYanLe on 2022/12/23 15:10.
 * https://github.com/heyanLE
 */
data class GameConfig(
    var isAutoBattle: Boolean,
    var mapId: String,
    var battleMaps: List<String>? = emptyList(),
    var keepingAP: Long,
    var recruitReserve: Long,
    var recruitIgnoreRobot: Boolean,
    var isStopped: Boolean,
    var enableBuildingArrange: Boolean,

    @SerializedName("accelerateSlot_CN")
    var accelerateSlotCN: String = "",
)
