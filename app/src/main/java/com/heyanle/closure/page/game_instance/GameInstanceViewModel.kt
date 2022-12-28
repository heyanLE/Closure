package com.heyanle.closure.page.game_instance

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.MainController
import com.heyanle.closure.R
import com.heyanle.closure.net.Net
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.stringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

/**
 * Created by HeYanLe on 2022/12/23 22:01.
 * https://github.com/heyanLE
 */
class GameInstanceViewModel: ViewModel() {

    var errorCode  = stringRes(R.string.net_error)
    var isLoading = MutableLiveData(false)
    var isError = MutableLiveData(false)

    init {
        val list = MainController.gameInstance.value
        if(list == null){
            viewModelScope.launch {
                refresh()
            }
        }
    }

    suspend fun refresh(){
        isLoading.value = true
        isError.value = false
        Net.game.get(MainController.token.value?:"").awaitResponseOK()
            .onSuccessful {
                withContext(Dispatchers.Main){
                    if(it == null){
                        isLoading.value = false
                        isError.value = true
                        errorCode  = stringRes(R.string.load_error)
                    }else{
                        isLoading.value = false
                        isError.value = false
                        MainController.gameInstance.value = it
                    }

                }
            }.onFailed { b, s ->
                errorCode  = s
                isError.value = true
                isLoading.value = false
            }
    }

}