package com.heyanle.closure.closure.game

import com.heyanle.closure.closure.game.model.GameResp
import com.heyanle.closure.closure.game.model.GetGameInfo
import com.heyanle.closure.closure.game.model.LogInfo
import com.heyanle.closure.closure.game.model.UpdateGameInfo
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.closure.net.Net
import com.heyanle.closure.closure.net.NetResponse
import com.heyanle.closure.utils.logi
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Deferred

/**
 * Created by heyanlin on 2024/1/17 16:20.
 */
class GameRepository(
    private val net: Net
) {

    fun getWebGame(token: String): Deferred<NetResponse<GameResp<List<WebGame>>>> {
        return net.send {
            get {
                url("${net.gameUrl}/game}")
                header("Authorization", "Bearer $token")
                //accept(ContentType.Application.Json)
            }
        }
    }

    suspend fun awaitGetWebGame(token: String): NetResponse<GameResp<List<WebGame>>> {
        return getWebGame(token).await()
    }

    fun getGameInfo(account: String, token: String): Deferred<NetResponse<GameResp<GetGameInfo>>> {
        return net.send {
            get {
                url("${net.gameUrl}/game/${account}")
                header("Authorization", "Bearer $token")
                //accept(ContentType.Application.Json)
            }
        }
    }

    suspend fun awaitGetGameInfo(
        account: String,
        token: String
    ): NetResponse<GameResp<GetGameInfo>> {
        return getGameInfo(account, token).await()
    }

    fun getLog(
        account: String,
        token: String,
        offset: Int
    ): Deferred<NetResponse<GameResp<LogInfo>>> {
        return net.send {
            get {
                url("${net.gameUrl}/game/log/${account}/${offset}")
                header("Authorization", "Bearer $token")
                //accept(ContentType.Application.Json)
            }
        }
    }

    suspend fun awaitGetLog(
        account: String,
        token: String,
        offset: Int
    ): NetResponse<GameResp<LogInfo>> {
        return getLog(account, token, offset).await()
    }

    fun updateGame(
        account: String,
        token: String,
        updateGameInfo: UpdateGameInfo
    ): Deferred<NetResponse<GameResp<String>>> {
        return net.send {
            post {
                url("${net.gameUrl}/game/config/${account}")
                header("Authorization", "Bearer $token")
                //accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                setBody(updateGameInfo)
                updateGameInfo.logi("GameRepository")
            }
        }
    }

    suspend fun awaitUpdateGame(
        account: String,
        token: String,
        updateGameInfo: UpdateGameInfo
    ): NetResponse<GameResp<String>> {
        return updateGame(account, token, updateGameInfo).await()
    }

    fun startGame(
        account: String,
        token: String,
        captchaToken: String
    ): Deferred<NetResponse<GameResp<String>>> {
        return net.send {
            post {
                url("${net.gameUrl}/game/login/${account}")
                header("Authorization", "Bearer $token")
                header("Token", captchaToken)
            }
        }
    }

    suspend fun awaitStartGame(
        account: String,
        token: String,
        captchaToken: String
    ): NetResponse<GameResp<String>> {
        return startGame(account, token, captchaToken).await()
    }

}