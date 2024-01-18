package com.heyanle.closure

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heyanle.closure.closure.auth.AuthController
import com.heyanle.closure.theme.NormalSystemBarColor
import com.heyanle.closure.ui.login.Login
import com.heyanle.closure.utils.koin
import java.lang.ref.WeakReference

/**
 * Created by heyanlin on 2023/12/31.
 */
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("AppNavController Not Provide")
}
val LocalToken = staticCompositionLocalOf<String> {
    error("Token not provide")
}

var navControllerRef: WeakReference<NavHostController>? = null

const val HOME = "HOME"

// 缺省路由
const val DEFAULT = HOME

@Composable
fun Nav(){

    val authController: AuthController by koin.inject()
    val token = authController.token.collectAsState()
    if (token.value.isEmpty()) {
        // 登录态阻断
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            NormalSystemBarColor()
            Login()
        }

    } else {
        val nav = rememberNavController()
        LaunchedEffect(key1 = nav) {
            navControllerRef = WeakReference(nav)
        }
        CompositionLocalProvider(
            LocalNavController provides nav,
            LocalToken provides token.value
        ) {
            NavHost(nav, DEFAULT,
                modifier = Modifier.fillMaxSize(),
                enterTransition = { slideInHorizontally(tween()) { it } },
                exitTransition = { slideOutHorizontally(tween()) { -it } + fadeOut(tween()) },
                popEnterTransition = { slideInHorizontally(tween()) { -it } },
                popExitTransition = { slideOutHorizontally(tween()) { it } }
            ) {
                composable(HOME){

                }
            }
        }
    }

}