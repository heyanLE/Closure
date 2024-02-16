package com.heyanle.closure.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.LocalClosureStatePresenter
import com.heyanle.closure.LocalGeetestHelper
import com.heyanle.closure.R
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.ui.common.ErrorPage
import com.heyanle.closure.ui.common.LoadingPage
import com.heyanle.closure.ui.common.OkImage
import com.heyanle.closure.ui.common.TabPage
import com.heyanle.closure.ui.home.instance.Instance
import com.heyanle.closure.ui.home.instance.InstanceViewModel
import com.heyanle.closure.ui.home.instance_manage.InstanceManager
import com.heyanle.closure.utils.easyTODO
import com.heyanle.closure.utils.logi
import com.heyanle.closure.utils.stringRes
import kotlinx.coroutines.launch

/**
 * Created by heyanlin on 2024/1/18 16:04.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {

    val geetest = LocalGeetestHelper.current
    val vm =
        viewModel<HomeViewModel>(
            factory = HomeViewModelFactory(
                LocalClosureStatePresenter.current.username,
                geetest
            )
        )
    val webGameList = vm.webGameList.collectAsState()
    val webG = webGameList.value

    val scope = rememberCoroutineScope()
    Surface(
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            TopAppBar(
                navigationIcon = {
                    Row {
                        Spacer(modifier = Modifier.size(8.dp))
                        OkImage(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            image = R.drawable.logo,
                            contentDescription = stringResource(id = com.heyanle.i18n.R.string.app_name)
                        )
                    }

                },
                title = {
                    Text(text = vm.title.value)
                },
                actions = {
                    when (val sta = vm.topAppBarState.value) {
                        HomeViewModel.TopAppBarState.Normal -> {}
                        HomeViewModel.TopAppBarState.InstanceManager -> {
                            IconButton(onClick = {
                                easyTODO()
                            }) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = stringRes(com.heyanle.i18n.R.string.add_instance)
                                )
                            }
                        }

                        is HomeViewModel.TopAppBarState.Instance -> {
                            IconButton(onClick = {
                                vm.refresh(sta.webGame)
                            }) {
                                Icon(
                                    Icons.Filled.Refresh,
                                    contentDescription = stringRes(com.heyanle.i18n.R.string.refresh)
                                )
                            }
                        }
                    }
                }
            )

            if (webG.data == null && webG.isLoading) {
                LaunchedEffect(key1 = Unit) {
                    vm.topAppBarState.value = HomeViewModel.TopAppBarState.Normal
                }
                LoadingPage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            } else if (webG.data == null) {
                LaunchedEffect(key1 = Unit) {
                    vm.topAppBarState.value = HomeViewModel.TopAppBarState.Normal
                }
                ErrorPage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    errorMsg = webG.errorMsg ?: "",
                    clickEnable = true,
                    other = {
                        Text(text = stringResource(id = com.heyanle.i18n.R.string.click_to_retry))
                    },
                    onClick = {
                        //vm.refreshWebGameList()
                    }
                )
            } else {
                HomeTabContent(
                    vm = vm,
                    username = LocalClosureStatePresenter.current.username,
                    webGameList = webG.data
                )
            }

        }
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.HomeTabContent(
    vm: HomeViewModel,
    username: String,
    webGameList: List<WebGame>
) {

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState {
        webGameList.size + 1
    }
    LaunchedEffect(key1 = pagerState.currentPage) {
        if (pagerState.currentPage == 0) {
            vm.topAppBarState.value = HomeViewModel.TopAppBarState.InstanceManager
        } else {
            vm.topAppBarState.value = runCatching {
                HomeViewModel.TopAppBarState.Instance(webGameList[pagerState.currentPage - 1])
            }.getOrElse {
                HomeViewModel.TopAppBarState.Normal
            }
        }
    }
    TabPage(
        Modifier.weight(1f),
        tabSize = webGameList.size + 1,
        pagerState = pagerState,
        onTabSelect = {
            scope.launch {
//                it.logi("Home")
//                pagerState.animateScrollToPage(it)
            }
        },
        tabs = { it, _ ->
            if (it == 0) {
                Text(text = stringResource(id = com.heyanle.i18n.R.string.instance_manager))
            } else {
                webGameList.getOrNull(it - 1)?.let {
                    it.status.nickName.logi("HomeTabContent")
                    Text(text = it.status.nickName.ifEmpty { stringRes(com.heyanle.i18n.R.string.unknown_doctor) })
                }
            }
        }) {
        if (it == 0) {
            InstanceManager(homeViewModel = vm)
        } else {
            val account = webGameList[it - 1].gameSetting.account
            val owner = vm.getViewModelOwner(account)
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides owner
            ) {
                Instance(homeViewModel = vm, username = username, account = account)
            }
        }


    }
}