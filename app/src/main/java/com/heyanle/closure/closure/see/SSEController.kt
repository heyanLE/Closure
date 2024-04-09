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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

/**
 * Created by heyanle on 2024/1/27.
 * https://github.com/heyanLE
 */
class SSEController(
    private val okHttpClient: OkHttpClient,
    private val closureController: ClosureController,
): SSEHelper.SSEListener {

    companion object {
        const val TAG = "SSEController"
        const val URL = "https://api.ltsc.vip/sse/games?token={{token}}"
    }


    data class SSEState(
        val username: String = "",
        val token: String = "",

        // sse 开关
        val isEnable: Boolean = true,

        // 连接中
        val isLoading: Boolean = false,

        // 是否真正启动
        val isActive: Boolean = false,
    ){
        companion object {
            val init = SSEState()
        }
    }

    private val _sta = MutableStateFlow<SSEState>(SSEState.init)
    val sta = _sta.asStateFlow()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val singleScope = CoroutineScope(SupervisorJob() + CoroutineProvider.SINGLE)
    private var helper: SSEHelper? = null

    init {
        scope.launch {
            closureController.state.map { it.username to it.token }.distinctUntilChanged().collectLatest { pair ->
                if (pair.first.isNotEmpty() && pair.second.isNotEmpty()){
                    singleScope.launch {
                        val cur = sta.value
                        if (cur == SSEState.init || cur.username != pair.first || cur.token != pair.second){
                            _sta.update {
                                it.copy(username = pair.first, token = pair.second)
                            }
                            relink()
                        }
                    }
                }else{
                    helper?.release()
                    helper = null
                }

            }
        }
    }

    fun relink() {
        singleScope.launch {
            val cur = sta.value
            if (cur.isEnable){
                innerConnect()
            }
        }
    }

    private fun innerConnect() {
        helper?.release()
        _sta.update {
            it.copy(isLoading = true)
        }
        helper = SSEHelper(okHttpClient, URL.replace("{{token}}", sta.value.token), this)
        helper?.start()
    }



    override fun onOpen() {
        singleScope.launch {
            _sta.update {
                it.copy(isActive = true, isLoading = false)
            }
        }
    }

    override fun onMessage(event: String, data: String) {
        singleScope.launch {
            handleMessage(event, data, sta.value.username)
        }

    }

    override fun onError(ex: Throwable?) {
        ex?.printStackTrace()
        ex?.message?.moeSnackBar()
        singleScope.launch {
            _sta.update {
                it.copy(isLoading = false, isActive = false)
            }
        }
    }

    override fun onClose() {
        singleScope.launch {
            _sta.update {
                it.copy(isActive = false, isLoading = false)
            }
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
                _sta.update {
                    it.copy(isEnable = false)
                }
                helper?.release()
                helper = null
                stringRes(R.string.connect_unlink_because_other).moeSnackBar()
            }
        }
    }

}