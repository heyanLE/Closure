package com.heyanle.closure.closure.entity

import com.heyanle.closure.net.model.ConsumableItem
import com.heyanle.closure.net.model.GameGetStatus
import com.heyanle.closure.net.model.GetGameResp

/**
 * Created by HeYanLe on 2023/8/19 19:16.
 * https://github.com/heyanLE
 */
data class GameInfo(
    val status: GameGetStatus? = null,
    val consumable: Map<String, List<ConsumableItem>>? = emptyMap(),
    val inventory: Map<String, Long>? = emptyMap(),
    val lastFreshTs: Long = 0L,
    val timestamp: Long = 0L,
) {

    companion object {
        fun fromGetGameResp(getGameResp: GetGameResp): GameInfo {
            return GameInfo(
                status = getGameResp.status,
                consumable = getGameResp.consumable,
                inventory = getGameResp.inventory,
                lastFreshTs = getGameResp.lastFreshTs,
                timestamp = System.currentTimeMillis()
            )
        }

        val NONE = GameInfo()
    }

}