package com.heyanle.closure.compose.home.instance

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heyanle.closure.R
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.closure.entity.GameInfo
import com.heyanle.closure.closure.items.ItemsController
import com.heyanle.closure.compose.LocalNavController
import com.heyanle.closure.compose.common.ErrorPage
import com.heyanle.closure.compose.common.FastScrollToTopFab
import com.heyanle.closure.compose.common.LoadingPage
import com.heyanle.closure.compose.common.OKImage
import com.heyanle.closure.compose.home.HomeViewModel
import com.heyanle.closure.compose.home.manager.AccountCard
import com.heyanle.closure.net.Net
import com.heyanle.closure.net.model.Announcement
import com.heyanle.closure.net.model.Config
import com.heyanle.closure.net.model.GameLogItem
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.net.model.GameStatus
import com.heyanle.closure.net.model.GetGameResp
import com.heyanle.closure.utils.APUtils
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.timeToString
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by HeYanLe on 2023/8/20 16:39.
 * https://github.com/heyanLE
 */
@Composable
fun Instance(
    enable: Boolean,
    gameResp: GameResp,
    instanceViewModel: InstanceViewModel,
    homeViewModel: HomeViewModel,
    onBind: (GameResp) -> Unit,
    onConfig: (GameResp) -> Unit,
    onScreenshot: (GameResp) -> Unit,
) {


    LaunchedEffect(key1 = enable, key2 = instanceViewModel.gameInfo.value) {
        if (enable && instanceViewModel.gameInfo.value is InstanceViewModel.GameInfoState.None) {
            instanceViewModel.refresh()
        }
    }

    val closureController: ClosureController by Injekt.injectLazy()

    val itemController: ItemsController by Injekt.injectLazy()
    val itemMap = itemController.map.collectAsState()
    val lazyGridState = rememberLazyGridState()
    val state = instanceViewModel.gameInfo.value
    val items = remember(key1 = state, key2 = itemMap.value) {
        val gameInfo =
            (state as? InstanceViewModel.GameInfoState.Info) ?: return@remember emptyList()
        instanceViewModel.getItems(gameInfo.gameInfo, itemMap.value)
    }
    LaunchedEffect(key1 = enable, key2 = state){
        val gameInfo =
            (state as? InstanceViewModel.GameInfoState.Info) ?: return@LaunchedEffect
        if(!enable){
            return@LaunchedEffect
        }
        gameInfo.gameInfo
        homeViewModel.avatarImg.value = gameInfo.gameInfo.status?.getSecretaryIconUrl()?:R.drawable.logo
        homeViewModel.topBarTitle.value = gameInfo.gameInfo.status?.nickName?: stringRes(R.string.app_name)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(50.dp),
            state = lazyGridState,
            contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 106.dp)
        ) {
            item(span = {
                // LazyGridItemSpanScope:
                // maxLineSpan
                GridItemSpan(maxLineSpan)
            }) {
                AccountCard(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(16.dp),
                    account = gameResp.config.account,
                    platform = gameResp.config.platform
                )
            }

            when (state) {
                is InstanceViewModel.GameInfoState.None -> {
                }

                is InstanceViewModel.GameInfoState.Loading -> {
                    item(span = {
                        // LazyGridItemSpanScope:
                        // maxLineSpan
                        GridItemSpan(maxLineSpan)
                    }) {
                        LoadingPage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                        )
                    }

                }

                is InstanceViewModel.GameInfoState.Empty -> {
                    item(span = {
                        // LazyGridItemSpanScope:
                        // maxLineSpan
                        GridItemSpan(maxLineSpan)
                    }) {
                        ErrorPage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            errorMsg = stringResource(id = R.string.instance_empty),
                            clickEnable = true,
                            onClick = {
                                instanceViewModel.refresh()
                            },
                            other = {
                                Text(text = stringResource(id = R.string.click_to_retry))
                            }
                        )
                    }

                }

                is InstanceViewModel.GameInfoState.Info -> {
                    item(span = {
                        // LazyGridItemSpanScope:
                        // maxLineSpan
                        GridItemSpan(maxLineSpan)
                    }) {
                        Column {
                            UpdateTime(state.gameInfo)
                            Spacer(modifier = Modifier.size(16.dp))
                            val anno = closureController.announcement.collectAsState()
                            anno.value.anno?.let {
                                AnnoCard(announcement = it)
                                Spacer(modifier = Modifier.size(16.dp))
                            }

                            MoneyPanel(gameInfo = state.gameInfo)
                            Spacer(modifier = Modifier.size(16.dp))
                            APLVPanel(getGameResp = state.gameInfo)
                            Spacer(modifier = Modifier.size(16.dp))
                            GameInstanceActions(
                                onBind = {
                                    onBind(gameResp)
                                },
                                onConfig = {
                                    onConfig(gameResp)
                                },
                                onScreenshot = {
                                    onScreenshot(gameResp)
                                })
                            Spacer(modifier = Modifier.size(16.dp))

                            ChangeHeader(isLog = instanceViewModel.isShowLog, onChange = {
                                instanceViewModel.isShowLog = it
                            })
                        }
                    }

                    if (instanceViewModel.isShowLog) {
                        items(
                            instanceViewModel.logList,
                            span = {
                                // LazyGridItemSpanScope:
                                // maxLineSpan
                                GridItemSpan(maxLineSpan)
                            }
                        ) {
                            LogItem(item = it)

                        }
                    } else {
                        item(
                            span = {
                                // LazyGridItemSpanScope:
                                // maxLineSpan
                                GridItemSpan(maxLineSpan)
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(16.dp, 0.dp, 16.dp, 16.dp)
                            ) {
                                TextButton(
                                    onClick = {
                                        instanceViewModel.pushOcr()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.background,
                                        contentColor = MaterialTheme.colorScheme.onBackground,
                                    ),
                                ) {
                                    Text(text = stringResource(id = R.string.click_to_ocr))
                                }


                                if (items.isEmpty()) {
                                    ErrorPage(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp),
                                        errorMsg = stringResource(id = R.string.please_refresh_warehouse),
                                        clickEnable = false,
                                    )
                                } else {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(0.dp, 4.dp, 0.dp, 12.dp),
                                        textAlign = TextAlign.Center,
                                        text = "${stringResource(id = R.string.last_update_time)} ${(state.gameInfo.lastFreshTs * 1000L).timeToString()}"
                                    )
                                }
                            }


                        }

                        if (items.isNotEmpty()) {
                            items(items) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    OKImage(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .padding(1.dp, 0.dp),
                                        image = it.iconUrl,
                                        contentDescription = it.iconUrl
                                    )
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

        FastScrollToTopFab(listState = lazyGridState, after = 2)
    }


}

@Composable
fun AnnoCard(
    announcement: Announcement
) {
    var showAll by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable {
                showAll = !showAll
            }
            .padding(12.dp, 16.dp),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                fontWeight = FontWeight.W900,
                color = MaterialTheme.colorScheme.secondary,
                text = stringResource(id = R.string.anno),
            )

            Text(
                text = stringResource(id = R.string.click_to_open),
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = announcement.announcement,
            maxLines = if (showAll) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis
        )


    }
}

@Composable
fun UpdateTime(
    gameInfo: GameInfo
) {
    val time = remember(gameInfo.timestamp) {
        val date = Date(gameInfo.timestamp)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        format.format(date)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp, 0.dp, 16.dp, 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            fontWeight = FontWeight.W900,
            text = stringResource(id = R.string.last_update_time),
        )
        Text(text = time)

    }
}

/**
 * 源石 合成玉 龙门币
 */
@Composable
fun MoneyPanel(gameInfo: GameInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 源石
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OKImage(
                modifier = Modifier
                    .size(48.dp)
                    .padding(1.dp, 0.dp),
                image = ItemsController.DIAMOND_ICON_URL,
                contentDescription = stringResource(
                    id = R.string.diamond
                )
            )
            Text(
                fontSize = 14.sp,
                text = "${gameInfo.status?.androidDiamond}"
            )

        }

        // 合成玉
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OKImage(
                modifier = Modifier
                    .size(48.dp)
                    .padding(1.dp, 0.dp),
                image = ItemsController.DIAMOND_SHD_ICON_URL,
                contentDescription = stringResource(
                    id = R.string.diamond_shd
                )
            )
            Text(
                fontSize = 14.sp,
                text = "${gameInfo.status?.diamondShard}"
            )

        }

        // 龙门币
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OKImage(
                modifier = Modifier
                    .size(48.dp)
                    .padding(1.dp, 0.dp),
                image = ItemsController.GOLD_ICON_URL,
                contentDescription = stringResource(
                    id = R.string.gold
                )
            )
            Text(
                fontSize = 14.sp,
                text = "${gameInfo.status?.gold}"
            )

        }

    }
}

/**
 * 理智等级面板
 */
@Composable
fun APLVPanel(getGameResp: GameInfo) {
    val scope = rememberCoroutineScope()
    var nowAp by remember {
        mutableStateOf(
            APUtils.getNowAp(
                getGameResp.status?.ap ?: 0,
                getGameResp.status?.maxAp ?: 0,
                getGameResp.status?.lastApAddTime ?: 0
            )
        )
    }

    DisposableEffect(Unit) {
        scope.launch {
            while (scope.isActive) {
                // 每隔增加 1 理智时间 *2 时间刷新一下理智数
                delay((APUtils.AP_UP_TIME * 2).toLong())
                nowAp = APUtils.getNowAp(
                    getGameResp.status?.ap ?: 0,
                    getGameResp.status?.maxAp ?: 0,
                    getGameResp.status?.lastApAddTime ?: 0
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
            .background(MaterialTheme.colorScheme.secondaryContainer)
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
                text = stringResource(id = R.string.ap),
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
                text = stringResource(id = R.string.ap_max_time)
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
                getGameResp.status?.ap ?: 0,
                getGameResp.status?.maxAp ?: 0,
                (getGameResp.status?.lastApAddTime ?: 0) * 1000
            )
        )


    }
}

@Composable
fun GameInstanceActions(
    onBind: () -> Unit,
    onConfig: () -> Unit,
    onScreenshot: () -> Unit,
) {
    Column() {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            onClick = {
                onConfig()
            }) {
            Text(text = stringResource(id = R.string.instance_config))
        }

        Spacer(modifier = Modifier.size(4.dp))


        Row(
            modifier = Modifier.padding(8.dp, 4.dp)
        ) {
            Button(
                modifier = Modifier
                    .padding(8.dp, 0.dp)
                    .weight(1f),
                onClick = {
                    onBind()
                }
            ) {
                Text(text = stringResource(id = R.string.bind_qq))
            }
            Button(
                modifier = Modifier
                    .padding(8.dp, 0.dp)
                    .weight(1f),
                onClick = {
                    onScreenshot()
                }) {
                Text(text = stringResource(id = R.string.screenshot))
            }
        }


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeHeader(
    isLog: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .run {
                    if (isLog) {
                        background(MaterialTheme.colorScheme.secondary)
                    } else {
                        this
                    }

                }
                .clickable {
                    onChange(true)
                }
                .padding(8.dp, 4.dp),
            fontWeight = FontWeight.W900,
            color = if (isLog) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onBackground,
            text = stringResource(id = R.string.log),
        )

        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .run {
                    if (!isLog) {
                        background(MaterialTheme.colorScheme.secondary)
                    } else {
                        this
                    }
                }
                .clickable {
                    onChange(false)
                }
                .padding(8.dp, 4.dp),
            fontWeight = FontWeight.W900,
            color = if (!isLog) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onBackground,
            text = stringResource(id = R.string.warehouse),
        )

    }
}

@Composable
fun LogHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(12.dp, 16.dp)
    ) {
        Text(
            fontWeight = FontWeight.W900,
            color = MaterialTheme.colorScheme.secondary,
            text = stringResource(id = R.string.log),
        )
    }
}

@Composable
fun LogItem(
    item: GameLogItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(12.dp, 0.dp, 12.dp, 16.dp)

    ) {

        Text(
            fontSize = 12.sp,
            text = "${item.getData()}\n${item.getTime()}",
            color = item.getColor(),
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = item.info,
            color = item.getColor(),
        )
    }
}

@Composable
fun Warehouse(
    gameInfo: GameInfo,
    btnEnable: Boolean,
    onOcr: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onOcr()
            },
        ) {
            Text(text = stringResource(id = R.string.click_to_ocr))
        }
    }

}

