package com.heyanle.closure.preferences

import android.app.Application
import com.heyanle.closure.preferences.android.AndroidPreferenceStore
import com.heyanle.closure.preferences.mmkv.MMKVPreferenceStore
import com.heyanle.injekt.api.InjektModule
import com.heyanle.injekt.api.InjektScope
import com.heyanle.injekt.api.addSingletonFactory
import com.heyanle.injekt.api.get
import org.koin.dsl.module

/**
 * Created by heyanlin on 2023/12/31.
 */ class PreferenceModule(
    private val application: Application
) : InjektModule {
    override fun InjektScope.registerInjectables() {
        addSingletonFactory {
            AndroidPreferenceStore(get())
        }

        addSingletonFactory {
            MMKVPreferenceStore(get())
        }
    }
}