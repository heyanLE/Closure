package com.heyanle.closure.closure.stage

import android.content.Context
import com.heyanle.closure.base.preferences.android.AndroidPreferenceStore
import com.heyanle.closure.compose.common.moeSnackBar
import com.heyanle.closure.net.Net
import com.heyanle.closure.utils.get
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by HeYanLe on 2023/8/12 12:25.
 * https://github.com/heyanLE
 */
class StageController(
    private val context: Context,
    private val preferenceStore: AndroidPreferenceStore,
    private val net: Net,
) {

    companion object {
        private const val URL = "https://arknights.host/data/Stage.json"
    }

    private val scope = MainScope()


    private val updateTimePre = preferenceStore.getLong("stage_update_time", 0L)
    val updateTIme =
        updateTimePre.flow().distinctUntilChanged().stateIn(scope, SharingStarted.Lazily, 0)

    private val _map = MutableStateFlow<Map<String, Stage>>(emptyMap())
    val map = _map.asStateFlow()

    init {
        scope.launch {
            retryCount = 3
            innerLoad()
        }
    }

    @Volatile
    private var retryCount = 3
    private suspend fun innerLoad() {
        if (map.value.isEmpty()) {
            withContext(Dispatchers.IO) {
                // 先加载本地的先用着
                val t = context.assets?.open("Stage.json")?.bufferedReader()?.readText()
                val map = Stage.parsonFromResp(t ?: "{}")
                if (map.isNotEmpty() && map.values.isEmpty()) {
                    _map.update { map }
                }
            }
        }
        if (retryCount > 0) {
            net.okHttpClient.get(URL).onSuccessful {
                retryCount = 3
                val map = Stage.parsonFromResp(it)
                if (map.isNotEmpty()) {
                    _map.update { map }
                } else {
                    retryCount--
                    innerLoad()
                }
            }.onFailed { _, s ->
                s.moeSnackBar()
                retryCount--
                innerLoad()
            }
        }
    }

}