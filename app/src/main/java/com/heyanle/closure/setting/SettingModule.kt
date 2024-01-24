package com.heyanle.closure.setting

import android.app.Application
import com.heyanle.closure.preferences.PreferenceStore
import com.heyanle.closure.preferences.android.AndroidPreferenceStore
import com.heyanle.closure.preferences.mmkv.MMKVPreferenceStore
import com.heyanle.injekt.api.InjektModule
import com.heyanle.injekt.api.InjektScope
import com.heyanle.injekt.api.addSingletonFactory
import com.heyanle.injekt.api.get
import org.koin.dsl.binds
import org.koin.dsl.module


/**
 * Created by heyanlin on 2023/10/30.
 */
class SettingModule (
    private val application: Application,
): InjektModule {
    override fun InjektScope.registerInjectables() {
        addSingletonFactory {
            AndroidPreferenceStore(application)
        }

        addSingletonFactory {
            MMKVPreferenceStore(application)
        }

        addSingletonFactory {
            SettingPreferences(get(), get<AndroidPreferenceStore>())
        }
    }
}