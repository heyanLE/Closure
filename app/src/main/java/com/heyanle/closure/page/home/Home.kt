package com.heyanle.closure.page.home

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.heyanle.closure.INSTANCE
import com.heyanle.closure.LOGIN
import com.heyanle.closure.LocalNavController
import com.heyanle.closure.page.MainController
import com.heyanle.closure.R
import com.heyanle.closure.net.model.GetGameResp
import com.heyanle.closure.page.game_instance.DoubleText
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage
import com.heyanle.closure.ui.LoadingPage
import com.heyanle.closure.ui.OKImage

/**
 * Created by HeYanLe on 2022/12/23 20:29.
 * https://github.com/heyanLE
 */
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Home(){

    val vm = viewModel<HomeViewModel>()
    val nav = LocalNavController.current

    val current by MainController.current.observeAsState()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MainTopAppBar(vm = vm)
        }
    ){ padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .background(ColorScheme.background)
        ){
            val account = current?.account?:""
            val platform = current?.platform?:-1L
            // 如果当前没选择实例，引导选择
            if(account.isEmpty() || platform == -1L){
                ErrorPage(
                    modifier = Modifier.fillMaxSize(),
                    image = R.drawable.empty,
                    errorMsg = stringResource(id = R.string.no_chosen_instance),
                    other = {
                        Text(text = stringResource(id = R.string.click_to_choose_instance))
                    },
                    clickEnable = true,
                    onClick = {
                        // 路由到 INSTANCE
                        nav.navigate(INSTANCE)
                    }
                )
            }else{
                val getGameRep by MainController.currentGetGame.observeAsState(MainController.StatusData.None())
                getGameRep.onError {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AccountCard(account, platform)
                    }

                }.onLoading {
                    LoadingPage(modifier = Modifier.fillMaxSize(),)
                }.onData {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AccountCard(account, platform)
                        GamePanel(getGameResp = it.data)
                    }
                }
            }

        }
    }


}

@Composable
fun AccountCard(account: String, platform: Long){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        DoubleText(
            startText = "${stringResource(id = R.string.account)}: ${account.replaceRange(3, 7, "****")}",
            endText =
            if(platform < 2)
                stringResource(id = R.string.official_server)
            else
                stringResource(id = R.string.bilibili_server)
        )
    }
}

@Composable
fun GamePanel(getGameResp: GetGameResp){
    Text(text = "理智 ${getGameResp.status.ap}/${getGameResp.status.maxAp}")
    Text(text = "等级 ${getGameResp.status.level}")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    vm: HomeViewModel
){
    val title by vm.topBarTitle.observeAsState(stringResource(id = R.string.app_name))
    val image by vm.avatarImage.observeAsState(R.drawable.logo)

    val nav = LocalNavController.current

    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            Row() {
                Spacer(modifier = Modifier.size(8.dp))
                OKImage(modifier = Modifier.size(48.dp),image = image, contentDescription = title)
            }

        },
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Filled.MoreVert, contentDescription = stringResource(id = R.string.more))
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                // 切换实例
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(id = R.string.change_instance))
                    },
                    onClick = {
                        // 路由到 INSTANCE
                        nav.navigate(INSTANCE)
                        showMenu = false

                    },
                )
                // 切换账号
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(id = R.string.change_account))
                    },
                    onClick = {
                        // 路由到 LOGIN
                        nav.navigate(LOGIN)
                        showMenu = false
                    },
                )

            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = ColorScheme.primary
        ),
    )
}
