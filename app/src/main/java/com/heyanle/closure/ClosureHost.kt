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

    login: @Composable () -> Unit,
    contentBuilder: NavGraphBuilder.() -> Unit,
) {
    val state by closureController.state.collectAsState()
    val sta = state

    LaunchedEffect(key1 = Unit) {
        launch {
            snapshotFlow {
                state.isShowPage to state.username
            }.collectLatest {
                state.loge("ClosureHost")
                if (it.first || it.second.isEmpty()) {
                    navController.navigate(LOGIN) {
                        anim {
                            popEnter = -1
                            popExit = -1
                            enter = -1
                            exit = -1
                        }
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                } else {
                    navController.navigate(startDestination) {
                        anim {
                            popEnter = -1
                            popExit = -1
                            enter = -1
                            exit = -1
                        }
                        popUpTo(navController.graph.findStartDestination().id) {
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
            modifier = Modifier,
            contentAlignment = Alignment.Center,
            enterTransition = { slideInHorizontally(tween()) { it } },
            exitTransition = { slideOutHorizontally(tween()) { -it } + fadeOut(tween()) },
            popEnterTransition = { slideInHorizontally(tween()) { -it } },
            popExitTransition = { slideOutHorizontally(tween()) { it } },
        ) {
            composable(LOGIN) {
                NormalSystemBarColor()
                login()
            }
            contentBuilder()
        }
    }


}