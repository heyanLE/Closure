package com.heyanle.closure.ui.home

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.R
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.closure.ClosurePresenter
import com.heyanle.closure.closure.game.GameRepository
import com.heyanle.closure.closure.game.model.GameUpdateConfig
import com.heyanle.closure.closure.game.model.UpdateCaptchaInfo
import com.heyanle.closure.closure.game.model.UpdateGameInfo
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.closure.quota.QuotaRepository
import com.heyanle.closure.geetest.GeetestHelper
import com.heyanle.closure.ui.common.moeSnackBar
import com.heyanle.closure.utils.ViewModelOwnerMap
import com.heyanle.closure.utils.logi
import com.heyanle.closure.utils.stringRes
import com.heyanle.injekt.api.get
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * Created by heyanlin on 2024/1/18 16:55.
 */
class HomeViewModel(
    private val username: String,
    private val geetestHelper: GeetestHelper,
) : ViewModel() {

    private val closureController: ClosureController by Injekt.injectLazy()
    private val gameRepository: GameRepository by Injekt.injectLazy()
    private val quotaRepository: QuotaRepository by Injekt.injectLazy()
    private val closurePresenter: ClosurePresenter by Injekt.injectLazy(username)

    val account = closurePresenter.account
    val webGameList = closurePresenter.webGameList

    sealed class TopAppBarState {
        data object Normal: TopAppBarState()

        data object InstanceManager: TopAppBarState()

        data class Instance(
            val webGame: WebGame
        ): TopAppBarState()

    }

    val topAppBarState = mutableStateOf<TopAppBarState>(TopAppBarState.Normal)
    val title = mutableStateOf(stringRes(com.heyanle.i18n.R.string.app_name))
    val icon = mutableStateOf("")

    private val instanceVMMap = ViewModelOwnerMap<String>()

    fun getViewModelOwner(account: String) = instanceVMMap.getViewModelStoreOwner(account)

    override fun onCleared() {
        super.onCleared()
        instanceVMMap.clear()
    }


    fun onOpen(webGame: WebGame) {
        viewModelScope.launch {
            val token = closureController.tokenIfNull(username) ?: return@launch
            val captchaToken = geetestHelper.onGT4()
            captchaToken.logi("HomeViewModel")
            if (captchaToken == null) {
                stringRes(com.heyanle.i18n.R.string.captcha_error).moeSnackBar()
            } else {
                gameRepository.awaitStartGame(
                    webGame.status.account,
                    token,
                    captchaToken
                ).okWithData {
                    it.okNullable {
                        it.message?.moeSnackBar()
                    }.snackWhenError()
                }.snackWhenError()
            }
            closurePresenter.refreshGetGameInfoFlow(webGame.status.account)
        }
    }

    fun onPause(webGame: WebGame) {
        viewModelScope.launch {
            val token = closureController.tokenIfNull(username) ?: return@launch
            gameRepository.awaitUpdateGame(
                webGame.status.account,
                token,
                UpdateGameInfo(
                    GameUpdateConfig.fromGameSetting(webGame.gameSetting).copy(
                        isStopped = true,
                    )
                )
            ).okWithData {
                //it.sna
                it.okNullable {
                    it.message?.moeSnackBar()
                }.snackWhenError()
            }.error {
                it.snackWhenError()
            }
            closurePresenter.refreshGetGameInfoFlow(webGame.status.account)
        }
    }

    fun onDelete(webGame: WebGame) {
//        viewModelScope.launch {
//            val token = closureController.tokenIfNull(username) ?: return@launch
//            val captchaToken = geetestHelper.onGT4()
//            if (captchaToken == null) {
//                stringRes(com.heyanle.i18n.R.string.captcha_error).moeSnackBar()
//            } else {
//                quotaRepository.awaitDeleteGame(
//                    token,
//                    webGame.status.uuid,
//                    captchaToken
//                ).okWithData {
//                    it.results.slotUserSmsVerified.message.moeSnackBar()
//                }.snackWhenError()
//            }
//        }
    }

    fun onCaptcha(webGame: WebGame) {
        viewModelScope.launch {
            val token = closureController.tokenIfNull(username) ?: return@launch
            val json = geetestHelper.onGT3(webGame.captchaInfo)
            if(json == null){
                stringRes(com.heyanle.i18n.R.string.captcha_error).moeSnackBar()
                return@launch
            }
            val jsonObject = JSONObject(json)
            val geetestChallenge = jsonObject.getString("geetest_challenge")
            val geetestSeccode = jsonObject.getString("geetest_seccode")
            val geetestValidate = jsonObject.getString("geetest_validate")
            gameRepository.awaitUpdateGame(
                webGame.status.account,
                token,
                UpdateGameInfo(
                    captchaInfo = UpdateCaptchaInfo(
                        challenge = webGame.captchaInfo.challenge,
                        geetestValidate = geetestValidate,
                        geetestChallenge = geetestChallenge,
                        geetestSeccode = geetestSeccode
                    )
                )
            ).okWithData {
                //it.sna
                it.okNullable {
                    it.message?.moeSnackBar()
                }.snackWhenError()
            }.error {
                it.snackWhenError()
            }
            closurePresenter.refreshGetGameInfoFlow(webGame.status.account)

        }
    }

    fun onConfig(webGame: WebGame) {}

    fun refresh(webGame: WebGame){
        closurePresenter.refreshGetGameInfoFlow(webGame.status.account)
    }

}

class HomeViewModelFactory(
    private val username: String,
    private val geetestHelper: GeetestHelper
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    @SuppressWarnings("unchecked")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(username, geetestHelper) as T
        throw RuntimeException("unknown class :" + modelClass.name)
    }
}

