package com.heyanle.closure.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.closure.game.GameRepository
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.utils.koin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Created by heyanlin on 2024/1/18 16:55.
 */
class HomeViewModel(
    private val token: String,
): ViewModel() {


    private val gameRepository: GameRepository by koin.inject()

    private val _webGameList = MutableStateFlow(emptyList<WebGame>())
    val webGameList = _webGameList.value




    init {



    }


}