package com.heyanle.closure.ui.home.instance

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.R
import com.heyanle.closure.closure.assets.AssetsController
import com.heyanle.closure.closure.game.model.GetGameInfo
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.ui.common.ErrorPage
import com.heyanle.closure.ui.common.LoadingPage
import com.heyanle.closure.ui.common.OkImage
import com.heyanle.closure.ui.home.HomeViewModel
import com.heyanle.closure.ui.home.instance_manage.AccountCard
import com.heyanle.closure.utils.APUtils
import com.heyanle.closure.utils.logi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Created by heyanle on 2024/1/27.
 * https://github.com/heyanLE
 */
@Composable
fun Instance(
    homeViewModel: HomeViewModel,
    username: String,
    account: String,
) {

    val vm = viewModel<InstanceViewModel>(factory = InstanceViewModelFactory(username, account))
    val instance = vm.instanceState.collectAsState()
    instance.value.logi("Instance")
    if (!instance.value.webGame.isLoading && instance.value.webGame.data != null) {
        InstanceContent(
            homeViewModel = homeViewModel,
            instanceViewModel = vm,
            state = instance.value
        )
    }


}

@Composable
fun InstanceContent(
    homeViewModel: HomeViewModel,
    instanceViewModel: InstanceViewModel,
    state: InstanceViewModel.InstanceState
) {

    if (state.getGameInfo.isLoading) {
        LoadingPage(modifier = Modifier.fillMaxSize())
    } else if (state.getGameInfo.data == null) {
        ErrorPage(
            modifier = Modifier.fillMaxSize(),
            errorMsg = state.getGameInfo.errorMsg ?: "",
            other = {
                Text(text = stringResource(id = com.heyanle.i18n.R.string.click_to_retry))
            },
            clickEnable = true,
            onClick = {
                instanceViewModel.refreshGetGameInfo()
                instanceViewModel.refreshLogs()
            }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 账号信息
            item {

                AccountCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                        .padding(16.dp),
                    account = state.getGameInfo.data.config.account,
                    platform = state.webGame.data?.status?.platform?.toLong() ?: -1L,
                    accountColor = MaterialTheme.colorScheme.secondary,

                )
            }
            
            // 原石 合成玉 龙门币
             
            item { 
                MoneyPanel(getGameInfo = state.getGameInfo.data)
            }

            // 理智

            item {
                APLVPanel(getGameResp = state.getGameInfo.data)
            }

            // 操作

            // 日志
        }
    }

}

/**
 * 源石 合成玉 龙门币
 */
@Composable
fun MoneyPanel(getGameInfo: GetGameInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 源石
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OkImage(
                modifier = Modifier
                    .size(48.dp)
                    .padding(1.dp, 0.dp),
                image = AssetsController.DIAMOND_ICON_URL,
                contentDescription = stringResource(
                    id = com.heyanle.i18n.R.string.diamond
                )
            )
            Text(
                fontSize = 14.sp,
                text = "${getGameInfo.status.androidDiamond}"
            )

        }

        // 合成玉
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OkImage(
                modifier = Modifier
                    .size(48.dp)
                    .padding(1.dp, 0.dp),
                image = AssetsController.DIAMOND_SHD_ICON_URL,
                contentDescription = stringResource(
                    id = com.heyanle.i18n.R.string.diamond_shd
                )
            )
            Text(
                fontSize = 14.sp,
                text = "${getGameInfo.status?.diamondShard}"
            )

        }

        // 龙门币
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OkImage(
                modifier = Modifier
                    .size(48.dp)
                    .padding(1.dp, 0.dp),
                image = AssetsController.GOLD_ICON_URL,
                contentDescription = stringResource(
                    id = com.heyanle.i18n.R.string.gold
                )
            )
            Text(
                fontSize = 14.sp,
                text = "${getGameInfo.status.gold}"
            )

        }

    }
}

/**
 * 理智等级面板
 */
@Composable
fun APLVPanel(getGameResp: GetGameInfo) {
    val scope = rememberCoroutineScope()
    var nowAp by remember {
        mutableStateOf(
            APUtils.getNowAp(
                getGameResp.status.ap.toLong() ?: 0,
                getGameResp.status.maxAp.toLong() ?: 0,
                getGameResp.status.lastApAddTime.toLong() ?: 0
            )
        )
    }

    DisposableEffect(Unit) {
        scope.launch {
            while (scope.isActive) {
                // 每隔增加 1 理智时间 *2 时间刷新一下理智数
                delay((APUtils.AP_UP_TIME * 2).toLong())
                nowAp = APUtils.getNowAp(
                    getGameResp.status.ap.toLong() ?: 0,
                    getGameResp.status.maxAp.toLong() ?: 0,
                    getGameResp.status.lastApAddTime?.toLong() ?: 0
                )
            }
        }
        onDispose {
            scope.cancel()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            .padding(12.dp)
    ) {
        // 理智 等级
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                fontWeight = FontWeight.W900,
                color = MaterialTheme.colorScheme.secondary,
                text = stringResource(id = com.heyanle.i18n.R.string.ap),
            )
            Box(
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(8.dp, 4.dp)
            ) {
                Text(
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.W900,
                    text = "Lv.${getGameResp.status?.level}",
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
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                fontWeight = FontWeight.W900,
                fontSize = 48.sp,
                color = MaterialTheme.colorScheme.secondary,
                text = "$nowAp",
            )
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                fontWeight = FontWeight.W900,
                fontSize = 48.sp,
                text = "/${getGameResp.status?.maxAp}",
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .alpha(0.6f)
                    .padding(4.dp, 0.dp)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
            )
            // 预计溢出时间
            Text(
                modifier = Modifier.padding(12.dp, 0.dp),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.secondary,
                text = stringResource(id = com.heyanle.i18n.R.string.ap_max_time)
            )
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .alpha(0.6f)
                    .padding(4.dp, 0.dp)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
            )
        }

        Spacer(modifier = Modifier.size(4.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = APUtils.getAPMaxTime(
                getGameResp.status.ap.toLong() ?: 0,
                getGameResp.status.maxAp.toLong() ?: 0,
                (getGameResp.status.lastApAddTime.toLong() ?: 0) * 1000
            )
        )


    }
}