package com.heyanle.closure.closure.logs

import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.closure.game.GameRepository
import com.heyanle.closure.closure.game.model.LogItem
import com.heyanle.closure.ui.common.moeSnackBar
import com.heyanle.closure.utils.CoroutineProvider
import com.heyanle.closure.utils.hekv.HeKV
import com.heyanle.closure.utils.jsonTo
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toJson
import com.heyanle.i18n.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

/**
 * Created by heyanle on 2024/1/27.
 * https://github.com/heyanLE
 */
class ClosureLogsPresenter(
    private val username: String,
    private val account: String,
    private val closureController: ClosureController,
    private val rootFolderPath: String,
    private val closureLogsRepository: ClosureLogsRepository,
) {

    private val hekv = HeKV("${rootFolderPath}/${username}", account)
    private val scope = CoroutineScope(SupervisorJob() + CoroutineProvider.SINGLE)

    data class LogState(
        val isLoading: Boolean = true,
        val logList: List<LogItem> = emptyList(),
    )


    private val _logFlow = MutableStateFlow<LogState>(LogState())
    val logFlow = _logFlow.asStateFlow()

    init {
        scope.launch {
            val logList = arrayListOf<LogItem>()
            hekv.keys().forEach {
                val json = hekv.get(it, "")
                if (json.isNotEmpty()) {
                    val logItem = json.jsonTo<LogItem>()
                    if (logItem != null) {
                        logList.add(logItem)
                    }
                }
            }
            append(logList)
        }
    }

    fun refresh() {
        scope.launch {
            val token = closureController.tokenIfNull(username) ?: return@launch
            _logFlow.update {
                it.copy(isLoading = true)
            }
            val cur = _logFlow.value.logList.firstOrNull()?.ts ?: 0
            val logList = closureLogsRepository.awaitGetLog(account, token, cur)
            append(logList)
        }
    }

    private fun append(logList: List<LogItem>) {
        _logFlow.update {
            it.copy(
                isLoading = false,
                logList = (it.logList + logList).distinctBy { it.id }.sortedByDescending { it.ts }
            )
        }
        scope.launch {
            logList.forEachIndexed { index, logItem ->
                hekv.put(logItem.toJson(), logItem.toJson())
            }
        }
    }


}