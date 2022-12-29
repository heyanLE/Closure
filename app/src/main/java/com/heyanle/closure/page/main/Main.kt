package com.heyanle.closure.page.main

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.More
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.heyanle.closure.INSTANCE
import com.heyanle.closure.LOGIN
import com.heyanle.closure.LocalNavController
import com.heyanle.closure.MainController
import com.heyanle.closure.R
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.page.game_instance.Instance
import com.heyanle.closure.page.home.Home
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage
import com.heyanle.closure.ui.HomePagerLayout
import com.heyanle.closure.ui.HomeTab
import com.heyanle.closure.ui.OKImage
import com.heyanle.closure.utils.stringRes
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2022/12/23 20:29.
 * https://github.com/heyanLE
 */
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Main(){

    val vm = viewModel<MainViewModel>()
    val nav = LocalNavController.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MainTopAppBar(vm = vm)
        }
    ){ padding ->
        Box(
            modifier = Modifier.padding(padding).background(ColorScheme.background)
        ){
            val currentInstance by MainController.currentGameInstance.observeAsState(null)
            val cur = currentInstance

            // 如果当前没选择实例，引导选择
            if(cur == null){
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
                Home(gameResp = cur)
            }

        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    vm: MainViewModel
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
                Icon(Icons.Filled.Menu, contentDescription = stringResource(id = R.string.more))
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
