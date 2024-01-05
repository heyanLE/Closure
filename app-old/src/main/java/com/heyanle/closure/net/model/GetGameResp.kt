package com.heyanle.closure.net.model

/**
 * Created by HeYanLe on 2022/12/29 18:41.
 * https://github.com/heyanLE
 */
data class GetGameResp(
    val status: GameGetStatus,
    var consumable: Map<String, List<ConsumableItem>>? = emptyMap(),
    var inventory: Map<String, Long>? = emptyMap(),
    val troop: Any?,
    val lastFreshTs: Long,
)

data class ConsumableItem(
    val ts: Long,
    val count: Long,
)