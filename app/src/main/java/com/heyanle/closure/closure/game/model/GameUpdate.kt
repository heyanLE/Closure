package com.heyanle.closure.closure.game.model

import com.squareup.moshi.Json

/**
 * Created by heyanlin on 2024/1/17 17:34.
 */

data class UpdateGameInfo(
    @Json(name = "config")
    val config: GameUpdateConfig? = null,

    @Json(name = "captcha_info")
    val captchaInfo: UpdateCaptchaInfo? = null,

    @Json(name = "require_ocr")
    val requireOcr: Boolean = false,
)

data class UpdateCaptchaInfo(

    @Json(name = "challenge")
    val challenge: String,

    @Json(name = "geetest_challenge")
    val geetestChallenge: String,

    @Json(name = "geetest_validate")
    val geetestValidate: String,

    @Json(name = "geetest_seccode")
    val geetestSeccode: String,
)

data class GameUpdateConfig (
    @Json(name = "isAutoBattle")
    val isAutoBattle: Boolean,

    @Json(name = "mapId")
    val mapId: String,

    @Json(name = "battleMaps")
    val battleMaps: List<String>,

    @Json(name = "keepingAP")
    val keepingAP:Int,

    @Json(name = "recruitReserve")
    val recruitReserve: Int,

    @Json(name = "recruitIgnoreRobot")
    val recruitIgnoreRobot: Boolean,

    @Json(name = "isStopped")
    val isStopped: Boolean,

    @Json(name = "enableBuildingArrange")
    val enableBuildingArrange: Boolean,

    @Json(name = "accelerateSlot_CN")
    val accelerateSlotCN: String,
){
    companion object {
        fun fromGameSetting(gameSetting: GameSetting): GameUpdateConfig {
            return GameUpdateConfig(
                isAutoBattle = gameSetting.isAutoBattle,
                mapId = gameSetting.mapId,
                battleMaps = gameSetting.battleMaps,
                keepingAP = gameSetting.keepingAP,
                recruitReserve = gameSetting.recruitReserve,
                recruitIgnoreRobot = gameSetting.recruitIgnoreRobot,
                isStopped = gameSetting.isStopped,
                enableBuildingArrange = gameSetting.enableBuildingArrange,
                accelerateSlotCN = gameSetting.accelerateSlotCN
            )
        }
    }
}