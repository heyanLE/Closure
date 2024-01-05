package com.heyanle.closure

import android.content.Context
import com.heyanle.closure.base.preferences.android.AndroidPreferenceStore
import com.heyanle.closure.base.preferences.hekv.HeKVPreferenceStore
import com.heyanle.closure.base.preferences.mmkv.MMKVPreferenceStore
import com.heyanle.injekt.api.get
import com.heyanle.injekt.core.Injekt
import com.heyanle.okkv2.core.okkv

/**
 * preferences 更新
 * Created by HeYanLe on 2023/7/29 17:38.
 * https://github.com/heyanLE
 */
object Migrate {

    fun tryUpdate(
        context: Context
    ) {
        preferenceUpdate(
            context,
            Injekt.get(),
            Injekt.get(),
            Injekt.get(),
        )
    }

    private fun preferenceUpdate(
        context: Context,
        androidPreferenceStore: AndroidPreferenceStore,
        mmkvPreferenceStore: MMKVPreferenceStore,
        heKVPreferenceStore: HeKVPreferenceStore,
    ) {

        val lastVersionCode = androidPreferenceStore.getInt("last_version_code", 0).get()
        val curVersionCode = BuildConfig.VERSION_CODE

        if (lastVersionCode < curVersionCode) {
            // 后续版本在这里加数据迁移

        }

        androidPreferenceStore.getInt("last_version_code", 0).set(curVersionCode)

    }


}