package com.heyanle.closure.closure.quota

import com.heyanle.closure.closure.net.Net
import com.heyanle.closure.closure.net.NetResponse
import com.heyanle.closure.closure.quota.module.Account
import com.heyanle.closure.closure.quota.module.CreateGameBody
import com.heyanle.closure.closure.quota.module.CreateGameResp
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Deferred

/**
 * Created by heyanlin on 2024/1/15 14:44.
 */
class QuotaRepository(
    private val net: Net,
) {

    fun account(token: String): Deferred<NetResponse<Account>> {
        return net.send {
            get {
                url("${net.arkQuotaUrl}/users/me")
                header("Accept", "application/json")
            }
        }
    }

    suspend fun awaitAccount(token: String): NetResponse<Account> {
        return account(token).await()
    }

    fun createGame(token: String, uuid: String, captchaToken: String, createGameBody: CreateGameBody): Deferred<NetResponse<CreateGameResp>>  {
        return net.send {
            post {
                url("${net.arkQuotaUrl}/slots/gameAccount?uuid=${uuid}")
                header("Authorization", token)
                header("Accept", "application/json")
                header("x-platform", "app")
                header("token", captchaToken)
                contentType(ContentType.Application.Json)
                setBody(createGameBody)
            }
        }
    }

    suspend fun awaitCreateGame(token: String, uuid: String, captchaToken: String, createGameBody: CreateGameBody): NetResponse<CreateGameResp> {
        return createGame(token, uuid, captchaToken, createGameBody).await()
    }

}