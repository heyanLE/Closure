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
import okio.BufferedSource
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * Created by heyanlin on 2024/3/5.
 */
class SSEHelper(
    private val okHttpClient: OkHttpClient,
) {


    var onOpening: () -> Unit = {}
    var onOpen: () -> Unit = {}
    var onMessage: (event: String, msg: String) -> Unit = { _, _ -> }
    var onError: (ex: IOException?) -> Unit = {}
    var onClose: () -> Unit = {}

    var timeout: Long = 3000L
        set(value) {
            field = value
            lastSource?.timeout()?.timeout(value, TimeUnit.MILLISECONDS)
        }


    private var isStart = false
    private val single = Executors.newSingleThreadExecutor()
    private var lastEventId: String = ""
    private var lastSource: BufferedSource? = null

    fun start(url: String) {
        innerClose()
        isStart = true
        single.execute {
            try {
                innerStart(url)
            } catch (ex: IOException) {
                if (isStart) {
                    onError(ex)
                    close()
                }
            }
        }
    }

    fun close() {
        isStart = false
        innerClose()
    }

    fun release() {
        isStart = false
        close()
        lastEventId = ""
        lastSource = null
        timeout = 3000L
    }


    private fun innerClose(){
        try {
            lastSource?.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        lastSource = null
        onClose()
    }
    @Throws(IOException::class)
    private fun innerStart(url: String) {
        onOpening()
        val call = okHttpClient.newCall(
            Request.Builder().url(url)
                .addHeader("Accept-Encoding", "")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Accept", "text/event-stream").apply {
                    if (lastEventId.isNotEmpty()) {
                        addHeader("Last-Event-ID", lastEventId)
                    }
                }.build()
        )
        val resp = call.execute()
        if (!resp.isSuccessful) {
            throw IOException("resp failed ${resp.code} ${resp.message}")
        }
        val source = resp.body?.source() ?: throw IOException("source is null")
        source.timeout().timeout(timeout, TimeUnit.MILLISECONDS)
        this.lastSource = source
        var curEvent = ""
        val sb = StringBuilder()
        onOpen()
        while (!call.isCanceled()) {
            val line = source.readUtf8LineStrict()
            line.logi(SSEController.TAG)
            if (line.isEmpty()) {
                lastEventId = curEvent
                onMessage(curEvent, sb.toString())
                curEvent = ""
                sb.clear()
                continue
            }
            val index = line.indexOf(":")
            if (index == -1) {
                curEvent = ""
                sb.clear()
                continue
            } else if (index == 0) {
                continue
            } else {
                val field = line.substring(0, index)
                val value = line.substring(index + 1, line.length)
                when (field) {
                    "data" -> {
                        sb.append(value.trim())
                    }

                    "event" -> {
                        curEvent = value.trim()
                    }

                    "retry" -> {
                        timeout = value.trim().toLongOrNull() ?: timeout
                    }
                }
            }
        }
        onClose()
    }


}