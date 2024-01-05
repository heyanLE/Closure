package com.heyanle.closure.compose.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.LocalAct
import com.heyanle.closure.R
import com.heyanle.closure.base.theme.LocalThemeState
import com.heyanle.closure.compose.ABOUT
import com.heyanle.closure.compose.LocalNavController
import com.heyanle.closure.compose.SETTING
import com.heyanle.closure.compose.common.ErrorPage
import com.heyanle.closure.compose.common.LoadingPage
import com.heyanle.closure.compose.common.OKImage
import com.heyanle.closure.compose.home.add_game.AddInstanceDialog
import com.heyanle.closure.compose.home.bind.BindQQDialog
import com.heyanle.closure.compose.home.game_config.AutoSettingDialog
import com.heyanle.closure.compose.home.instance.Instance
import com.heyanle.closure.compose.home.instance.InstanceViewModel
import com.heyanle.closure.compose.home.instance.InstanceViewModelFactory
import com.heyanle.closure.compose.home.manager.Manager
import com.heyanle.closure.compose.home.screenshot.ScreenshotDialog
import com.heyanle.closure.utils.stringRes
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2023/8/20 15:11.
 * https://github.com/heyanLE
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home() {
    val scope = rememberCoroutineScope()
    val vm = viewModel<HomeViewModel>()
    val pagerState = rememberPagerState {
        vm.gameList.size + 1
    }

    val configDialog = vm.configDialog.value
    configDialog?.let { resp ->
        AutoSettingDialog(enable = true, gameResp = resp,
            onDismissRequest = {
                vm.configDialog.value = null
            }, onSave = {
                vm.configDialog.value = null
                vm.onPostConfig(resp, it)
            })

    }

    AddInstanceDialog(
        enable = vm.addDialog.value,
        onDismissRequest = { vm.addDialog.value = false },
        onAdd = {
            vm.onAddInstance(it)
            vm.addDialog.value = false
        })

    val deleteDialog = vm.deleteDialog.value
    deleteDialog?.let { resp ->
        DeleteDialog(show = true, onDismissRequest = {
            vm.deleteDialog.value = null
        }) {
            scope.launch {
                vm.deleteDialog.value = null
                vm.onDelete(resp)
            }

        }
    }

    val screenDialog = vm.screenshotDialog.value
    screenDialog?.let { resp ->
        ScreenshotDialog(
            enable = true,
            onDismissRequest = { vm.screenshotDialog.value = null },
            select = resp
        )

    }

    val bindDialog = vm.bindQQDialog.value
    bindDialog?.let { resp ->
        BindQQDialog(enable = true) {
            vm.bindQQDialog.value = null
        }

    }


    val nav = LocalNavController.current


    Column {
        HomeTopAppBar(
            image = vm.avatarImg.value,
            title = vm.topBarTitle.value,
            onRefresh = {
                if (pagerState.currentPage == 0) {
                    vm.onRefreshList()
                } else {
                    vm.onRefresh(vm.gameList[pagerState.currentPage - 1])
                }
            },
            onSetting = {
                nav.navigate(SETTING)
            },
            onExit = {
                vm.onClearToken()
            },
            onAbout = {
                nav.navigate(ABOUT)
            }
        )

        HomeTab(
            count = pagerState.pageCount,
            selectionIndex = pagerState.currentPage,
            onSelectionChanged = {
                scope.launch {
                    pagerState.animateScrollToPage(it)
                }
            }) {
            if (it == 0) {
                Text(text = stringResource(id = R.string.instance_manager))
            } else {
                Text(text = "${stringResource(id = R.string.instance)}${it}")
            }

        }
        Box(modifier = Modifier.weight(1f)) {
            val act = LocalAct.current
            HorizontalPager(state = pagerState) {
                if (it == 0) {
                    LaunchedEffect(key1 = pagerState.currentPage) {
                        if (pagerState.currentPage == 0) {
                            vm.avatarImg.value = R.drawable.logo
                            vm.topBarTitle.value = stringRes(R.string.app_name)
                        }
                    }
                    if (vm.isLoading.value) {
                        LoadingPage(
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (vm.loadingErrorMsg.value.isNotEmpty()) {
                        ErrorPage(
                            modifier = Modifier.fillMaxSize(),
                            errorMsg = vm.loadingErrorMsg.value,
                            clickEnable = true,
                            other = {
                                Text(text = stringResource(id = R.string.click_to_retry))
                            },
                            onClick = {
                                vm.onRefreshList()
                            }
                        )
                    } else {
                        Manager(
                            vm.gameList.toList(),
                            onClick = {
                                val index = vm.gameList.indexOf(it)
                                if (index >= 0) {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index+1)
                                    }

                                }
                            },
                            onOpen = {
                                vm.onOpen(it)
                            },
                            onPause = {
                                vm.onPause(it)
                            },
                            onDelete = {
                                vm.deleteDialog.value = it
                            },
                            onCaptcha = {
                                vm.onCaptcha(act, it)
                            },
                            onConfig = {
                                vm.configDialog.value = it
                            },
                            onAddInstance = {
                                vm.addDialog.value = true
                            }
                        )
                    }


                } else {
                    val gameResp = vm.gameList[it - 1]
                    CompositionLocalProvider(
                        LocalViewModelStoreOwner provides vm.getViewModelOwner(gameResp)
                    ) {
                        val instanceViewModel =
                            viewModel<InstanceViewModel>(factory = InstanceViewModelFactory(gameResp))
                        LaunchedEffect(key1 = gameResp) {
                            vm.newRefreshListener(gameResp) {
                                instanceViewModel.refresh()
                            }
                        }
                        Instance(
                            pagerState.currentPage == it,
                            gameResp = gameResp,
                            instanceViewModel = instanceViewModel,
                            vm,
                            onConfig = {
                                vm.configDialog.value = it
                            },
                            onScreenshot = {
                                vm.screenshotDialog.value = it
                            },
                            onBind = {
                                vm.bindQQDialog.value = it
                            }
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun HomeTab(
    count: Int,
    selectionIndex: Int,
    onSelectionChanged: (Int) -> Unit,
    label: @Composable (Int) -> Unit,
) {
    val isUseSecondary = LocalThemeState.current.isDark()
    ScrollableTabRow(
        selectedTabIndex = selectionIndex,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        edgePadding = 0.dp,
        divider = {},
        indicator = { tabPositions ->
            if (tabPositions.isNotEmpty() && selectionIndex >= 0 && selectionIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectionIndex])
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)),
                    color = if (isUseSecondary) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) {
        repeat(count) {
            Tab(
                selected = it == selectionIndex,
                onClick = {
                    onSelectionChanged(it)
                },
                text = {
                    label(it)
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    image: Any,
    title: String,
    onRefresh: () -> Unit,
    onSetting: () -> Unit,
    onExit: () -> Unit,
    onAbout: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Text(text = title)
        },
        navigationIcon = {
            Row() {
                Spacer(modifier = Modifier.size(8.dp))
                OKImage(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape), image = image, contentDescription = title
                )
            }

        },
        actions = {
            IconButton(
                onClick = {
                    onRefresh()
                })
            {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = stringResource(id = R.string.refresh)
                )
            }
            IconButton(
                onClick = {
                    showMenu = !showMenu
                }
            ) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = stringResource(id = R.string.more)
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(id = R.string.appearance_setting))
                    },
                    onClick = {
                        showMenu = false
                        onSetting()
                    },
                )
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(id = R.string.exit_account))
                    },
                    onClick = {
                        showMenu = false
                        onExit()

                    },
                )
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(id = R.string.about))
                    },
                    onClick = {
                        showMenu = false
                        onAbout()

                    },
                )

            }
        },

        )
}

@Composable
fun DeleteDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (show) {
        AlertDialog(
            modifier = Modifier.width(IntrinsicSize.Min),
            icon = {
                Image(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.error),
                    contentDescription = stringResource(id = R.string.register)
                )
            },

            text = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.ask_delete_instance)
                )
            },
            onDismissRequest = { onDismissRequest() },
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError

                    ),
                    onClick = {
                        onConfirm()
                    }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    onClick = {
                        onDismissRequest()
                    }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}