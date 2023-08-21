package com.heyanle.closure.compose.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.MainActivity
import com.heyanle.closure.R
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.compose.common.moeSnackBar
import com.heyanle.closure.net.Net
import com.heyanle.closure.net.api.GameAPI
import com.heyanle.closure.net.model.CaptchaInfo
import com.heyanle.closure.net.model.CaptchaReq
import com.heyanle.closure.net.model.CreateGameReq
import com.heyanle.closure.net.model.GameConfig
import com.heyanle.closure.net.model.GameLoginReq
import com.heyanle.closure.net.model.GameReq
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.utils.ViewModelOwnerMap
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.onSuccessfulNullable
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toast
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Created by HeYanLe on 2023/8/20 15:12.
 * https://github.com/heyanLE
 */
class HomeViewModel : ViewModel() {

    private val viewModelOwnerMap = ViewModelOwnerMap<GameResp>()

    private val refreshListener = hashMapOf<GameResp, () -> Unit>()

    private val closureController: ClosureController by Injekt.injectLazy()

    private val gameApi: GameAPI by Injekt.injectLazy()


    val avatarImg = mutableStateOf<Any>(R.drawable.logo)
    val topBarTitle = mutableStateOf(stringRes(R.string.app_name))

    val deleteDialog = mutableStateOf<GameResp?>(null)
    val configDialog = mutableStateOf<GameResp?>(null)
    val screenshotDialog = mutableStateOf<GameResp?>(null)
    val bindQQDialog = mutableStateOf<GameResp?>(null)

    val addDialog = mutableStateOf(false)

    val gameList = mutableStateListOf<GameResp>()
    val gameState = closureController.gameData

    val isLoading = mutableStateOf(false)
    val loadingErrorMsg = mutableStateOf("")


    init {
        if (closureController.token.value.isNotEmpty()) {
            closureController.updateGameList()
        }
        viewModelScope.launch {
            closureController.gameData.collectLatest {
                if (!it.loading) {
                    gameList.clear()
                    gameList += it.gameList
                }
                isLoading.value = it.loading
                loadingErrorMsg.value = it.errorMsg
            }
        }
    }

    fun getViewModelOwner(gameResp: GameResp): ViewModelStoreOwner {
        return viewModelOwnerMap.getViewModelStoreOwner(gameResp)
    }

    fun newRefreshListener(gameResp: GameResp, listener: () -> Unit) {
        refreshListener[gameResp] = listener
    }

    fun onClearToken() {
        closureController.clearToken()
    }

    fun onRefreshList() {
        closureController.updateGameList()
    }

    fun onRefresh(gameResp: GameResp) {
        refreshListener[gameResp]?.invoke()
    }

    fun onAddInstance(createGameReq: CreateGameReq){
        val token = closureController.token.value.ifEmpty { return }
        viewModelScope.launch {
            gameApi.post(token, createGameReq)
                .awaitResponseOK()
                .onSuccessfulNullable {
                    stringRes(R.string.instance_add_completely).moeSnackBar()
                    delay(500)
                    onRefreshList()
                }
                .onFailed { b, s ->
                    s.moeSnackBar()
                }
        }
    }

    fun onOpen(gameResp: GameResp) {
        val token = closureController.token.value.ifEmpty { return }
        viewModelScope.launch {
            gameApi.login(token, GameLoginReq(gameResp.config.account, gameResp.config.platform))
                .awaitResponseOK()
                .onSuccessfulNullable {
                    stringRes(R.string.game_login_completely).moeSnackBar()
                    delay(500)
                    onRefreshList()
                }
                .onFailed { b, s ->
                    s.moeSnackBar()
                }
        }
    }

    fun onPause(gameResp: GameResp) {
        val token = closureController.token.value.ifEmpty { return }
        viewModelScope.launch {
            gameApi.getConfig(token, gameResp.config.platform, gameResp.config.account)
                .awaitResponseOK()
                .onSuccessful {
                    stringRes(R.string.game_pause_completely).moeSnackBar()
                    onPostConfig(gameResp, it.copy(isStopped = true))
                }.onFailed { b, s ->
                    onPostConfig(gameResp, gameResp.gameConfig.copy(isStopped = true))
                }
        }
    }

    fun onDelete(gameResp: GameResp){
        val token = closureController.token.value.ifEmpty { return }
        viewModelScope.launch {
            gameApi.delete(token, GameReq(gameResp.config.account, gameResp.config.platform))
                .awaitResponseOK()
                .onSuccessfulNullable {
                    stringRes(R.string.instance_delete_completely).moeSnackBar()
                    delay(500)
                    onRefreshList()
                }.onFailed { b, s ->
                    s.moeSnackBar()
                }
        }
    }

    fun onPostConfig(gameResp: GameResp, gameConfig: GameConfig) {
        val token = closureController.token.value.ifEmpty { return }
        viewModelScope.launch {
            gameApi.postConfig(token, gameResp.config.platform, gameResp.config.account, gameConfig)
                .awaitResponseOK()
                .onSuccessful {
                    stringRes(R.string.post_config_completely).moeSnackBar()
                    delay(500)
                    onRefreshList()
                    onRefresh(gameResp)
                }
                .onFailed { b, s ->
                    s.moeSnackBar()
                }
        }

    }

    override fun onCleared() {
        super.onCleared()
        refreshListener.clear()
    }

    fun onCaptcha(act: MainActivity, gameResp: GameResp){
        act.onCaptcha(gameResp.captchaInfo) {
            onCaptchaPost(gameResp, it)
        }
    }
    private fun onCaptchaPost(gameResp: GameResp, string: String){
        val token = closureController.token.value.ifEmpty { return }
        viewModelScope.launch {
            val account = gameResp.config.account
            val platform = gameResp.config.platform
            val jsonObject = JSONObject(string)
            gameApi.postCaptcha(token, platform, account, CaptchaReq(
                challenge = gameResp.captchaInfo.challenge,
                geetestChallenge = jsonObject.getString("geetest_challenge"),
                geetestSeccode = jsonObject.getString("geetest_seccode"),
                geetestValidate = jsonObject.getString("geetest_validate"),
                success = true,
            )
            ).awaitResponseOK()
                .onSuccessful {
                    withContext(Dispatchers.Main){
                        stringRes(R.string.captcha_sus).toast()
                    }
                    onRefreshList()
                }.onFailed { b, s ->
                    withContext(Dispatchers.Main){
                        s.toast()
                    }
                }
        }
    }


}