package com.heyanle.closure.ui.home.instance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.closure.ClosurePresenter
import com.heyanle.closure.closure.LoadableData
import com.heyanle.closure.closure.game.model.GetGameInfo
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.closure.logs.ClosureLogsPresenter
import com.heyanle.closure.ui.home.HomeViewModel
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by heyanle on 2024/1/27.
 * https://github.com/heyanLE
 */
class InstanceViewModel(
    private val username: String,
    private val account: String
): ViewModel() {

    private val closurePresenter: ClosurePresenter by Injekt.injectLazy(username)
    private val closureLogsPresenter: ClosureLogsPresenter by Injekt.injectLazy(username to account)

    data class InstanceState(
        val webGame: LoadableData<WebGame>,
        val getGameInfo: LoadableData<GetGameInfo>,
    )

    private val _instanceState = MutableStateFlow(InstanceState(LoadableData(), LoadableData()))
    val instanceState = _instanceState.asStateFlow()

    val closureLogState = closureLogsPresenter.logFlow

    init {
        refreshGetGameInfo()
        refreshLogs()
    }
    fun refreshGetGameInfo(){
        closurePresenter.refreshGetGameInfoFlow(account)
    }

    fun refreshLogs(){
        closureLogsPresenter.refresh()
    }

    init {
        viewModelScope.launch {
            closurePresenter.webGameList
                .collectLatest { ld ->
                    _instanceState.update {
                        it.copy(
                            webGame = ld.map {
                                it?.find { it.status.account == account }
                            }
                        )
                    }

                }
        }

        viewModelScope.launch {
            closurePresenter.getGetGameInfoFlow(account)
                .collectLatest { ld ->
                    _instanceState.update {
                        it.copy(
                            getGameInfo = ld
                        )
                    }
                }
        }
    }


}

class InstanceViewModelFactory(
    private val username: String,
    private val account: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    @SuppressWarnings("unchecked")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstanceViewModel::class.java))
            return InstanceViewModel(username, account) as T
        throw RuntimeException("unknown class :" + modelClass.name)
    }
}
