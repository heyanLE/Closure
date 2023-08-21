package com.heyanle.closure

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.heyanle.closure.net.Net
import com.heyanle.closure.page.MainController
import com.heyanle.closure.page.bind.BindQQ
import com.heyanle.closure.page.game_instance.Instance
import com.heyanle.closure.page.login.Login
import com.heyanle.closure.page.home.Home
import com.heyanle.closure.page.warehouse.Warehouse
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage
import com.heyanle.closure.ui.LoadingPage
import kotlinx.coroutines.launch

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
const val WAREHOUSE = "warehouse"
const val BIND = "bind"

// 缺省路由
const val DEFAULT = HOME


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Nav() {


    val scope = rememberCoroutineScope()
    AnimatedContent(targetState = Net.baseUrl.value) {


        when(it){
            Net.BaseUrlState.None -> {
                LaunchedEffect(key1 = it){
                    Net.getBaseUrl()
                }
            }
            Net.BaseUrlState.Loading -> {
                LoadingPage(modifier = Modifier
                    .fillMaxSize()
                    .background(ColorScheme.background)) {
                    Text(text = "正在获取域名！")
                }
            }
            Net.BaseUrlState.Error -> {
                // 没有储存 Token
                ErrorPage(
                    modifier = Modifier.background(ColorScheme.background),
                    errorMsg = "获取域名失败",
                    clickEnable = true,
                    onClick = {
                        Net.getBaseUrl()
                    },
                    other = {
                        Text(text = stringResource(id = R.string.click_to_retry))
                    }
                )
            }
            is Net.BaseUrlState.Url -> {


                AnimatedContent(targetState = Net.announcement.value) {
                    when(it){
                        is Net.AnnouncementState.None -> {
                            LaunchedEffect(key1 = Unit){
                                Net.getAnon()
                            }
                        }
                        is Net.AnnouncementState.Loading -> {
                            LoadingPage(modifier = Modifier
                                .fillMaxSize()
                                .background(ColorScheme.background)) {
                                Text(text = "正在获取公告！")
                            }
                        }
                        is Net.AnnouncementState.Announcement -> {
                            if(it.announcement.allowGameLogin && it.announcement.allowLogin){
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

                                        composableWithTokenCheck(WAREHOUSE){
                                            Warehouse()
                                        }

                                        composable(
                                            LOGIN,
                                        ) { entry ->
                                            Login {
                                                MainController.current.postValue(MainController.InstanceSelect("", -1L))
                                                MainController.token.value = it.token
                                                nav.popBackStack()
                                            }
                                        }

                                        composableWithTokenCheck(BIND){
                                            BindQQ()
                                        }
                                    }
                                }
                            }else{
                                ErrorPage(
                                    modifier = Modifier.background(ColorScheme.background),
                                    errorMsg = "可露希尔正在维护中",
                                    clickEnable = true,
                                    onClick = {
                                        Net.getAnon()

                                    },
                                    other = {
                                        Text(text = it.announcement.announcement)
                                    }
                                )
                            }
                        }
                    }
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
        if(token.isNotEmpty()){
            content.invoke(this, it)
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
