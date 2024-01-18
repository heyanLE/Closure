package com.heyanle.closure.theme

import org.koin.dsl.module

/**
 * Created by heyanlin on 2024/1/18 16:08.
 */
val themeModule = module {
    single {
        EasyThemeController(get())
    }
}