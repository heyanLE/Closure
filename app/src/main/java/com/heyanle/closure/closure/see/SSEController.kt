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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.IOException

/**
 * Created by heyanle on 2024/1/27.
 * https://github.com/heyanLE
 */
class SSEController(
    private val okHttpClient: OkHttpClient,
    private val closureController: ClosureController,
) {

    companion object {
        const val TAG = "SSEController"
    }


    data class SSEState(
        // sse 开关
        val isEnable: Boolean = true,

        // 是否已经触发启动逻辑
        val isStarted: Boolean = false,

        // 是否真正启动
        val isActive: Boolean = false,
    )

    @Volatile
    private var username: String = ""
    @Volatile
    private var token: String = ""

    private val _sta = MutableStateFlow<SSEState>(SSEState())
    val sta = _sta.asStateFlow()
    private val helper = SSEHelper(okHttpClient).apply {
        onOpen = {
            _sta.update {
                it.copy(isActive = true, isStarted = true)
            }
        }
        onClose = {
            _sta.update {
                it.copy(
                    isStarted = false,
                    isActive = false
                )
            }
        }
        onError = {
            it?.printStackTrace()
            _sta.update {
                it.copy(isStarted = false, isActive = false)
            }
        }
        onMessage = { event, msg ->
            if (username.isNotEmpty()){
                handleMessage(event, msg, username)
            }
        }

    }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch {

            combine(
                closureController.state.map { it.username to it.token }.distinctUntilChanged(),
                _sta,
            ) { p, state ->
                if (state.isEnable && !state.isStarted) {
                    if (p.first.isNotEmpty() && p.second.isNotEmpty()) {
                        helper.start("https://api.ltsc.vip/sse/games?token=${p.second}")
                        _sta.update {
                            it.copy(isStarted = true)
                        }
                    }

                } else if (!state.isEnable && state.isActive) {
                    helper.close()
                }
                username = p.first
                token = p.second
            }.collect()
        }
    }

    fun enable() {
        disable()
        _sta.update {
            it.copy(isEnable = true, isStarted = false)
        }
    }

    fun disable() {
        _sta.update {
            it.copy(isEnable = false, isStarted = false)
        }
    }



    private fun handleMessage(event: String, data: String, username: String) {
        "handleMessage ${event} ${data} ${username}".logi(TAG)
        when (event) {
            "game" -> {
                val webGame = data.jsonTo<List<WebGame>>()
                if (webGame != null) {
                    val presenter = Injekt.get<ClosurePresenter>(username)
                    presenter.onSEEPush(webGame)
                }

            }

            "close" -> {
                disable()
                helper.close()
                stringRes(R.string.connect_unlink_because_other).moeSnackBar()
            }
        }
    }

}