package com.heyanle.closure.setting

import android.app.Application
import com.heyanle.closure.preferences.PreferenceStore
import com.heyanle.closure.preferences.android.AndroidPreferenceStore
import com.heyanle.closure.preferences.mmkv.MMKVPreferenceStore
import org.koin.dsl.binds
import org.koin.dsl.module


/**
 * Created by heyanlin on 2023/10/30.
 */

val settingModule = module {
    single {
        AndroidPreferenceStore(get<Application>())
    }
    single {
        MMKVPreferenceStore(get<Application>())
    }
    single {
        SettingPreferences(get(), get<AndroidPreferenceStore>())
    }
}