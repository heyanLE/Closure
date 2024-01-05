package com.heyanle.closure.compose

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heyanle.closure.R
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.compose.about.About
import com.heyanle.closure.compose.common.ErrorPage
import com.heyanle.closure.compose.common.LoadingPage
import com.heyanle.closure.compose.common.ProgressDialog
import com.heyanle.closure.compose.home.Home
import com.heyanle.closure.compose.login.Login
import com.heyanle.closure.compose.setting.Setting
import com.heyanle.closure.net.Net
import com.heyanle.injekt.core.Injekt

/**
 * Created by HeYanLe on 2023/8/20 14:13.
 * https://github.com/heyanLE
 */

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("AppNavController Not Provide")
}

const val HOME = "HOME"
const val LOGIN = "LOGIN"
const val WAREHOUSE = "warehouse"
const val BIND = "bind"
const val SETTING = "SETTING"
const val ABOUT = "ABOUT"

// 缺省路由
const val DEFAULT = HOME

@Composable
fun Nav() {
    val controller: ClosureController by Injekt.injectLazy()
    val anno = controller.announcement.collectAsState()
    val token = controller.token.collectAsState()

    if (anno.value.loading) {
        ProgressDialog()
    }
    if (!anno.value.loading && (anno.value.anno?.allowGameLogin != true || anno.value.anno?.allowLogin != true)) {
        ErrorPage(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            errorMsg = stringResource(id = R.string.not_allow_use),
            clickEnable = true,
            onClick = {
                controller.updateAnnouncement()
            },
            other = {
                Text(text = anno.value.anno?.announcement ?: anno.value.errorMsg)
            }
        )
    } else if (token.value.isEmpty()) {
        Column {
            Box(modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(MaterialTheme.colorScheme.primary))
            Box(modifier = Modifier.weight(1f)){
                Login()
            }
        }

    } else {
        val nav = rememberNavController()
        CompositionLocalProvider(LocalNavController provides nav) {
            NavHost(nav, DEFAULT,
                enterTransition = { slideInHorizontally(tween()) { it } },
                exitTransition = { slideOutHorizontally(tween()) { -it } + fadeOut(tween()) },
                popEnterTransition = { slideInHorizontally(tween()) { -it } },
                popExitTransition = { slideOutHorizontally(tween()) { it } })
            {
                composable(HOME){
                    Home()
                }
                composable(SETTING){
                    Setting()
                }
                composable(ABOUT){
                    About()
                }
            }
        }

    }

}
