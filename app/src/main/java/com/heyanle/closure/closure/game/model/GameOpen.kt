package com.heyanle.closure.closure.game.model

import com.squareup.moshi.Json

/**
 * Created by heyanlin on 2024/1/17 16:35.
 */
data class GameOpenReq (
    @Json(name = "Account")
    val account: String,
)
