package com.heyanle.closure.closure.logs

import com.heyanle.closure.closure.game.GameRepository
import com.heyanle.closure.closure.game.model.GameResp
import com.heyanle.closure.closure.game.model.LogInfo
import com.heyanle.closure.closure.game.model.LogItem
import com.heyanle.closure.closure.net.NetResponse
import com.heyanle.closure.utils.CoroutineProvider
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.ContentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive

/**
 * Created by heyanle on 2024/1/27.
 * https://github.com/heyanLE
 */
class ClosureLogsRepository(
    private val gameRepository: GameRepository
) {

    private val scope = CoroutineScope(SupervisorJob() + CoroutineProvider.SINGLE)

    fun getLog(account: String, token: String, until: Long): Deferred<List<LogItem>> {
        return scope.async {
            val res = arrayListOf<LogItem>()
            var offset = 0
            var hasNext = true
            while(isActive && hasNext){
                gameRepository.awaitGetLog(account, token, offset)
                    .okWithData {
                        it.okWithData {
                            res.addAll(it.logList)
                            hasNext = it.hasMore
                            offset += it.logList.size
                        }
                            .error {
                                hasNext = false
                            }
                    }.error {
                        hasNext = false
                    }
                val last = res.lastOrNull()
                if((last?.ts ?: 0) < until){
                    break
                }
            }
            res
        }
    }

    suspend fun awaitGetLog(account: String, token: String, until: Long): List<LogItem> {
        return getLog(account, token, until).await()
    }




}