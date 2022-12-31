package com.heyanle.closure.page.game_instance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.heyanle.closure.LocalAct
import com.heyanle.closure.LocalNavController
import com.heyanle.closure.page.MainController
import com.heyanle.closure.R
import com.heyanle.closure.model.StageModel
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.page.home.AccountCard
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage
import com.heyanle.closure.ui.LoadingIcon
import com.heyanle.closure.ui.LoadingPage
import com.heyanle.closure.ui.OKImage
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toast
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2022/12/23 22:01.
 * https://github.com/heyanLE
 */

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Instance() {
    val nav = LocalNavController.current
    val scope = rememberCoroutineScope()
    val vm = viewModel<GameInstanceViewModel>()
    val status by MainController.instance.observeAsState(MainController.StatusData.None())
    val curStatus = status

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            InstanceTopAppBar(
                onAddClick = {
                    vm.onAddClick()
                },
                onRefresh = {
                    scope.launch {
                        vm.loadGameInstances()
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(ColorScheme.background)
        ) {
            curStatus.onError {
                ErrorPage(
                    modifier = Modifier.fillMaxSize(),
                    errorMsg = it.errorMsg,
                    clickEnable = true,
                    other = {
                        Text(text = stringResource(id = R.string.click_to_retry))
                    },
                    onClick = {
                        scope.launch {
                            vm.loadGameInstances()
                        }
                    }
                )
            }.onLoading {
                LoadingPage(modifier = Modifier.fillMaxSize(),)
            }.onData {
                if(it.data.isEmpty()){
                    ErrorPage(
                        modifier = Modifier.fillMaxSize(),
                        image = R.drawable.kaltsit,
                        errorMsg = stringResource(id = R.string.no_instance),
                        clickEnable = true,
                        onClick = {
                            vm.onAddClick()
                        },
                        other = {
                            Text(text = stringResource(id = R.string.click_to_add_instance))
                        }
                    ) 
                }else{
                    val act = LocalAct.current
                    LazyColumn(
                        modifier = Modifier.padding(8.dp, 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        item {
                            Text(
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(0.6f),
                                textAlign = TextAlign.Start,
                                text = stringResource(id = R.string.click_to_choose_instance)
                            )
                        }

                        val data = it.data

                        items(count = data.size){
                            GameInstanceCard(
                                resp = data[it],
                                onCaptcha = {
                                    scope.launch {
                                        vm.gameCaptcha(it, act)
                                    }
                                },
                                onLogin = {
                                    scope.launch {
                                        vm.gameLogin(it)
                                    }
                                },
                                onPause = {
                                    scope.launch {
                                        vm.gamePause(it)
                                    }
                                },
                                onDelete = {
                                    scope.launch {
                                        vm.instanceDelete(it)
                                    }
                                }
                            ) { resp ->
                                when(resp.status.code){
                                    999 -> {
                                        stringRes(R.string.please_captcha_first).toast()
                                    }
                                    2 -> {
                                        MainController.current.value = MainController.InstanceSelect(resp.config.account, resp.config.platform)
                                        nav.popBackStack()
                                    }
                                    1 -> {
                                        stringRes(R.string.game_logging).toast()
                                    }
                                    else -> {
                                        stringRes(R.string.please_login_game_first).toast()
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
fun InstanceTopAppBar(
    onAddClick: ()->Unit,
    onRefresh: ()->Unit,
){
    TopAppBar(
        title = {
            Text(text = stringResource(
                id = R.string.instance_manager
            ))
        },
        navigationIcon = {
            Row() {
                Spacer(modifier = Modifier.size(8.dp))
                OKImage(modifier = Modifier.size(48.dp),image = R.drawable.skadi, contentDescription = stringResource(
                    id = R.string.instance_manager
                ))
            }

        },
        actions = {
            IconButton(onClick = { onRefresh() }) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(id = R.string.refresh))
            }
            IconButton(onClick = { onAddClick() }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_instance))
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = ColorScheme.primary
        ),
    )
}

@Composable
fun GameInstanceCard(
    resp: GameResp,
    onCaptcha: (GameResp)-> Unit,
    onLogin: (GameResp)-> Unit,
    onPause: (GameResp)-> Unit,
    onDelete: (GameResp)-> Unit,
    onClick: (GameResp)->Unit,
){
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .background(ColorScheme.surface)
            .height(IntrinsicSize.Min)
            .clickable {
                onClick(resp)
            }

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){

            AccountCard(
                account = resp.config.account,
                platform = resp.config.platform,
                accountColor = ColorScheme.secondary,
            )

            DoubleText(
                startText = stringResource(id = R.string.status),
                endText = resp.status.text
            )
            var text = stringResource(id = R.string.much_stage)
            val list = resp.gameConfig.battleMaps?: emptyList()
            if(list.isNotEmpty()){
                val map by StageModel.mapLiveData.observeAsState(emptyMap())
                val stage = map.getOrDefault(resp.gameConfig.battleMaps?.get(0)?:"", null)
                text = if(stage == null){
                    resp.gameConfig.battleMaps?.get(0)?:""
                }else{
                    "${stage.code} ${stage.name}"
                }
            }
            DoubleText(
                startText = stringResource(id = R.string.map),
                endText = text
            )
            DoubleText(
                startText = stringResource(id = R.string.keep_ap),
                endText = resp.gameConfig.keepingAP.toString()
            )
            DoubleText(
                startText = stringResource(id = R.string.building_arrange),
                endText = if(resp.gameConfig.enableBuildingArrange)stringResource(id = R.string.enable) else stringResource(id = R.string.unable),
            )
            Spacer(modifier = Modifier.height(4.dp))
            InstanceAction(
                gameResp = resp,
                onCaptcha = {
                    onCaptcha(resp)
                },
                onLogin = {
                    onLogin(resp)
                },
                onPause = {
                    onPause(resp)
                },
                onDelete = {
                    onDelete(resp)
                }
            )




        }


        // 游戏启动中蒙层
        if(resp.status.code == 1){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xB3000000)),
                contentAlignment = Alignment.Center
            ){
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ){

                    LoadingIcon()
                    Spacer(modifier = Modifier.size(16.dp))
                    Column() {
                        Text(text = stringResource(id = R.string.game_logging))
                        Text(text = stringResource(id = R.string.it_take_five_minute))
                        Text(text = stringResource(id = R.string.please_refresh))
                    }
                }
            }
        }
    }
}

@Composable
fun InstanceAction(
    gameResp: GameResp,
    onCaptcha: ()-> Unit,
    onLogin: ()-> Unit,
    onPause: ()-> Unit,
    onDelete: ()-> Unit,
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Button(
            onClick = {
                onDelete()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorScheme.error,
                contentColor = ColorScheme.onError
            ),
        ) {
            Text(text = stringResource(id = R.string.delete_instance))
        }

        Button(
            onClick = {
                when(gameResp.status.code){
                    999 -> onCaptcha()
                    2 -> onPause()
                    else -> onLogin()
                }
            },
        ) {
            val text = when(gameResp.status.code){
                999 -> stringResource(id = R.string.captcha)
                2 -> stringResource(id = R.string.pause_game)
                else -> stringResource(id = R.string.login_game)
            }
            Text(text = text)
        }


    }
}

@Composable
fun DoubleText(
    startText: String,
    endText: String,
){
    Box(
        Modifier
            .fillMaxWidth(),
    ){
        Text(
            modifier = Modifier.align(Alignment.TopStart),
            text = startText,
        )

        Text(
            modifier = Modifier.align(Alignment.TopEnd),
            text = endText,
        )

    }
}

