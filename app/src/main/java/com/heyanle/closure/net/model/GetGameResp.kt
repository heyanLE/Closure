package com.heyanle.closure.net.model

/**
 * Created by HeYanLe on 2022/12/29 18:41.
 * https://github.com/heyanLE
 */
data class GetGameResp(
    val status: GameGetStatus,
    val consumable: Any?,
    val inventory: Any?,
    val troop: Any?,
    val lastFreshTs: Long,
)