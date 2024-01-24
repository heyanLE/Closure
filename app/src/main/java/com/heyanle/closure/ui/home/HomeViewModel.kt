package com.heyanle.closure.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.closure.ClosurePresenter
import com.heyanle.closure.closure.game.GameRepository
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Created by heyanlin on 2024/1/18 16:55.
 */
class HomeViewModel(
    private val username: String
): ViewModel() {


    private val gameRepository: GameRepository by Injekt.injectLazy()
    private val closurePresenter: ClosurePresenter by Injekt.injectLazy(username)

    val account = closurePresenter.account
    val webGameList = closurePresenter.webGameList

}