package com.heyanle.closure

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.closure.ClosurePresenter
import com.heyanle.closure.utils.koin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by heyanlin on 2024/1/22 14:32.
 */

const val LOGIN = "LOGIN"

@Composable
fun ClosureHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = { slideInHorizontally(tween()) { it } },
    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = { slideOutHorizontally(tween()) { -it } + fadeOut(tween()) },
    popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = { slideInHorizontally(tween()) { -it } },
    popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = { slideOutHorizontally(tween()) { it } },
    login: @Composable (ClosureController)->Unit,
    contentBuilder: NavGraphBuilder.(ClosureController, ClosureController.ClosureState) -> Unit,
){
    val closureController: ClosureController by koin.inject()
    val state by closureController.state.collectAsState()
    val sta = state

    LaunchedEffect(key1 = Unit){
        launch {
            snapshotFlow {
                if(sta.isShowPage || sta.username.isEmpty()){
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
            }.collect()
        }
    }

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
            login(closureController)
        }

        contentBuilder(closureController, sta)
    }


}