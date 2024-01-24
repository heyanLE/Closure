package com.heyanle.closure.theme

import android.app.Application
import com.heyanle.injekt.api.InjektModule
import com.heyanle.injekt.api.InjektScope
import com.heyanle.injekt.api.addSingletonFactory
import com.heyanle.injekt.api.get

/**
 * Created by heyanlin on 2024/1/18 16:08.
 */

class ThemeModule (
    private val application: Application
): InjektModule {
    override fun InjektScope.registerInjectables() {
        addSingletonFactory {
            EasyThemeController(get())
        }
    }
}