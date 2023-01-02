package com.heyanle.closure.page.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.heyanle.closure.INSTANCE
import com.heyanle.closure.LOGIN
import com.heyanle.closure.LocalNavController
import com.heyanle.closure.page.MainController
import com.heyanle.closure.R
import com.heyanle.closure.model.ItemModel
import com.heyanle.closure.net.model.GameLogItem
import com.heyanle.closure.net.model.GetGameResp
import com.heyanle.closure.page.login.ProgressDialog
import com.heyanle.closure.page.screenshot.ScreenshotDialog
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage
import com.heyanle.closure.ui.FastScrollToTopFab
import com.heyanle.closure.ui.LoadingPage
import com.heyanle.closure.ui.OKImage
import com.heyanle.closure.utils.APUtils
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2022/12/23 20:29.
 * https://github.com/heyanLE
 */
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Home(){

    val scope = rememberCoroutineScope()

    val vm = viewModel<HomeViewModel>()
    val nav = LocalNavController.current

    val current by MainController.current.observeAsState()

    val lazyListState = rememberLazyListState()

    current?.let {
        ScreenshotDialog(enable = vm.enableScreenShot.value, onDismissRequest = { vm.enableScreenShot.value = false }, select = it)
    }

    AutoSettingDialog(
        vm.enableAutoSettingDialog.value,
        onDismissRequest = {
            vm.enableAutoSettingDialog.value = false
        },
        onSave = {
            scope.launch {
                vm.updateConfig(it)
            }
        }
    )

    ProgressDialog(show = vm.enableLoadingDialog)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MainTopAppBar(vm = vm)
        },
        floatingActionButton = {
            FastScrollToTopFab(listState = lazyListState)
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
                        Text(text = stringResource(id = R.string.click_to_manager_instance))
                    },
                    clickEnable = true,
                    onClick = {
                        // 路由到 INSTANCE
                        nav.navigate(INSTANCE)
                    }
                )
            }else{
                val getGameRep by MainController.currentGetGame.observeAsState(MainController.StatusData.None())
                val logData by vm.log.observeAsState(MainController.StatusData.None())
                getGameRep.onError {
                    Box(modifier = Modifier.fillMaxSize()){
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
                        AccountCard(
                            modifier = Modifier
                                .background(ColorScheme.surface)
                                .padding(16.dp),
                            account = account,
                            platform = platform)
                    }


                }.onLoading {
                    LoadingPage(modifier = Modifier.fillMaxSize(),)
                }.onData {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        item {
                            AccountCard(
                                modifier = Modifier
                                    .background(ColorScheme.surface)
                                    .padding(16.dp),
                                account = account,
                                platform = platform)

                            Spacer(modifier = Modifier.size(16.dp))

                            MoneyPanel(getGameResp = it.data)

                            Spacer(modifier = Modifier.size(16.dp))

                            APLVPanel(getGameResp = it.data)

                            Spacer(modifier = Modifier.size(16.dp))

                            GameInstanceActions(vm)

                            Spacer(modifier = Modifier.size(8.dp))

                            LogHeader()
                        }

                        logCard(logData, vm)
                    }
                }
            }
        }
    }
}

@Composable
fun AccountCard(
    modifier: Modifier = Modifier,
    account: String,
    platform: Long,
    accountColor: Color = Color.Unspecified
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        val start = 0.coerceAtLeast(3.coerceAtMost(account.length - 1))
        val end = 7.coerceAtMost(account.length)
        val sb = StringBuilder()
        for(i in 1..end-start){
            sb.append("*")
        }

        Text(
            fontWeight = FontWeight.W900,
            color = accountColor,
            text = "${stringResource(id = R.string.account)}: ${account.replaceRange(start, end, sb.toString())}",
        )
        Box(modifier = Modifier
            .clip(
                CircleShape
            )
            .background(ColorScheme.secondary)
            .padding(8.dp, 4.dp)){
            Text(
                color = ColorScheme.onSecondary,
                fontWeight = FontWeight.W900,
                text =
                if(platform < 2)
                    stringResource(id = R.string.official_server)
                else
                    stringResource(id = R.string.bilibili_server),
                fontSize = 12.sp,
            )
        }


    }
}

/**
 * 源石 合成玉 龙门币
 */
@Composable
fun MoneyPanel(getGameResp: GetGameResp){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        // 源石
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OKImage(modifier = Modifier
                .size(48.dp)
                .padding(1.dp, 0.dp), image = ItemModel.DIAMOND_ICON_URL, contentDescription = stringResource(
                id = R.string.diamond
            ))
            Text(
                fontSize = 14.sp,
                text = "${getGameResp.status.androidDiamond}"
            )

        }

        // 合成玉
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OKImage(modifier = Modifier
                .size(48.dp)
                .padding(1.dp, 0.dp), image = ItemModel.DIAMOND_SHD_ICON_URL, contentDescription = stringResource(
                id = R.string.diamond_shd
            ))
            Text(
                fontSize = 14.sp,
                text = "${getGameResp.status.diamondShard}"
            )

        }

        // 龙门币
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OKImage(modifier = Modifier
                .size(48.dp)
                .padding(1.dp, 0.dp), image = ItemModel.GOLD_ICON_URL, contentDescription = stringResource(
                id = R.string.gold
            ))
            Text(
                fontSize = 14.sp,
                text = "${getGameResp.status.gold}"
            )

        }

    }
}

/**
 * 理智等级面板
 */
@Composable
fun APLVPanel(getGameResp: GetGameResp){
    val scope = rememberCoroutineScope()
    var nowAp by remember {
        mutableStateOf(APUtils.getNowAp(getGameResp.status.ap, getGameResp.status.maxAp, getGameResp.status.lastApAddTime))
    }

    DisposableEffect(Unit){
        scope.launch {
            while(scope.isActive){
                // 每隔增加 1 理智时间 *2 时间刷新一下理智数
                delay((APUtils.AP_UP_TIME*2).toLong())
                nowAp = APUtils.getNowAp(getGameResp.status.ap, getGameResp.status.maxAp, getGameResp.status.lastApAddTime)
            }
        }
        onDispose {
            scope.cancel()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorScheme.surface)
            .padding(12.dp)
    ) {
        // 理智 等级
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Text(
                fontWeight = FontWeight.W900,
                color = ColorScheme.secondary,
                text = stringResource(id = R.string.ap),
            )
            Box(modifier = Modifier
                .clip(
                    CircleShape
                )
                .background(ColorScheme.secondary)
                .padding(8.dp, 4.dp)){
                Text(
                    color = ColorScheme.onSecondary,
                    fontWeight = FontWeight.W900,
                    text = "Lv.${getGameResp.status.level}",
                    fontSize = 12.sp,
                )
            }

        }
        // 理智 xx/xx
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center,
        ){
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                fontWeight = FontWeight.W900,
                fontSize = 48.sp,
                color = ColorScheme.secondary,
                text = "$nowAp",
            )
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                fontWeight = FontWeight.W900,
                fontSize = 48.sp,
                text = "/${getGameResp.status.maxAp}",
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .alpha(0.6f)
                .padding(4.dp, 0.dp)
                .background(ColorScheme.onSurface))
            // 预计溢出时间
            Text(
                modifier = Modifier.padding(12.dp, 0.dp),
                textAlign = TextAlign.Start,
                color = ColorScheme.secondary,
                text = stringResource(id = R.string.ap_max_time)
            )
            Box(modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .alpha(0.6f)
                .padding(4.dp, 0.dp)
                .background(ColorScheme.onSurface))
        }

        Spacer(modifier = Modifier.size(4.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = APUtils.getAPMaxTime(getGameResp.status.ap, getGameResp.status.maxAp, getGameResp.status.lastApAddTime*1000)
        )

        

        

    }
}

@Composable
fun GameInstanceActions(
    vm: HomeViewModel
){
    val nav = LocalNavController.current
    Column() {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorScheme.secondary,
                contentColor = ColorScheme.onSecondary
            ),
            onClick = {
                vm.onInstanceConfig()
            }) {
            Text(text = stringResource(id = R.string.instance_config))
        }

        Spacer(modifier = Modifier.size(4.dp))

        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Button(
                modifier = Modifier
                    .padding(8.dp, 0.dp)
                    .weight(1f),
                onClick = {
                    vm.onWarehouse(nav)
                }
            ) {
                Text(text = stringResource(id = R.string.warehouse))
            }
            Button(
                modifier = Modifier
                    .padding(8.dp, 0.dp)
                    .weight(1f),
                onClick = {
                    vm.onScreenshot()
                }) {
                Text(text = stringResource(id = R.string.screenshot))
            }
        }


    }
    
}

@Composable
fun LogHeader(){
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(ColorScheme.surface)
        .padding(12.dp, 16.dp)){
        Text(
            fontWeight = FontWeight.W900,
            color = ColorScheme.secondary,
            text = stringResource(id = R.string.log),
        )
    }

}

fun LazyListScope.logCard(
    data: MainController.StatusData<List<GameLogItem>>,
    vm: HomeViewModel,
){

    if(data.isLoading()){
        item {
            LoadingPage(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorScheme.surface),
            )
        }
    }else if(data.isError()){
        item {
            val errorData = data as? MainController.StatusData.Error
            ErrorPage(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorScheme.surface),
                errorMsg = errorData?.errorMsg?:"",
                clickEnable = true,
            ) {
                vm.viewModelScope.launch {
                    vm.loadLog()
                }
            }
        }

    }else if(data.isData()){
        val errorData = data as? MainController.StatusData.Data
        val list = errorData?.data?: emptyList()
        items(list) {
            Column(modifier = Modifier
                .background(ColorScheme.surface)
                .padding(12.dp, 0.dp)){
                LogItem(item = it)
                Spacer(modifier = Modifier
                    .size(16.dp))
            }

        }
    }

}

@Composable
fun LogItem(
    item: GameLogItem
){
    Row (
        modifier = Modifier
            .fillMaxWidth()

    ) {

        Text(
            fontSize = 12.sp,
            text = "${item.getData()}\n${item.getTime()}")
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = item.info)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    vm: HomeViewModel,
){
    val title by vm.topBarTitle.observeAsState(stringResource(id = R.string.app_name))
    val image by vm.avatarImage.observeAsState(R.drawable.logo)
    val scope = rememberCoroutineScope()

    val nav = LocalNavController.current

    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            Row() {
                Spacer(modifier = Modifier.size(8.dp))
                OKImage(modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),image = image, contentDescription = title)
            }

        },
        actions = {
            IconButton(onClick = {
                scope.launch {
                    vm.loadGetGameResp()
                    vm.loadLog()
                }

            }) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(id = R.string.refresh))
            }
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
                        Text(text = stringResource(id = R.string.instance_manager))
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
