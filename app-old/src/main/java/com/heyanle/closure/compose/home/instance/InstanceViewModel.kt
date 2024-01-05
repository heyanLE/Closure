package com.heyanle.closure.compose.home.instance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.R
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.closure.entity.GameInfo
import com.heyanle.closure.closure.entity.GameSummary
import com.heyanle.closure.closure.items.ItemBean
import com.heyanle.closure.closure.items.ItemsController
import com.heyanle.closure.closure.repository.InstanceRepository
import com.heyanle.closure.compose.common.moeSnackBar
import com.heyanle.closure.net.api.GameAPI
import com.heyanle.closure.net.model.GameLogItem
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.net.model.GetGameResp
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.onSuccessfulNullable
import com.heyanle.closure.utils.stringRes
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2023/8/20 16:30.
 * https://github.com/heyanLE
 */
class InstanceViewModel(
    private val gameResp: GameResp
) : ViewModel() {

    var isShowLog by mutableStateOf(true)

    private val instanceRepository: InstanceRepository by Injekt.injectLazy()
    private val closureController: ClosureController by Injekt.injectLazy()
    private val gameAPI: GameAPI by Injekt.injectLazy()

    sealed class GameInfoState {
        object None : GameInfoState()

        object Empty : GameInfoState()

        object Loading : GameInfoState()

        class Info(
            val gameInfo: GameInfo
        ) : GameInfoState()

    }


    var gameInfo = mutableStateOf<GameInfoState>(GameInfoState.None)

    var ocrBtnEnable = mutableStateOf(true)

    var logList = mutableListOf<GameLogItem>()

    fun refresh() {
        loadGameInfo()
        loadLog()
    }

    private fun loadGameInfo() {
        val token = closureController.token.value.ifEmpty { return }
        gameInfo.value = GameInfoState.Loading
        viewModelScope.launch {
            gameAPI.game(token, gameResp.config.platform, gameResp.config.account)
                .awaitResponseOK()
                .onSuccessful { net ->
                    val info = GameInfo.fromGetGameResp(net)
                    instanceRepository.getInstanceInfoLocal(
                        GameSummary(
                            gameResp.config.platform,
                            gameResp.config.account
                        )
                    )
                        .onOK {
                            if (net.lastFreshTs < it.lastFreshTs) {
                                instanceRepository.updateInstanceInfoLocal(
                                    GameSummary(
                                        gameResp.config.platform,
                                        gameResp.config.account
                                    ), info.copy(
                                        lastFreshTs = it.lastFreshTs,
                                        consumable = it.consumable,
                                        inventory = it.inventory
                                    )
                                )
                            } else {
                                instanceRepository.updateInstanceInfoLocal(
                                    GameSummary(
                                        gameResp.config.platform,
                                        gameResp.config.account
                                    ), info
                                )
                            }

                        }
                        .onError {
                            instanceRepository.updateInstanceInfoLocal(
                                GameSummary(
                                    gameResp.config.platform,
                                    gameResp.config.account
                                ), info
                            )
                        }

                    gameInfo.value = GameInfoState.Info(info)
                }.onFailed { b, s ->
                    instanceRepository.getInstanceInfoLocal(
                        GameSummary(
                            gameResp.config.platform,
                            gameResp.config.account
                        )
                    )
                        .onOK {
                            gameInfo.value = GameInfoState.Info(it)
                        }
                        .onError {
                            gameInfo.value = GameInfoState.Empty
                            s.moeSnackBar()
                        }
                }
        }

    }

    private fun loadLog() {
        val token = closureController.token.value.ifEmpty { return }
        viewModelScope.launch {
            gameAPI.getLog(
                token,
                gameResp.config.platform,
                gameResp.config.account,
                0
            )
                .awaitResponseOK()
                .onSuccessful { gameLogItems ->
                    var list = gameLogItems
                    list += logList
                    instanceRepository.getLogLocal(
                        GameSummary(
                            gameResp.config.platform,
                            gameResp.config.account
                        )
                    ).onOK {
                        list += it
                    }.onError {
                        it.errorMsg
                    }


                    val r = list.map {
                        it.copy(
                            it.ts.toInt().toDouble(),
                            it.info
                        )
                    }.toSet().toList().sortedByDescending { it.ts }
                    logList.clear()
                    logList += r
                    instanceRepository.updateLogLocal(
                        GameSummary(
                            gameResp.config.platform,
                            gameResp.config.account
                        ), logList
                    )
                }.onFailed { b, s ->
                    instanceRepository.getLogLocal(
                        GameSummary(
                            gameResp.config.platform,
                            gameResp.config.account
                        )
                    ).onOK {
                        logList.addAll(it)
                        val r = logList.toSet().toList().sortedByDescending { it.ts }
                        logList.clear()
                        logList += r
                    }.onError {
                        s.moeSnackBar()
                    }
                }
        }
    }

    data class ItemIcon(
        val iconUrl: String,
        val count: Long,
    )

    fun getItems(gameInfo: GameInfo, item: Map<String, ItemBean>): List<ItemIcon> {
        val items = arrayListOf<ItemIcon>()
        gameInfo.consumable?.forEach { (t, u) ->
            if (item.containsKey(t)) {
                var count = 0L
                u.forEach {
                    count += it.count
                }
                if (count > 0) {
                    items.add(
                        ItemIcon(
                            item[t]?.getIconUrl() ?: "",
                            count
                        )
                    )
                }

            }
        }
        gameInfo.inventory?.forEach { (t, u) ->
            if (item.containsKey(t)) {
                if (u > 0) {
                    items.add(
                        ItemIcon(
                            item[t]?.getIconUrl() ?: "",
                            u
                        )
                    )
                }
            }
        }
        return items
    }

    fun pushOcr() {
        if (ocrBtnEnable.value) {
            val token = closureController.token.value.ifEmpty { return }
            viewModelScope.launch {
                ocrBtnEnable.value = false
                gameAPI.ocr(token, gameResp.config.platform, gameResp.config.account)
                    .awaitResponseOK()
                    .onSuccessfulNullable {
                        ocrBtnEnable.value = true
                    }.onFailed { b, s ->
                        ocrBtnEnable.value = true
                        s.moeSnackBar()
                    }
            }
        } else {
            stringRes(R.string.ocr_cd).moeSnackBar()
        }


    }
}

class InstanceViewModelFactory(
    private val gameResp: GameResp
) : ViewModelProvider.Factory {

    companion object {
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressWarnings("unchecked")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstanceViewModel::class.java))
            return InstanceViewModel(gameResp) as T
        throw RuntimeException("unknown class :" + modelClass.name)
    }
}