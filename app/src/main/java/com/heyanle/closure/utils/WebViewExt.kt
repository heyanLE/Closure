package com.heyanle.closure.utils

import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by heyanlin on 2023/12/31.
 */
suspend fun WebView.waitPageFinished(timeout: Long = 5000L) {
    withTimeout(timeout) {
        suspendCoroutine<Unit> { con ->
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    con.resume(Unit)
                }
            }
        }
    }
}

suspend fun WebView.evaluateJavascript(javaScript: String): String {
    var res = suspendCoroutine { con ->
        evaluateJavascript(javaScript) {
            con.resume(it ?: "")
        }
    }
    if (res.startsWith("\"")) {
        res = res.substring(1)
    }
    if (res.endsWith("\"")) {
        res = res.substring(0, res.lastIndex - 1)
    }
    return res
}