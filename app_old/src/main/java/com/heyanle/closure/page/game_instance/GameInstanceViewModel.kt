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
import com.heyanle.closure.net.model.CaptchaReq
import com.heyanle.closure.net.model.CreateGameReq
import com.heyanle.closure.net.model.GameConfig
import com.heyanle.closure.net.model.GameLoginReq
import com.heyanle.closure.net.model.GameReq
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
import com.heyanle.closure.utils.TODO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Created by HeYanLe on 2022/12/23 22:01.
 * https://github.com/heyanLE
 */
class GameInstanceViewModel: ViewModel() {

    val enableAutoSettingDialog = mutableStateOf(false)

    val loadingDialogEnable = mutableStateOf(false)

    var deleteResp: GameResp? = null
    val deleteDialogEnable = mutableStateOf(false)

    val addDialogEnable = mutableStateOf(false)

//    init {
//        viewModelScope.launch {
//            val instance = MainController.instance.value
//            if(instance == null || instance.isNone()){
//                loadGameInstances()
//            }
//
//        }
//    }

    fun onAddClick(){
        addDialogEnable.value = true
    }

    fun onDeleteClick(gameResp: GameResp){
        deleteResp = gameResp
        deleteDialogEnable.value = true
    }

    suspend fun updateConfig(gameConfig: GameConfig, gameResp: GameResp){
        val token = MainController.token.value?:""
        Net.game.postConfig(token, gameResp.config.platform, gameResp.config.account, gameConfig).awaitResponseOK()
            .onSuccessful {
                //loadGetGameResp()
                viewModelScope.launch {
                    enableAutoSettingDialog.value = false
                    stringRes(R.string.post_config_completely).toast()
                    loadGameInstances()
                }
            }.onFailed { b, s ->
                withContext(Dispatchers.Main){
                    s.toast()
                }
            }
    }

    suspend fun loadGameInstances(){
        MainController.instance.loading()
        Net.game.get((MainController.token.value)?:"").awaitResponseOK()
            .onSuccessful {
                withContext(Dispatchers.Main){
                    if(it == null){
                        MainController.instance.data(emptyList())
                    }else{
                        MainController.instance.data(it)
                    }

                }
            }.onFailed { b, s ->
                MainController.instance.error(s)
            }
    }

    suspend fun gameLogin(gameResp: GameResp){
        val token = MainController.token.value?:""
        val account = gameResp.config.account
        val platform = gameResp.config.platform
        Net.game.login(token, GameLoginReq(
            account = account,
            platform = platform.toInt()
        )).awaitResponseOK()
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
        mainActivity.onCaptcha(gameResp.captchaInfo) {
            onCaptchaPost(gameResp, it)
        }
    }

    private fun onCaptchaPost(gameResp: GameResp, string: String){
        viewModelScope.launch {
            val token = MainController.token.value?:""
            val account = gameResp.config.account
            val platform = gameResp.config.platform
            val jsonObject = JSONObject(string)
            Net.game.postCaptcha(token, platform, account, CaptchaReq(
                challenge = gameResp.captchaInfo.challenge,
                geetestChallenge = jsonObject.getString("geetest_challenge"),
                geetestSeccode = jsonObject.getString("geetest_seccode"),
                geetestValidate = jsonObject.getString("geetest_validate"),
                success = true,
            )).awaitResponseOK()
                .onSuccessful {
                    withContext(Dispatchers.Main){
                        stringRes(R.string.captcha_sus).toast()
                    }
                    loadGameInstances()
                }.onFailed { b, s ->
                    withContext(Dispatchers.Main){
                        s.toast()
                    }
                }
        }
    }

    suspend fun gamePause(gameResp: GameResp){
        loadingDialogEnable.value = true
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
                    loadingDialogEnable.value = false
                    stringRes(R.string.game_pause_completely).toast()
                }
            }.onFailed { b, s ->
                withContext(Dispatchers.Main){
                    loadingDialogEnable.value = false
                    s.toast()
                }
            }
    }

    suspend fun instanceDelete(gameResp: GameResp){
        loadingDialogEnable.value = true
        val token = MainController.token.value?:""
        val account = gameResp.config.account
        val platform = gameResp.config.platform
        Net.game.delete(token, GameReq(account, platform.toInt())).awaitResponseOK()
            .onSuccessful {
                loadGameInstances()
                withContext(Dispatchers.Main){
                    deleteDialogEnable.value = false
                    loadingDialogEnable.value = false
                    stringRes(R.string.instance_delete_completely).toast()
                    MainController.current.value?.let {
                        if(it.account == gameResp.config.account && it.platform == gameResp.config.platform){
                            MainController.current.value =
                                MainController.InstanceSelect(
                                    "",
                                    -1
                                )
                        }
                    }
                }
            }.onFailed { b, s ->
                withContext(Dispatchers.Main){
                    loadingDialogEnable.value = false
                    deleteDialogEnable.value = false
                    s.toast()
                }
            }
    }

    suspend fun instanceAdd(createGameReq: CreateGameReq){
        loadingDialogEnable.value = true
        val token = MainController.token.value?:""
        Net.game.post(token, createGameReq).awaitResponseOK()
            .onSuccessful {
                loadGameInstances()
                withContext(Dispatchers.Main){
                    loadingDialogEnable.value = false
                    addDialogEnable.value = false
                    stringRes(R.string.instance_add_completely).toast()
                }
            }
            .onFailed { b, s ->
                withContext(Dispatchers.Main){
                    loadingDialogEnable.value = false
                    addDialogEnable.value = true
                    s.toast()
                }
            }
    }

}