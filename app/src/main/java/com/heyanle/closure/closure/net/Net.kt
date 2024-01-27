package com.heyanle.closure.closure.net

import androidx.compose.ui.res.stringResource
import com.heyanle.closure.utils.jsonTo
import com.heyanle.closure.utils.logi
import com.heyanle.closure.utils.stringRes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
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
        const val TAG = "Net"
        const val CODE_JSON_ERROR = Int.MIN_VALUE
        const val CODE_TIMEOUT = Int.MIN_VALUE + 1
    }

    val authUrl = "https://passport.arknights.host/api/v1"
    val arkQuotaUrl = " https://registry.closure.setonink.com/api"
    val ticketUrl = "https://ticket.arknights.host/tickets"
    val gameUrl = "https://api.ltsc.vip"
    val assetsUrl = "https://www.arknights.host"


    inline fun <reified R> send(
        crossinline block: suspend HttpClient.() -> HttpResponse,
    ): Deferred<NetResponse<R>> {
        return scope.async {
            return@async try {
                "send >> ${R::class}".logi(TAG)
                val resp = block.invoke(httpClient)
                val body = resp.body<String>()
                "send << ${body}".logi(TAG)
                // 返回 String 类型的不用装载
                if (R::class.java == String::class.java) {
                    NetResponse.netOk(resp.status.value, resp.status.description, body as R, body)
                } else {
                    val r = body.jsonTo<R>()
                    if (r == null) {
                        NetResponse.netError<R>(
                            CODE_JSON_ERROR, stringRes(com.heyanle.i18n.R.string.moshi_error), body,
                        )
                    } else {
                        NetResponse.netOk(resp.status.value, resp.status.description, r, body)
                    }
                }
            } catch (ex: RedirectResponseException) {
                // 3xx - responses
                NetResponse.netError<R>(
                    ex.response.status.value,
                    ex.response.status.description,
                    ex.response.body(),
                    ex
                )
            } catch (ex: ClientRequestException) {
                // 4xx - responses
                NetResponse.netError<R>(
                    ex.response.status.value,
                    ex.response.status.description,
                    ex.response.body(),
                    ex
                )
            } catch (ex: ServerResponseException) {
                // 5xx - response
                NetResponse.netError<R>(
                    ex.response.status.value,
                    ex.response.status.description,
                    ex.response.body(),
                    ex
                )
            } catch (ex: HttpRequestTimeoutException) {
                NetResponse.netError<R>(
                    CODE_TIMEOUT, stringRes(com.heyanle.i18n.R.string.net_timeout), "", ex, true
                )
            } catch (ex: ConnectTimeoutException) {
                NetResponse.netError<R>(
                    CODE_TIMEOUT, stringRes(com.heyanle.i18n.R.string.net_timeout), "", ex, true
                )
            } catch (ex: SocketTimeoutException) {
                NetResponse.netError<R>(
                    CODE_TIMEOUT, stringRes(com.heyanle.i18n.R.string.net_timeout), "", ex, true
                )
            }
        }
    }

}