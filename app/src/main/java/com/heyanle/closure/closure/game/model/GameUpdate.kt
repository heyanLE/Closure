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
    @Json(name = "is_auto_battle")
    val isAutoBattle: Boolean,

    @Json(name = "map_id")
    val mapId: String,

    @Json(name = "battle_maps")
    val battleMaps: List<String>,

    @Json(name = "keeping_ap")
    val keepingAP:Int,

    @Json(name = "recruit_reserve")
    val recruitReserve: Int,

    @Json(name = "recruit_ignore_robot")
    val recruitIgnoreRobot: Boolean,

    @Json(name = "is_stopped")
    val isStopped: Boolean,

    @Json(name = "enable_building_arrange")
    val enableBuildingArrange: Boolean,

    @Json(name = "accelerate_slot_cn")
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