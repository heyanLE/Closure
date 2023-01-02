package com.heyanle.closure.page.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.R
import com.heyanle.closure.net.Net
import com.heyanle.closure.net.model.GameConfig
import com.heyanle.closure.page.MainController
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by HeYanLe on 2023/1/2 17:10.
 * https://github.com/heyanLE
 */
class AutoSettingViewModel: ViewModel() {

    // 地图序列
    val battleMap = mutableStateListOf<String>()

    // 理智保留
    val keepingAP = mutableStateOf(0)

    // 公招券保留
    val recruitReserve = mutableStateOf(0)

    // 招募支援机械
    val recruitIgnoreRobot = mutableStateOf(false)

    // 基建排班
    val enableBuildingArrange = mutableStateOf(true)

    val gameConfig = MutableLiveData<GameConfig>()
    val isLoading = mutableStateOf(false)
    val isError = mutableStateOf(false)
    val errorMsg = mutableStateOf("")

    val observer = Observer<GameConfig> {
        battleMap.clear()
        battleMap.addAll(it.battleMaps?: emptyList())

        keepingAP.value = it.keepingAP.toInt()
        recruitReserve.value = it.recruitReserve.toInt()

        recruitIgnoreRobot.value = it.recruitIgnoreRobot
        enableBuildingArrange.value = it.enableBuildingArrange
    }

    init {
        gameConfig.observeForever(observer)
        viewModelScope.launch {
            refresh()
        }
    }

    suspend fun refresh(){
        val token = MainController.token.value?:""
        val current = MainController.current.value
        val account = current?.account?:""
        val platform = current?.platform?:-1L
        isLoading.value = true
        isError.value = false
        Net.game.getConfig(token, platform, account).awaitResponseOK()
            .onSuccessful {
                withContext(Dispatchers.Main){
                    isLoading.value = false
                    if(it == null){
                        isError.value = true
                        errorMsg.value = stringRes(R.string.net_error)
                    }else{
                        isError.value = false
                        gameConfig.value = it
                    }
                }

            }.onFailed { b, s ->
                withContext(Dispatchers.Main){
                    s.toast()
                    isError.value = true
                    errorMsg.value = s
                }
            }
    }

    fun newConfig(): GameConfig? {
        val oldConfig = (gameConfig.value?.copy()) ?: return null

        val list = arrayListOf<String>()
        list.addAll(battleMap)
        oldConfig.battleMaps = list

        oldConfig.keepingAP = keepingAP.value.toLong()
        oldConfig.recruitReserve = recruitReserve.value.toLong()
        oldConfig.enableBuildingArrange = enableBuildingArrange.value
        oldConfig.recruitIgnoreRobot = recruitIgnoreRobot.value
        return oldConfig
    }

    override fun onCleared() {
        super.onCleared()
        gameConfig.removeObserver(observer)
    }


}