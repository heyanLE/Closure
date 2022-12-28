package com.heyanle.closure.net.model

/**
 * Created by HeYanLe on 2022/12/23 15:10.
 * https://github.com/heyanLE
 */
data class GameConfig(
    val isAutoBattle: Boolean,
    val mapId: String,
    val battleMaps: List<String>,
    val keepingAP: Long,
    val recruitReserve: Long,
    val recruitIgnoreRobot: Boolean,
    val isStopped: Boolean,
    val enableBuildingArrange: Boolean,
)
