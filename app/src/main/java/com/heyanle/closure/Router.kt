package com.heyanle.closure

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.stringResource
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.heyanle.closure.page.MainController
import com.heyanle.closure.page.game_instance.Instance
import com.heyanle.closure.page.login.Login
import com.heyanle.closure.page.home.Home
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage

/**
 * Created by HeYanLe on 2022/12/23 16:46.
 * https://github.com/heyanLE
 */
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("AppNavController Not Provide")
}


const val LOGIN = "login"
const val HOME = "home"
const val INSTANCE = "instance"

// 缺省路由
const val DEFAULT = HOME


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Nav() {
    val nav = rememberAnimatedNavController()
    CompositionLocalProvider(LocalNavController provides nav) {
        AnimatedNavHost(nav, DEFAULT,
            enterTransition = { slideInHorizontally(tween()) { it } },
            exitTransition = { slideOutHorizontally(tween()) { -it } + fadeOut(tween()) },
            popEnterTransition = { slideInHorizontally(tween()) { -it } },
            popExitTransition = { slideOutHorizontally(tween()) { it } })
        {
            // 登录状态阻断
            composableWithTokenCheck(HOME){
                Home()
            }

            composableWithTokenCheck(INSTANCE) {
                Instance()
            }

            composable(
                LOGIN,
            ) { entry ->
                Login {
                    MainController.token.value = it.token
                    nav.popBackStack()
                }
            }
        }
    }
}

@ExperimentalAnimationApi
public fun NavGraphBuilder.composableWithTokenCheck(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (
    AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
    )? = enterTransition,
    popExitTransition: (
    AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
    )? = exitTransition,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
){

    composable(route, arguments, deepLinks, enterTransition, exitTransition, popEnterTransition, popExitTransition){
        val token by MainController.token.observeAsState("")
        val navController = LocalNavController.current
        var isError by remember {
            mutableStateOf(false)
        }

        if(token.isNotEmpty()){
            content.invoke(this, it)
//            val user by MainController.user.observeAsState()
//            if(user == null && !isError){
//                LoadingPage()
//                LaunchedEffect(Unit){
//                    // 尝试用 token 登录
//                    MainController.loginWithToken {
//                        isError = true
//                    }
//                }
//            }else if(user == null){
//                // Token 自动登录失败
//                ErrorPage(
//                    errorMsg = stringResource(id = R.string.click_to_login_again),
//                    clickEnable = true,
//                    onClick = {
//                        isError = false
//                    },
//                    other = {
//                        Button(onClick = {
//                            navController.navigate(LOGIN)
//                        }) {
//                            Text(text = stringResource(id = R.string.click_to_reset_email_pass))
//                        }
//                    }
//                )
//            } else{
//                AnimatedVisibility(true) {
//                    content.invoke(this, it)
//                }
//            }
        } else {
            // 没有储存 Token
            ErrorPage(
                modifier = Modifier.background(ColorScheme.background),
                errorMsg = stringResource(id = R.string.click_to_login),
                clickEnable = true,
                onClick = {
                    navController.navigate(LOGIN)
                }
            ) 

        }
    }

}
