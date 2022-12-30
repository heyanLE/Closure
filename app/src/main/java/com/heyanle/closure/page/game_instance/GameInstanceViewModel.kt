package com.heyanle.closure.page.game_instance

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geetest.sdk.GT3ErrorBean
import com.geetest.sdk.GT3Listener
import com.heyanle.closure.MainActivity
import com.heyanle.closure.R
import com.heyanle.closure.net.Net
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.page.MainController
import com.heyanle.closure.page.data
import com.heyanle.closure.page.error
import com.heyanle.closure.page.loading
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toast
import com.heyanle.closure.utils.todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by HeYanLe on 2022/12/23 22:01.
 * https://github.com/heyanLE
 */
class GameInstanceViewModel: ViewModel() {

    val loadingDialogEnable = mutableStateOf(false)
    val deleteDialogEnable = mutableStateOf(false)
    val addDialogEnable = mutableStateOf(false)

    init {
        viewModelScope.launch {
            val instance = MainController.instance.value
            if(instance == null || instance.isNone()){
                loadGameInstances()
            }

        }
    }

    fun onAddClick(){
        todo("添加实例按钮")
    }

    suspend fun loadGameInstances(){
        MainController.instance.loading()
        Net.game.get((MainController.token.value)?:"").awaitResponseOK()
            .onSuccessful {
                withContext(Dispatchers.Main){
                    if(it == null){
                        MainController.instance.error(stringRes(R.string.load_error))
                    }else{
                        MainController.instance.data(it)
                    }

                }
            }.onFailed { b, s ->
                MainController.instance.error(s)
            }
    }

    suspend fun gameLogin(gameResp: GameResp){
        val config = gameResp.gameConfig.copy(
            isStopped = false
        )
        val token = MainController.token.value?:""
        val account = gameResp.config.account
        val platform = gameResp.config.platform
        Net.game.postConfig(token, platform, account, config).awaitResponseOK()
            .onSuccessful {
                loadGameInstances()
                withContext(Dispatchers.Main){
                    stringRes(R.string.game_login_completely).toast()
                }
            }.onFailed { b, s ->
                withContext(Dispatchers.Main){
                    s.toast()
                }
            }
    }

    suspend fun gameCaptcha(gameResp: GameResp, mainActivity: MainActivity){
        todo("滑动验证，这里能滑但是还没接入可小姐")
        mainActivity.onCaptcha(gameResp.captchaInfo, object: GT3Listener() {
            override fun onReceiveCaptchaCode(p0: Int) {
                Log.d("GameInstanceViewModel", "onReceiveCaptchaCode $p0")
            }

            override fun onStatistics(p0: String?) {
                Log.d("GameInstanceViewModel", "onStatistics $p0")
            }

            override fun onClosed(p0: Int) {
                Log.d("GameInstanceViewModel", "onClosed $p0")
            }

            override fun onSuccess(p0: String?) {
                Log.d("GameInstanceViewModel", "onSuccess $p0")
            }

            override fun onFailed(p0: GT3ErrorBean?) {
                Log.d("GameInstanceViewModel", "onFailed $p0")
            }

            override fun onButtonClick() {
                Log.d("GameInstanceViewModel", "onButtonClick")
            }
        })
    }

    suspend fun gamePause(gameResp: GameResp){
        val config = gameResp.gameConfig.copy(
            isStopped = true
        )
        val token = MainController.token.value?:""
        val account = gameResp.config.account
        val platform = gameResp.config.platform
        Net.game.postConfig(token, platform, account, config).awaitResponseOK()
            .onSuccessful {
                loadGameInstances()
                withContext(Dispatchers.Main){
                    stringRes(R.string.game_pause_completely).toast()
                }
            }.onFailed { b, s ->
                withContext(Dispatchers.Main){
                    s.toast()
                }
            }
    }

    suspend fun instanceDelete(gameResp: GameResp){
        todo("删除实例")
    }

}