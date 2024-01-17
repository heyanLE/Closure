package com.heyanle.closure.closure.game.model

import com.squareup.moshi.Json

/**
 * Created by heyanlin on 2024/1/17 17:05.
 */
class GetGameInfo (
    @Json(name = "config")
    val config: GameConfig,

    @Json(name = "consumable")
    var consumable: Map<String, List<ConsumableItem>>? = emptyMap(),

    @Json(name = "inventory")
    var inventory: Map<String, Long>? = emptyMap(),

    @Json(name = "lastFreshTs")
    val lastFreshTs: Long,

    @Json(name = "screenshot")
    val screenshot: List<ScreenshotItem>,

    @Json(name = "status")
    val status: GetGameStatus,

    @Json(name = "troop")
    val troop: Any?,

)

data class GameConfig (
    @Json(name = "accelerate_slot")
    val accelerateSlot: String,

    @Json(name = "accelerate_slot_cn")
    val accelerateSlotCN: String,

    val account: String,

    @Json(name = "allow_login_assist")
    val allowLoginAssist: Boolean,

    @Json(name = "battle_maps")
    val battleMaps: List<String>,

    @Json(name = "enable_building_arrange")
    val enableBuildingArrange: Boolean,

    @Json(name = "is_auto_battle")
    val isAutoBattle: Boolean,

    @Json(name = "is_stopped")
    val isStopped: Boolean,

    @Json(name = "keeping_ap")
    val keepingAp: Long,

    @Json(name = "map_id")
    val mapID: String,

    @Json(name = "recruit_ignore_robot")
    val recruitIgnoreRobot: Boolean,

    @Json(name = "recruit_reserve")
    val recruitReserve: Long
)

data class ConsumableItem(
    @Json(name = "ts")
    val ts: Long,

    @Json(name = "count")
    val count: Long,
)

data class GetGameStatus(
    @Json(name = "androidDiamond")
    val androidDiamond: Int,

    @Json(name = "ap")
    val ap: Int,

    @Json(name = "avatar")
    val avatar: AvatarInfo,

    @Json(name = "avatarId")
    val avatarId: String,

    @Json(name = "diamondShard")
    val diamondShard: Int,

    @Json(name = "gachaTicket")
    val gachaTicket: Int,

    @Json(name = "gold")
    val gold: Int,

    @Json(name = "lastApAddTime")
    val lastApAddTime: Int,

    @Json(name = "level")
    val level: Int,

    @Json(name = "maxAp")
    val maxAp: Int,

    @Json(name = "nickName")
    val nickName: String,

    @Json(name = "recruitLicense")
    val recruitLicense: Int,

    @Json(name = "secretary")
    val secretary: String,

    @Json(name = "secretarySkinId")
    val secretarySkinId: String,

    @Json(name = "socialPoint")
    val socialPoint: Int,

    @Json(name = "tenGachaTicket")
    val tenGachaTicket: Int,
)

data class ScreenshotItem (
    @Json(name = "UTCTime")
    val utcTime: Long,

    @Json(name = "fileName")
    val fileName: List<String>,

    @Json(name = "host")
    val host: String,

    @Json(name = "type")
    val type: Long,

    @Json(name = "url")
    val url: String
)