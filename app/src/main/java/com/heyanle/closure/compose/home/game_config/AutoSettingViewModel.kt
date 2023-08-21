package com.heyanle.closure.compose.home.game_config

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.R
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.net.Net
import com.heyanle.closure.net.api.GameAPI
import com.heyanle.closure.net.model.GameConfig
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toast
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by HeYanLe on 2023/1/2 17:10.
 * https://github.com/heyanLE
 */
class AutoSettingViewModel: ViewModel() {

    private val closureController: ClosureController by Injekt.injectLazy()
    private val gameApi: GameAPI by Injekt.injectLazy()

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

    // 无人机
    val accelerateSlotCN = mutableStateOf("中层左")

    companion object{
        val accelerateSlotSelected = listOf<String>(
            "顶层左",
            "顶层中",
            "顶层右",
            "中层左",
            "中层中",
            "中层右",
            "底层左",
            "底层中",
            "底层右",
        )
    }

    val gameConfig = MutableLiveData<GameConfig>()
    val isLoading = mutableStateOf(false)

    val observer = Observer<GameConfig> {
        battleMap.clear()
        battleMap.addAll(it.battleMaps?: emptyList())

        keepingAP.value = it.keepingAP.toInt()
        recruitReserve.value = it.recruitReserve.toInt()

        recruitIgnoreRobot.value = it.recruitIgnoreRobot
        enableBuildingArrange.value = it.enableBuildingArrange
        accelerateSlotCN.value = it.accelerateSlotCN
    }

    init {
        gameConfig.observeForever(observer)
    }

    suspend fun refresh(
        gameResp: GameResp,
    ){
        val token = closureController.token.value
        val platform = gameResp.config.platform
        val account = gameResp.config.account
        isLoading.value = true
        gameApi.getConfig(token, platform, account).awaitResponseOK()
            .onSuccessful {
                withContext(Dispatchers.Main){
                    isLoading.value = false
                    gameConfig.value = it
                }

            }.onFailed { b, s ->
                withContext(Dispatchers.Main){
                    isLoading.value = false
                    gameConfig.value = gameResp.gameConfig
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
        oldConfig.accelerateSlotCN = accelerateSlotCN.value
        return oldConfig
    }

    override fun onCleared() {
        super.onCleared()
        gameConfig.removeObserver(observer)
    }


}