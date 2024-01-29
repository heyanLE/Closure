package com.heyanle.closure

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.theme.NormalSystemBarColor
import com.heyanle.closure.utils.loge
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by heyanlin on 2024/1/22 14:32.
 */

const val LOGIN = "LOGIN"

val LocalClosureStatePresenter = compositionLocalOf<ClosureController.ClosureState> {
    error("closure state not provide")
}

@Composable
fun ClosureHost(
    navController: NavHostController,
    closureController: ClosureController,
    startDestination: String,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = { slideInHorizontally(tween()) { it } },
    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = { slideOutHorizontally(tween()) { -it } + fadeOut(tween()) },
    popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = { slideInHorizontally(tween()) { -it } },
    popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = { slideOutHorizontally(tween()) { it } },
    login: @Composable ()->Unit,
    contentBuilder: NavGraphBuilder.() -> Unit,
){
    val state by closureController.state.collectAsState()
    val sta = state

    LaunchedEffect(key1 = Unit){
        launch {
            snapshotFlow {
                state.isShowPage to state.username
            }.collectLatest {
                state.loge("ClosureHost")
                if(it.first || state.username.isEmpty()){
                    navController.navigate(LOGIN){
                        popUpTo(navController.graph.findStartDestination().id){
                            inclusive = true
                        }
                    }
                }else{

                    navController.navigate(startDestination){
                        popUpTo(navController.graph.findStartDestination().id){
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalClosureStatePresenter provides sta
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier,
            contentAlignment = contentAlignment,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
        ){
            composable(LOGIN){
                NormalSystemBarColor()
                login()
            }
            contentBuilder()
        }
    }






}