package com.heyanle.closure.preferences

import com.heyanle.closure.preferences.android.AndroidPreferenceStore
import com.heyanle.closure.preferences.mmkv.MMKVPreferenceStore
import org.koin.dsl.module

/**
 * Created by heyanlin on 2023/12/31.
 */
val preferenceModule = module {
    single {
        AndroidPreferenceStore(get())
    }
    single {
        MMKVPreferenceStore(get())
    }

}