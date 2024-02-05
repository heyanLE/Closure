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
import com.heyanle.closure.closure.game.model.UpdateGameInfo
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.geetest.GeetestHelper
import com.heyanle.closure.utils.ViewModelOwnerMap
import com.heyanle.closure.utils.stringRes
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Created by heyanlin on 2024/1/18 16:55.
 */
class HomeViewModel(
    private val username: String
): ViewModel() {

    private val closureController: ClosureController by Injekt.injectLazy()
    private val gameRepository: GameRepository by Injekt.injectLazy()
    private val closurePresenter: ClosurePresenter by Injekt.injectLazy(username)

    val account = closurePresenter.account
    val webGameList = closurePresenter.webGameList

    val title = mutableStateOf(stringRes(com.heyanle.i18n.R.string.app_name))

    private val instanceVMMap = ViewModelOwnerMap<String>()

    fun getViewModelOwner(account: String) = instanceVMMap.getViewModelStoreOwner(account)

    override fun onCleared() {
        super.onCleared()
        instanceVMMap.clear()
    }


    fun onOpen(webGame: WebGame){
        viewModelScope.launch {
            val token = closureController.awaitToken(username) ?: return@launch
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
                it.okWithData {  }
            }.error {
                it.snackWhenError()
            }
        }
    }

    fun onPause(webGame: WebGame){}

    fun onDelete(webGame: WebGame){}

    fun onCaptcha(webGame: WebGame, geetestHelper: GeetestHelper){

    }

    fun onConfig(webGame: WebGame){}

}

class HomeViewModelFactory(
    private val username: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    @SuppressWarnings("unchecked")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(username) as T
        throw RuntimeException("unknown class :" + modelClass.name)
    }
}

