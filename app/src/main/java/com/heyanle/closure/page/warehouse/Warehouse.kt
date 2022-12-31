package com.heyanle.closure.page.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.INSTANCE
import com.heyanle.closure.LocalNavController
import com.heyanle.closure.R
import com.heyanle.closure.model.ItemModel
import com.heyanle.closure.page.MainController
import com.heyanle.closure.page.game_instance.InstanceTopAppBar
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage
import com.heyanle.closure.ui.FastScrollToTopFab
import com.heyanle.closure.ui.LoadingPage
import com.heyanle.closure.ui.OKImage
import com.heyanle.closure.utils.timeToString
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2022/12/31 22:57.
 * https://github.com/heyanLE
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Warehouse() {

    val scope = rememberCoroutineScope()
    val vm = viewModel<WarehouseViewModel>()
    val nav = LocalNavController.current

    val lazyGridState = rememberLazyGridState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            WarehouseTopAppbar {
                scope.launch {
                    vm.loadGetGameResp()
                }
            }
        },
        floatingActionButton = {
            FastScrollToTopFab(listState = lazyGridState)
        }
    ) {padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(ColorScheme.background)
        ) {

            val getGameRep by MainController.currentGetGame.observeAsState(MainController.StatusData.None())
            val item by ItemModel.mapLiveData.observeAsState(emptyMap())
            getGameRep.onLoading {
                LoadingPage(modifier = Modifier.fillMaxSize(),)
            }.onError {
                ErrorPage(
                    modifier = Modifier.fillMaxSize(),
                    image = R.drawable.empty,
                    errorMsg = stringResource(id = R.string.instance_no_login),
                    other = {
                        Text(text = stringResource(id = R.string.click_to_manager_instance))
                    },
                    clickEnable = true,
                    onClick = {
                        // 路由到 INSTANCE
                        nav.navigate(INSTANCE)
                    }
                )
            }.onData {
                val isItemLoading by ItemModel.isLoading.observeAsState(initial = false)
                if(isItemLoading){
                    LoadingPage()
                }else{
                    // 物品信息加载失败
                    if(item.isEmpty()){
                        ErrorPage(
                            modifier = Modifier.fillMaxSize(),
                            image = R.drawable.empty,
                            errorMsg = stringResource(id = R.string.item_load_err),
                            other = {
                                Text(text = stringResource(id = R.string.click_to_retry))
                            },
                            clickEnable = true,
                            onClick = {
                                // 重新加载
                                ItemModel.refresh()
                            }
                        )

                    }else{
                        val updateButton = @Composable {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp, 0.dp),
                                onClick = {
                                    scope.launch {
                                        vm.pushOcr()
                                    }
                                },
                                enabled = vm.ocrBtnEnable.value
                            ) {
                                Text(text = stringResource(id = R.string.click_to_ocr))
                            }
                        }
                        Column() {
                            val items = vm.getItems(it.data, item)
                            if(items.isEmpty()){
                                updateButton()
                                ErrorPage(
                                    modifier = Modifier.fillMaxSize(),
                                    errorMsg = stringResource(id = R.string.please_refresh_warehouse),
                                    clickEnable = false,
                                )
                            }else {
                                Box(modifier = Modifier.padding(8.dp, 0.dp)){
                                    Column() {

                                        LazyVerticalGrid(columns = GridCells.Adaptive(50.dp), state = lazyGridState){
                                            item(span = {
                                                // LazyGridItemSpanScope:
                                                // maxLineSpan
                                                GridItemSpan(maxLineSpan)
                                            }) {
                                                updateButton()
                                            }
                                            item(span = {
                                                // LazyGridItemSpanScope:
                                                // maxLineSpan
                                                GridItemSpan(maxLineSpan)
                                            }) {
                                                Text(
                                                    modifier = Modifier.fillMaxWidth().padding(0.dp, 4.dp, 0.dp, 12.dp),
                                                    textAlign = TextAlign.Center,
                                                    text = "${stringResource(id = R.string.last_update_time)} ${(it.data.lastFreshTs*1000L).timeToString()}")
                                            }
                                            items(items){
                                                Column (
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ){
                                                    OKImage(modifier = Modifier
                                                        .size(48.dp)
                                                        .padding(1.dp, 0.dp), image = it.iconUrl, contentDescription = it.iconUrl)
                                                    Text(
                                                        fontSize = 14.sp,
                                                        text = "${it.count}"
                                                    )
                                                    Spacer(modifier = Modifier.size(8.dp))
                                                }
                                            }
                                        }
                                    }

                                }

                            }

                        }

                    }
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseTopAppbar(
    onRefresh: ()->Unit,
){
    TopAppBar(
        title = {
            Text(text = stringResource(
                id = R.string.warehouse
            )
            )
        },
        navigationIcon = {
            Row() {
                Spacer(modifier = Modifier.size(8.dp))
                OKImage(modifier = Modifier.size(48.dp),image = R.drawable.warehouse, contentDescription = stringResource(
                    id = R.string.warehouse
                ))
            }

        },
        actions = {
            IconButton(onClick = { onRefresh() }) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(id = R.string.refresh))
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = ColorScheme.primary
        ),
    )
}
