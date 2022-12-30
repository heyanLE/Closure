package com.heyanle.closure.page.game_instance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.R
import com.heyanle.closure.net.Net
import com.heyanle.closure.page.MainController
import com.heyanle.closure.page.data
import com.heyanle.closure.page.error
import com.heyanle.closure.page.loading
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.stringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by HeYanLe on 2022/12/23 22:01.
 * https://github.com/heyanLE
 */
class GameInstanceViewModel: ViewModel() {

    init {
        viewModelScope.launch {
            val instance = MainController.instance.value
            if(instance == null || instance.isNone()){
                loadGameInstances()
            }

        }
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

    suspend fun gameLogin(){}

    suspend fun gameCaptcha(){}

    suspend fun gamePause(){}

    suspend fun instanceDelete(){}

}