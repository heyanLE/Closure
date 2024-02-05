package com.heyanle.closure.closure.quota.module

import com.squareup.moshi.Json

/**
 * Created by heyanlin on 2024/1/15 15:05.
 */
data class CreateGameBody(

    @Json(name = "account")
    val account: String,

    @Json(name = "platform")
    val platform: String,

    @Json(name = "password")
    val password: String,
)

data class DeleteGameBody(
    @Json(name = "account")
    val account: String? = null,
)

data class CreateGameResp(
    @Json(name = "available")
    val available: Boolean,

    @Json(name = "results")
    val results: CreateGameResult,
)
data class CreateGameResult(
    @Json(name = "slot_user_sms_verified")
    val slotUserSmsVerified: SlotUserSmsVerifiedItem
)

data class SlotUserSmsVerifiedItem(

    @Json(name = "ruleId")
    val ruleId: String,

    @Json(name = "available")
    val available: Boolean,

    @Json(name = "statusId")
    val statusId: String,

    @Json(name = "message")
    val message: String,
)