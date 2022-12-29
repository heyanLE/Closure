package com.heyanle.closure.page.game_instance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.heyanle.closure.LocalNavController
import com.heyanle.closure.MainController
import com.heyanle.closure.R
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.page.main.MainViewModel
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage
import com.heyanle.closure.ui.LoadingPage
import com.heyanle.closure.utils.stringRes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2022/12/23 22:01.
 * https://github.com/heyanLE
 */

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Instance(
){

    val nav = LocalNavController.current
    val scope = rememberCoroutineScope()
    val vm = viewModel<GameInstanceViewModel>()

    var isFirst by remember {
        mutableStateOf(false)
    }
    val isError by vm.isError.observeAsState(initial = false)
    val isLoading by vm.isLoading.observeAsState(initial = false)
    val dat by MainController.gameInstance.observeAsState(initial = null)


    Box(modifier = Modifier
        .fillMaxSize()
        .background((ColorScheme.primary))){
        if(isFirst || isLoading) {
            LaunchedEffect(Unit){
                // 避免页面切换动画卡顿
                delay(200)
                isFirst = false
            }
            LoadingPage(modifier = Modifier.fillMaxSize(),)
        }else if(isError || dat == null){
            ErrorPage(
                modifier = Modifier.fillMaxSize(),
                errorMsg = vm.errorCode,
                clickEnable = true,
                other = {
                    Text(text = stringResource(id = R.string.click_to_retry))
                },
                onClick = {
                    scope.launch {
                        vm.refresh()
                    }
                }
            )
        } else {
            val data = dat?: emptyList()
            LazyColumn(
                modifier = Modifier.padding(8.dp, 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                items(count = data.size){
                    GameInstanceCard(resp = data[it]) { resp ->
                        MainController.currentGameInstance.value = resp
                        nav.popBackStack()
                    }
                }
            }
        }

    }
}

@Composable
fun GameInstanceCard(
    resp: GameResp,
    onClick: (GameResp)->Unit,
){
    Box(
        modifier = Modifier.background(ColorScheme.surface).clickable {
            onClick(resp)
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){
            DoubleText(
                startText = "${stringResource(id = R.string.account)}: ${resp.config.account.replaceRange(3, 7, "****")}",
                endText =
                if(resp.config.platform < 2)
                    stringResource(id = R.string.official_server)
                else
                    stringResource(id = R.string.bilibili_server)
            )
            DoubleText(
                startText = stringResource(id = R.string.status),
                endText = resp.status.text
            )
            DoubleText(
                startText = stringResource(id = R.string.map),
                endText = "${resp.getMapCode()}(${resp.getMapName()})"
            )
            DoubleText(
                startText = stringResource(id = R.string.keep_ap),
                endText = resp.gameConfig.keepingAP.toString()
            )
            DoubleText(
                startText = stringResource(id = R.string.building_arrange),
                endText = if(resp.gameConfig.enableBuildingArrange)stringResource(id = R.string.enable) else stringResource(id = R.string.unable),
            )


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