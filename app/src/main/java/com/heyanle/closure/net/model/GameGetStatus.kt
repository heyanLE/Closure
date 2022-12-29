package com.heyanle.closure.net.model

/**
 * Created by HeYanLe on 2022/12/29 18:41.
 * https://github.com/heyanLE
 */
data class GameGetStatus(
    val nickName: String,
    val level: Long,
    val socialPoint: Long,
    val gachaTicket: Long,
    val tenGachaTicket: Long,
    val recruitLicense: Long,
    val ap: Long,
    val maxAp: Long,
    val androidDiamond: Long,
    val diamondShard: Long,
    val gold: Long,
    val lastApAddTime: Long,
    val avatarId: String,
    val secretary: String,
    val secretarySkinId: String,
    val avatar: Avatar,
){
    fun getSecretaryIconUrl(): String {
        return "https://ak.dzp.me/dst/avatar/ASSISTANT/${secretary}.webp"
    }
}

data class Avatar(
    val type: String,
    val id: String,
)
