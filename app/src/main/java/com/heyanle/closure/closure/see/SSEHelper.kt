package com.heyanle.closure.closure.see

import com.heyanle.closure.utils.logi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import okio.BufferedSource
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * Created by heyanlin on 2024/3/5.
 */
class SSEHelper(
    private val okHttpClient: OkHttpClient,
    private val url: String,
    private var listener: SSEListener?,
) : EventSourceListener() {

    interface SSEListener {
        fun onOpen()
        fun onMessage(event: String, data: String)
        fun onError(ex: Throwable?)
        fun onClose()
    }


    private var eventSource: EventSource? = null
    private var lastEventId: String = ""

    fun start() {
        innerStart(url)
    }

    fun release() {
        listener = null
        eventSource?.cancel()
    }

    @Throws(IOException::class)
    private fun innerStart(url: String) {
        url.logi("SSEHelper")
        val req =  Request.Builder().url(url)
            .addHeader("Accept-Encoding", "")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Accept", "text/event-stream").apply {
                if (lastEventId.isNotEmpty()) {
                    addHeader("Last-Event-ID", lastEventId)
                }
            }.build()
        eventSource = EventSources.createFactory(okHttpClient).newEventSource(req, this)
    }

    override fun onClosed(eventSource: EventSource) {
        listener?.onClose()
    }

    override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
        lastEventId = type ?: return
        listener?.onMessage(type, data)
    }

    override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
        listener?.onError(t)
    }

    override fun onOpen(eventSource: EventSource, response: Response) {
        listener?.onOpen()
    }

}


