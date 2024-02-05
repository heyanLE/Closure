package com.heyanle.closure.ui.home.instance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.R
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.ui.common.ErrorPage
import com.heyanle.closure.ui.common.LoadingPage
import com.heyanle.closure.ui.home.HomeViewModel
import com.heyanle.closure.utils.logi

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
            errorMsg = state.getGameInfo.errorMsg?:"",
            other = {
                Text(text = stringResource(id = com.heyanle.i18n.R.string.click_to_retry))
            },
            onClick = {
                instanceViewModel.refreshGetGameInfo()
                instanceViewModel.refreshLogs()
            }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ){

            // 账号信息
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
                ) {
                    Text(text = state.getGameInfo.data.config.account)
                }
            }

            // 理智


            // 操作

            // 日志
        }
    }

}