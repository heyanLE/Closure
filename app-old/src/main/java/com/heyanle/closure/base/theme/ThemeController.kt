package com.heyanle.closure.base.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.heyanle.closure.base.preferences.android.AndroidPreferenceStore
import com.squareup.moshi.Moshi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/**
 * Created by HeYanLe on 2023/8/20 11:48.
 * https://github.com/heyanLE
 */

enum class DarkMode {
    Auto, Dark, Light
}

data class ThemeState(
    val themeMode: ClosureThemeMode,
    val darkMode: DarkMode,
    val isDynamicColor: Boolean,
) {

    companion object {
        val DEFAULT = ThemeState(ClosureThemeMode.Blue, DarkMode.Auto, true)
    }

    @Composable
    fun isDark(): Boolean {
        return when (darkMode) {
            DarkMode.Dark -> true
            DarkMode.Light -> false
            else -> isSystemInDarkTheme()
        }
    }
}

class ThemeController(
    private val preferenceStore: AndroidPreferenceStore,
    private val moshi: Moshi,
) {

    private val themeStateJson = moshi.adapter<ThemeState>(ThemeState::class.java)
    private val themeState = preferenceStore.getObject(
        "theme_mode", ThemeState(ClosureThemeMode.Blue, DarkMode.Auto, true),
        {
            themeStateJson.toJson(it)
        },
        {
            themeStateJson.fromJson(it) ?: ThemeState.DEFAULT
        }
    )
    private val scope = MainScope()
    val flow = themeState.flow().stateIn(scope, SharingStarted.Lazily, themeState.get())

    fun changeThemeMode(themeMode: ClosureThemeMode) {
        update {
            it.copy(themeMode = themeMode)
        }
    }

    fun changeIsDynamicColor(isDynamicColor: Boolean) {
        val real = (isDynamicColor && isSupportDynamicColor())
        update {
            it.copy(isDynamicColor = real)
        }
    }

    fun changeDarkMode(darkMode: DarkMode) {
        update {
            it.copy(darkMode = darkMode)
        }
    }

    private fun update(block: (ThemeState) -> ThemeState) {
        val old = themeState.get()
        val new = block(old)
        themeState.set(new)
    }

    // 暂时先不支持
    fun isSupportDynamicColor(): Boolean {
        return false //Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}