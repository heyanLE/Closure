package com.heyanle.closure.ui.home.instance_manage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heyanle.closure.R
import com.heyanle.closure.closure.assets.AssetsController
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.ui.common.LoadingImage
import com.heyanle.closure.ui.home.HomeViewModel
import com.heyanle.injekt.core.Injekt

/**
 * Created by heyanlin on 2024/2/4 14:42.
 */
@Composable
fun InstanceManager(
    homeViewModel: HomeViewModel,
) {

    val webListData = homeViewModel.webGameList.collectAsState()
    val w = webListData.value

    if(w.isLoading){

    }else if(w.isError || w.data == null){

    }else{
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ){
            items(w.data){
                InstanceCard(
                    webGame = it,
                    onClick = {},
                    onOpen = {},
                    onPause = {},
                    onDelete = {},
                    onCaptcha = {},
                    onConfig = {}
                )
            }
        }
    }
}


@Composable
fun InstanceCard(
    webGame: WebGame,
    onClick: (WebGame) -> Unit,
    onOpen: (WebGame) -> Unit,
    onPause: (WebGame) -> Unit,
    onDelete: (WebGame) -> Unit,
    onCaptcha: (WebGame) -> Unit,
    onConfig: (WebGame) -> Unit,
){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            .clickable {
                onClick(webGame)
            }
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AccountCard(
                account = webGame.status.account,
                platform = webGame.status.platform.toLong(),
                accountColor = MaterialTheme.colorScheme.secondary,
            )
            DoubleText(
                startText = stringResource(id = com.heyanle.i18n.R.string.status),
                endText = webGame.status.text
            )

            val assets: AssetsController by Injekt.injectLazy()
            val map = assets.stageMap.collectAsState()
            webGame.gameSetting.battleMaps
            val stage = map.value.getOrDefault(webGame.gameSetting.battleMaps.getOrNull(0) ?: "", null)
            val text = if (stage == null) {
                webGame.gameSetting.battleMaps.getOrNull(0) ?: ""
            } else {
                "${stage.code} ${stage.name}"
            }
            DoubleText(
                startText = stringResource(id = com.heyanle.i18n.R.string.map),
                endText = text
            )
            DoubleText(
                startText = stringResource(id = com.heyanle.i18n.R.string.keep_ap),
                endText = webGame.gameSetting.keepingAP.toString()
            )
            DoubleText(
                startText = stringResource(id = com.heyanle.i18n.R.string.building_arrange),
                endText = if (webGame.gameSetting.enableBuildingArrange) stringResource(id = com.heyanle.i18n.R.string.enable) else stringResource(
                    id = com.heyanle.i18n.R.string.unable
                ),
            )
            InstanceAction(
                webGame = webGame,
                onCaptcha = { onCaptcha(webGame) },
                onStart = { onOpen(webGame) },
                onPause = { onPause(webGame) },
                onDelete = {
                    onDelete(webGame)
                })

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onConfig(webGame)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
            ) {
                Text(text = stringResource(id = com.heyanle.i18n.R.string.instance_config))
            }

        }

        // 游戏启动中蒙层
        if (webGame.status.code == 1) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xB3000000))
                    .clickable {

                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LoadingImage()
                    // LoadingIcon()
                    Spacer(modifier = Modifier.size(16.dp))
                    Column() {
                        Text(text = stringResource(id = com.heyanle.i18n.R.string.game_logging))
                        Text(text = stringResource(id = com.heyanle.i18n.R.string.it_take_five_minute))
                    }
                }
            }

            TextButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp, 0.dp, 16.dp, 16.dp)
                    .fillMaxWidth(),
                onClick = {
                    onConfig(webGame)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
            ) {
                Text(text = stringResource(id = com.heyanle.i18n.R.string.instance_config))
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
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val start = 0.coerceAtLeast(4.coerceAtMost(account.length - 1))
        val end = 8.coerceAtMost(account.length)
        val sb = StringBuilder()
        for (i in 1..end - start) {
            sb.append("*")
        }

        Text(
            fontWeight = FontWeight.W900,
            color = accountColor,
            text = "${stringResource(id = com.heyanle.i18n.R.string.account)}: ${
                account.replaceRange(
                    start,
                    end,
                    sb.toString()
                )
            }",
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
                text =
                if (platform < 2)
                    stringResource(id = com.heyanle.i18n.R.string.official_server)
                else
                    stringResource(id = com.heyanle.i18n.R.string.bilibili_server),
                fontSize = 12.sp,
            )
        }


    }
}

@Composable
fun InstanceAction(
    webGame: WebGame,
    onCaptcha: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onDelete: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    onDelete()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
            ) {
                Text(text = stringResource(id = com.heyanle.i18n.R.string.delete_instance))
            }
            Spacer(modifier = Modifier.size(16.dp))
            TextButton(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.weight(1f),
                onClick = {
                    when (webGame.status.code) {
                        999 -> onCaptcha()
                        2 -> onPause()
                        else -> onStart()
                    }
                },
            ) {
                val text = when (webGame.status.code) {
                    999 -> stringResource(id = com.heyanle.i18n.R.string.captcha)
                    2 -> stringResource(id = com.heyanle.i18n.R.string.pause_game)
                    else -> stringResource(id = com.heyanle.i18n.R.string.login_game)
                }
                Text(text = text)
            }


        }


    }

}

@Composable
fun DoubleText(
    startText: String,
    endText: String,
) {
    Box(
        Modifier
            .fillMaxWidth(),
    ) {
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