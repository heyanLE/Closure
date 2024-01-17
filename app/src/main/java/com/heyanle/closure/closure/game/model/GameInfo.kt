package com.heyanle.closure.closure.game.model

import com.squareup.moshi.Json

/**
 * Created by heyanlin on 2024/1/17 16:36.
 */
data class WebGame(
    @Json(name = "status")
    val status: GameStatus,

    @Json(name = "captcha_info")
    val captchaInfo: CaptchaInfo,

    @Json(name = "game_config")
    val gameSetting: GameSetting,
)

data class GameSetting(

    @Json(name = "account")
    val account: String,

    @Json(name = "accelerate_slot")
    val accelerateSlot: String,

    @Json(name = "accelerate_slot_cn")
    val accelerateSlotCN: String,

    @Json(name = "battle_maps")
    val battleMaps: List<String>,

    @Json(name = "enable_building_arrange")
    val enableBuildingArrange: Boolean,

    @Json(name = "is_auto_battle")
    val isAutoBattle: Boolean,

    @Json(name = "is_stopped")
    val isStopped: Boolean,

    @Json(name = "keeping_ap")
    val keepingAP: Int,

    @Json(name = "recruit_ignore_robot")
    val recruitIgnoreRobot: Boolean,

    @Json(name = "recruit_reserve")
    val recruitReserve: Int,

    @Json(name = "map_id")
    val mapId: String,
)

data class CaptchaInfo(
    @Json(name = "challenge")
    val challenge: String,

    @Json(name = "gt")
    val gt: String,

    @Json(name = "created")
    val created: Long,

    @Json(name = "captcha_type")
    val captchaType: String,
)

data class GameStatus(
    @Json(name = "account")
    val account: String,

    @Json(name = "platform")
    val platform: Int,

    @Json(name = "uuid")
    val uuid: String,

    @Json(name = "code")
    val code: Int,

    @Json(name = "test")
    val text: String,

    @Json(name = "nick_name")
    val nickName: String,

    @Json(name = "level")
    val level: Int,

    @Json(name = "avatar")
    val avatar: AvatarInfo,

    @Json(name = "created_at")
    val createAt: Long,

    @Json(name = "is_veritify")
    val isVertify: Boolean,
)

data class AvatarInfo(

    @Json(name = "type")
    val type: String,

    @Json(name = "id")
    val id: String,
)