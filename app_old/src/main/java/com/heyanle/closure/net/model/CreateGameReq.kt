package com.heyanle.closure.net.model

/**
 * Created by HeYanLe on 2022/12/23 15:18.
 * https://github.com/heyanLE
 */
data class CreateGameReq(
    val account: String,
    val password: String,
    val platform: Int,
)

data class GameReq(
    val account: String,
    val platform: Int,
)