package com.heyanle.closure

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.closure.ClosurePresenter
import com.heyanle.closure.theme.NormalSystemBarColor
import com.heyanle.closure.ui.login.Login
import com.heyanle.injekt.core.Injekt
import java.lang.ref.WeakReference

/**
 * Created by heyanlin on 2023/12/31.
 */
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("AppNavController Not Provide")
}


var navControllerRef: WeakReference<NavHostController>? = null

const val HOME = "HOME"

// 缺省路由
const val DEFAULT = HOME

fun NavGraphBuilder.nav(){
    composable(HOME){

    }
}

@Composable
fun Host(){
    val nav = rememberNavController()
    val closureController: ClosureController by Injekt.injectLazy()
    CompositionLocalProvider (
        LocalNavController provides nav
    ) {
        DisposableEffect(key1 = nav){
            navControllerRef = WeakReference(nav)
            onDispose {
                navControllerRef = null
            }
        }
        ClosureHost(
            closureController = closureController,
            navController = nav,
            startDestination = DEFAULT,
            login = {
                Login(closureController)
            },
            contentBuilder = {
                nav()
            }
        )
    }
}