package com.heyanle.closure.page.game_instance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.heyanle.closure.LocalNavController
import com.heyanle.closure.page.MainController
import com.heyanle.closure.R
import com.heyanle.closure.model.StageModel
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage
import com.heyanle.closure.ui.LoadingPage
import com.heyanle.closure.ui.OKImage
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
            InstanceTopAppBar {

            }
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
                LazyColumn(
                    modifier = Modifier.padding(8.dp, 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    val data = it.data
                    items(count = data.size){
                        GameInstanceCard(resp = data[it]) { resp ->
                            MainController.current.value = MainController.InstanceSelect(resp.config.account, resp.config.platform)
                            nav.popBackStack()
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
){
    TopAppBar(
        title = {
            Text(text = stringResource(
                id = R.string.please_choose_instance
            ))
        },
        navigationIcon = {
            Row() {
                Spacer(modifier = Modifier.size(8.dp))
                OKImage(modifier = Modifier.size(48.dp),image = R.drawable.skadi, contentDescription = stringResource(
                    id = R.string.please_choose_instance
                ))
            }

        },
        actions = {
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
    onClick: (GameResp)->Unit,
){
    Box(
        modifier = Modifier
            .background(ColorScheme.surface)
            .clickable {
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
            var text = stringResource(id = R.string.much_stage)
            val list = resp.gameConfig.battleMaps
            if(list.isNotEmpty()){
                val map by StageModel.mapLiveData.observeAsState(emptyMap())
                val stage = map.getOrDefault(resp.gameConfig.battleMaps[0], null)
                text = if(stage == null){
                    resp.gameConfig.battleMaps[0]
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