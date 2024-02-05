package com.heyanle.closure.ui.home.instance_manage

import android.view.ViewManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.closure.game.GameRepository
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.closure.quota.QuotaRepository
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.launch

/**
 * Created by heyanlin on 2024/2/4 14:45.
 */
class InstanceManagerViewModel: ViewModel() {


    private val closureController: ClosureController by Injekt.injectLazy()
    private val quotaRepository: QuotaRepository by Injekt.injectLazy()
    private val gameRepository: GameRepository by Injekt.injectLazy()

    fun onOpen(webGame: WebGame){

    }

    fun onPause(webGame: WebGame){}

    fun onDelete(webGame: WebGame){}

    fun onCaptcha(webGame: WebGame){}

    fun onConfig(webGame: WebGame){}


}