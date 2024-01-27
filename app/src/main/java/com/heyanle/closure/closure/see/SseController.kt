package com.heyanle.closure.closure.see

import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.closure.ClosurePresenter
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.ui.common.moeSnackBar
import com.heyanle.closure.utils.CoroutineProvider
import com.heyanle.closure.utils.jsonTo
import com.heyanle.closure.utils.logi
import com.heyanle.closure.utils.stringRes
import com.heyanle.i18n.R
import com.heyanle.injekt.api.get
import com.heyanle.injekt.core.Injekt
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.http2.Hpack
import java.io.IOException
import java.net.Socket
import kotlin.time.Duration.Companion.milliseconds

/**
 * Created by heyanle on 2024/1/27.
 * https://github.com/heyanLE
 */
class SseController (
    private val okHttpClient: OkHttpClient,
    private val closureController: ClosureController,
){

    companion object {
        const val TAG = "SSEController"
    }

    private val httpClient = HttpClient(Android)
    private var seeJob: Job? = null

    private val scope = CoroutineScope(SupervisorJob() + CoroutineProvider.SINGLE)

    var delay: Long = 1000
    var lastEventId: String = ""


    init {
        scope.launch(Dispatchers.Main) {
            closureController.state.map { it.username to it.token }.distinctUntilChanged().collectLatest {
                if(it.first.isNotEmpty() && it.second.isNotEmpty()){
                    start(it.first)
                }
            }
        }
    }

    private fun start(username: String){
        seeJob?.cancel()
        seeJob = scope.launch {
            try {
                val token = closureController.tokenIfNull(username) ?: return@launch
                onStart(username)
                val call = okHttpClient.newCall(Request.Builder().url("https://api.ltsc.vip/sse/games?token=${token}")
                    .addHeader("Accept", "text/event-stream").apply {
                        if(lastEventId.isNotEmpty()){
                            addHeader("Last-Event-ID", lastEventId)
                        }
                    }.build())

                val resp = call.execute()
                if(!resp.isSuccessful){
                    "${resp.code} ${resp.message}".moeSnackBar()
                    stringRes(R.string.connect_error_retry_delay).moeSnackBar()
                }
                val source = resp.body?.source() ?: throw IOException("source is null")

                var curEvent = ""
                val sb = StringBuilder()
                onOpen(username)
                while(!call.isCanceled()){
                    val line = source.readUtf8LineStrict().trim()
                    line.logi(TAG)

                    if(line.isEmpty()){
                        lastEventId = curEvent
                        handleMessage(curEvent, sb.toString(), username)
                        curEvent = ""
                        sb.clear()
                        continue
                    }
                    val index = line.indexOf(":")
                    if(index == -1){
                        curEvent = ""
                        sb.clear()
                        continue
                    }
                    else if(index == 0){
                        continue
                    }
                    else {
                        val field = line.substring(0, index)
                        val value = line.substring(index +1, line.length)
                        when(field){
                            "data" -> {
                                sb.append(value.trim())
                            }
                            "event" -> {
                                curEvent = value.trim()
                            }
                            "retry" -> {
                                delay = value.trim().toLongOrNull()?:delay
                            }
                        }
                    }





                }

            }catch (ex: IOException) {
                ex.printStackTrace()
                "${ex.message}".moeSnackBar()
                stringRes(R.string.connect_stop_retry_delay).moeSnackBar()
            } catch (ex: Exception){
                ex.printStackTrace()
            }
            if(isActive){
                delay(delay)
                start(username)
            }

        }
    }

    private fun onStart(username: String){
        val presenter = Injekt.get<ClosurePresenter>(username)
        presenter.onSEEStart()
    }

    private fun onOpen(username: String){
        val presenter = Injekt.get<ClosurePresenter>(username)
        presenter.onSEEOpen()
    }

    private fun handleMessage(event: String, data: String, username: String){
        "handleMessage ${event} ${data} ${username}".logi(TAG)
        when(event){
            "game" -> {
                val webGame = data.jsonTo<List<WebGame>>()
                if(webGame != null){
                    val presenter = Injekt.get<ClosurePresenter>(username)
                    presenter.onSEEPush(webGame)
                }

            }
            "close" -> {}
        }
    }

}