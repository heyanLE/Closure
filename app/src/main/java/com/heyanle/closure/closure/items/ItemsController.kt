package com.heyanle.closure.closure.items

import android.content.Context
import com.heyanle.closure.base.preferences.android.AndroidPreferenceStore
import com.heyanle.closure.compose.common.moeSnackBar
import com.heyanle.closure.closure.stage.Stage
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
 * Created by HeYanLe on 2023/8/12 14:15.
 * https://github.com/heyanLE
 */
class ItemsController(
        private val context: Context,
        private val preferenceStore: AndroidPreferenceStore,
        private val net: Net,
) {
    companion object {
        // 理智图标
        const val AP_ICON_URL = "https://ak.dzp.me/dst/items/AP_GAMEPLAY.webp"

        // 源石图标
        const val DIAMOND_ICON_URL = "https://ak.dzp.me/dst/items/DIAMOND.webp"

        // 合成玉图标
        const val DIAMOND_SHD_ICON_URL = "https://ak.dzp.me/dst/items/DIAMOND_SHD.webp"

        // 龙门币图标
        const val GOLD_ICON_URL = "https://ak.dzp.me/dst/items/GOLD.webp"


        private const val URL = "https://arknights.host/data/Items.json"
    }

    private val scope = MainScope()


    private val updateTimePre = preferenceStore.getLong("items_update_time", 0L)
    val updateTIme =
            updateTimePre.flow().distinctUntilChanged().stateIn(scope, SharingStarted.Lazily, 0)

    private val _map = MutableStateFlow<Map<String, ItemBean>>(emptyMap())
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
                val t = context.assets?.open("Items.json")?.bufferedReader()?.readText()
                val map = ItemBean.parsonFromResp(t ?: "{}")
                if (map.isNotEmpty() && map.values.isEmpty()) {
                    _map.update { map }
                }
            }
        }
        if (retryCount > 0) {
            net.okHttpClient.get(URL).onSuccessful {
                retryCount = 3
                val map = ItemBean.parsonFromResp(it)
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