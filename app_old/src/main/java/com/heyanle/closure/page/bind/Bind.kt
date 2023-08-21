package com.heyanle.closure.page.bind

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.LocalNavController
import com.heyanle.closure.R
import com.heyanle.closure.net.Net
import com.heyanle.closure.page.warehouse.WarehouseTopAppbar
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage
import com.heyanle.closure.ui.FastScrollToTopFab
import com.heyanle.closure.ui.LoadingPage
import com.heyanle.closure.ui.OKImage
import com.heyanle.closure.utils.TimeUtils
import com.heyanle.closure.utils.openUrl
import com.heyanle.closure.utils.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2023/3/12 15:37.
 * https://github.com/heyanLE
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BindQQ() {

    val lazyListState = rememberLazyListState()
    val nav = LocalNavController.current
    val vm = viewModel<BindQQViewModel>()

    val manager: ClipboardManager = LocalClipboardManager.current

    val act = LocalContext.current

    LaunchedEffect(key1 = vm.state) {
        if (vm.state == BindQQViewModel.BindQQState.None) {
            vm.refresh()
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = ColorScheme.background, topBar = {
        BindQQTopAppbar(onRefresh = {
            vm.refresh()
        }, onBack = {
            nav.popBackStack()
        })

    }, floatingActionButton = {
        FastScrollToTopFab(listState = lazyListState)
    }) { padding ->
        AnimatedContent(
            modifier = Modifier.padding(padding),
            targetState = vm.state, transitionSpec = {
            fadeIn(
                animationSpec = tween(
                    300, delayMillis = 300
                )
            ) with fadeOut(animationSpec = tween(300, delayMillis = 0))
        }) {
            Column {
                when (it) {
                    is BindQQViewModel.BindQQState.None -> {

                    }

                    is BindQQViewModel.BindQQState.Loading -> {
                        LoadingPage(
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    is BindQQViewModel.BindQQState.Error -> {
                        ErrorPage(modifier = Modifier.fillMaxSize(),
                            errorMsg = it.errorMsg,
                            clickEnable = true,
                            onClick = {
                                vm.refresh()
                            },
                            other = {
                                Text(text = stringResource(id = R.string.click_to_retry))
                            })
                    }

                    is BindQQViewModel.BindQQState.Wait -> {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            UUIDCard(uuid = it.bindQQResponseWait.verifyCode, finishTime = it.bindQQResponseWait.expireTimestamp)
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(
                                modifier = Modifier.padding(16.dp, 0.dp),
                                text = "请将该【verifyCode:一段uuid码】发送给以下可露希尔官方QQ群"
                            )




                        }
                    }

                    is BindQQViewModel.BindQQState.Sus -> {
                        Text(modifier = Modifier.padding(16.dp),text = "绑定成功！" + it.qqCode)
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                QQGroupHeader()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ColorScheme.surface)
                        .padding(16.dp, 0.dp)
                ) {
                    QQGroup(groupCode = "450555868",
                        url = "https://jq.qq.com/?_wv=1027&k=FiJjOEe8",
                        onCopy = {
                            manager.setText(AnnotatedString(it))
                            "复制成功".toast()
                        },
                        onUrl = {
                            openUrl(act, it)
                        })
                    Spacer(modifier = Modifier.size(16.dp))
                    QQGroup(groupCode = "1345795",
                        url = "https://jq.qq.com/?_wv=1027&k=8C3DZiQM",
                        onCopy = {
                            manager.setText(AnnotatedString(it))
                            "复制成功".toast()
                        },
                        onUrl = {
                            openUrl(act, it)
                        })
                    Spacer(modifier = Modifier.size(16.dp))
                }

            }

        }
    }
}


@Composable
fun UUIDCard(
    uuid: String,
    finishTime: Long,
){
    val manager: ClipboardManager = LocalClipboardManager.current
    Column(
        modifier = Modifier
            .fillMaxWidth()

            .background(ColorScheme.surface)
            .clickable {
                manager.setText(AnnotatedString("verifyCode:${uuid}"))
                "复制成功".toast()
            }
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                fontWeight = FontWeight.W900,
                color = ColorScheme.secondary,
                text = "绑定代码",
            )
            Box(
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .background(ColorScheme.secondary)
                    .padding(8.dp, 4.dp)
            ) {
                Text(
                    color = ColorScheme.onSecondary,
                    fontWeight = FontWeight.W900,
                    text = "点击复制",
                    fontSize = 12.sp,
                )
            }

        }

        Text(
            fontWeight = FontWeight.W900,
            color = ColorScheme.secondary,
            text = "verifyCode:${uuid}",
        )

        TimeCard(finishTime = finishTime)
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimeCard(
    finishTime: Long,
) {

    Log.d("Bind", finishTime.toString())

    var showTime by remember(finishTime) {
        mutableStateOf(finishTime*1000 - System.currentTimeMillis())
    }

    LaunchedEffect(key1 = finishTime) {
        while (this.isActive) {
            delay(1000)
            showTime = finishTime*1000 - System.currentTimeMillis()
            if(showTime <= 0){
                break
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = showTime) {
            if (showTime <= 0) {
                Text(text = "已过期，请刷新后重新获取")
            } else {
                Text(text = "代码有效期：" + TimeUtils.toString(it))
            }
        }
    }
}

@Composable
fun QQGroupHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorScheme.surface)
            .padding(12.dp, 16.dp)
    ) {
        Text(
            fontWeight = FontWeight.W900,
            color = ColorScheme.secondary,
            text = "官方 QQ 群",
        )
    }

}
@Composable
fun QQGroup(
    groupCode: String,
    url: String?,
    onCopy: (String) -> Unit,
    onUrl: (String) -> Unit,
) {
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(text = groupCode)
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clip(
                    CircleShape
                )
                .background(ColorScheme.secondary)
                .clickable {
                    onCopy(groupCode)
                }
                .padding(8.dp, 4.dp)
        ) {
            Text(
                color = ColorScheme.onSecondary,
                fontWeight = FontWeight.W900,
                text = "点击复制",
                fontSize = 12.sp,
            )
        }


        url?.let {
            Spacer(modifier = Modifier.size(8.dp))
            Box(
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .background(ColorScheme.secondary)
                    .clickable {
                        onUrl(it)
                    }
                    .padding(8.dp, 4.dp)
            ) {
                Text(
                    color = ColorScheme.onSecondary,
                    fontWeight = FontWeight.W900,
                    text = "点击加群",
                    fontSize = 12.sp,
                )
            }
        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BindQQTopAppbar(
    onRefresh: () -> Unit,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(
                    id = R.string.bind_qq
                )
            )
        },
        navigationIcon = {
            Row() {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        Icons.Filled.ArrowBackIos,
                        contentDescription = stringResource(id = R.string.cancel)
                    )
                }
                OKImage(
                    modifier = Modifier.size(48.dp),
                    image = R.drawable.fiammetta,
                    contentDescription = stringResource(
                        id = R.string.bind_qq
                    )
                )
            }

        },
        actions = {
            IconButton(onClick = { onRefresh() }) {
                Icon(
                    Icons.Filled.Refresh, contentDescription = stringResource(id = R.string.refresh)
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = ColorScheme.primary
        ),
    )
}