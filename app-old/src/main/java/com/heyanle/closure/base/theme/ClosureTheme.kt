package com.heyanle.closure.base.theme

import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.heyanle.closure.compose.LocalNavController
import com.heyanle.injekt.core.Injekt

/**
 * Created by HeYanLe on 2023/8/20 11:57.
 * https://github.com/heyanLE
 */

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

val LocalThemeState = staticCompositionLocalOf<ThemeState> {
    error("AppNavController Not Provide")
}

@Composable
fun ClosureTheme(
    content: @Composable () -> Unit
) {

    val themeController: ThemeController by Injekt.injectLazy()
    val themeState by themeController.flow.collectAsState()
    val isDynamic = themeState.isDynamicColor && themeController.isSupportDynamicColor()
    val isDark = when (themeState.darkMode) {
        DarkMode.Dark -> true
        DarkMode.Light -> false
        else -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        isDynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

        }

        else -> {
            Log.d("theme", themeState.themeMode.name)
            val old = themeState.themeMode.getColorScheme(isDark)
            // 不想搞 md3 配色
            old.copy(
                primaryContainer = old.background,
                onPrimaryContainer = old.onBackground,
                secondaryContainer = old.surface,
                onSecondaryContainer = old.onSurface,
                tertiary = old.secondary,
                tertiaryContainer = old.secondary,
                onTertiary = old.onSecondary,
                onTertiaryContainer = old.onSecondary,
                surfaceVariant = old.surface,
                onSurfaceVariant = old.onSurface
            )
        }
    }
    val uiController = rememberSystemUiController()
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            uiController.setStatusBarColor(Color.Transparent, if (isDynamic) isDark else false)
            uiController.setNavigationBarColor(
                colorScheme.primary,
                if (isDynamic) isDark else false
            )
        }
    }
    CompositionLocalProvider(LocalThemeState provides themeState) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }


}