package com.heyanle.closure.closure.net

import com.heyanle.closure.utils.jsonTo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.reflect.typeOf

/**
 * Created by heyanlin on 2023/12/31.
 */

class Net(
    val scope: CoroutineScope,
    val httpClient: HttpClient
) {

    companion object {
        const val CODE_JSON_ERROR = Int.MIN_VALUE
    }

    val authUrl = "https://passport.arknights.host/api/v1"
    val arkQuotaUrl = " https://registry.closure.setonink.com/api"
    val ticketUrl = "https://ticket.arknights.host/tickets"


    inline fun <reified R> send(
        crossinline block: suspend HttpClient.() -> HttpResponse,
    ): Deferred<NetResponse<R>> {
        return scope.async {
            return@async try {
                val resp = block.invoke(httpClient)
                val body = resp.body<String>()
                // 返回 String 类型的不用装载
                if (R::class.java == String::class.java) {
                    NetResponse.netOk(resp.status.value, resp.status.description, body as R)
                } else {
                    val r = body.jsonTo<R>()
                    if (r == null) {
                        NetResponse.netError<R>(
                            CODE_JSON_ERROR, "json parse error"
                        )
                    } else {
                        NetResponse.netOk(resp.status.value, resp.status.description, r)
                    }
                }
            } catch (ex: RedirectResponseException) {
                // 3xx - responses
                NetResponse.netError<R>(
                    ex.response.status.value,
                    ex.response.status.description,
                    ex
                )
            } catch (ex: ClientRequestException) {
                // 4xx - responses
                NetResponse.netError<R>(
                    ex.response.status.value,
                    ex.response.status.description,
                    ex
                )
            } catch (ex: ServerResponseException) {
                // 5xx - response
                NetResponse.netError<R>(
                    ex.response.status.value,
                    ex.response.status.description,
                    ex
                )
            }
        }
    }

}