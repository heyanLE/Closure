package com.heyanle.closure.net

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

/**
 * Created by heyanlin on 2023/12/31.
 */
class Net(
    private val scope: CoroutineScope,
    private val httpClient: HttpClient
) {

    companion object {
        const val CODE_JSON_ERROR = Int.MIN_VALUE
    }

    val rootUrl = "https://passport.arknights.host/api/v1"

    fun <R> send(
        block: suspend HttpClient.() -> HttpResponse,
    ): Deferred<NetResponse<R>> {
        return scope.async {
            return@async try {
                block.invoke(httpClient)
                    .body<String>().jsonTo<NetResponse<R>>() ?:  NetResponse.netError<R>(
                    CODE_JSON_ERROR, "json parse error")
            } catch (ex: RedirectResponseException) {
                // 3xx - responses
                NetResponse.netError<R>(ex.response.status.value, ex.response.status.description, ex)
            } catch (ex: ClientRequestException) {
                // 4xx - responses
                NetResponse.netError<R>(ex.response.status.value, ex.response.status.description, ex)
            } catch (ex: ServerResponseException) {
                // 5xx - response
                NetResponse.netError<R>(ex.response.status.value, ex.response.status.description, ex)
            }
        }
    }

}