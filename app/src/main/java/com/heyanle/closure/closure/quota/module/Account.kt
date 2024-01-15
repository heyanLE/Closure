package com.heyanle.closure.closure.quota.module

import com.squareup.moshi.Json

/**
 * Created by heyanlin on 2024/1/15 14:55.
 */
data class Account (

    @Json(name = "uuid")
    val uuid: String,

    @Json(name = "idServerPermission")
    val idServerPermission: Long,

    @Json(name = "idServerPhone")
    val idServerPhone: String,

    @Json(name = "idServerQQ")
    val idServerQQ: String,

    @Json(name = "idServerStatus")
    val idServerStatus: Int,

    @Json(name = "runFlags")
    val runFlags: List<String>,

    @Json(name = "rules")
    val rules: List<String>,

    @Json(name = "slots")
    val slots: List<Slot>,

    @Json(name = "createdAt")
    val createdAt: Long,

    @Json(name = "updateAt")
    val updateAt: Long,

)

data class Slot(
    @Json(name = "uuid")
    val uuid: String,

    @Json(name = "gameAccount")
    val gameAccount: String,

    @Json(name = "useFlagDefaults")
    val useFlagDefaults: Boolean,

    @Json(name = "runFlags")
    val runFlags: List<String>,

    @Json(name = "createAt")
    val createdAt: Long,
)