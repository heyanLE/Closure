package com.heyanle.closure.compose.home.manager

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heyanle.closure.R
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.closure.stage.StageController
import com.heyanle.closure.compose.common.ErrorPage
import com.heyanle.closure.compose.common.LoadingIcon
import com.heyanle.closure.compose.common.LoadingPage
import com.heyanle.closure.net.model.GameResp
import com.heyanle.injekt.core.Injekt

/**
 * Created by HeYanLe on 2023/8/20 15:49.
 * https://github.com/heyanLE
 */
@Composable
fun Manager(
    gameRespList: List<GameResp>,
    onAddInstance: () -> Unit,
    onClick: (GameResp) -> Unit,
    onOpen: (GameResp) -> Unit,
    onPause: (GameResp) -> Unit,
    onDelete: (GameResp) -> Unit,
    onCaptcha: (GameResp) -> Unit,
    onConfig: (GameResp) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable {
                            onAddInstance()
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {

                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_instance)
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(text = stringResource(id = R.string.add_instance))
                }

            }
            items(gameRespList) {
                GameInstanceCard(
                    gameResp = it,
                    onClick = onClick,
                    onOpen = onOpen,
                    onPause = onPause,
                    onDelete = onDelete,
                    onCaptcha = onCaptcha,
                    onConfig = onConfig,
                )
            }
        }
    }


}

@Composable
fun GameInstanceCard(
    gameResp: GameResp,
    onClick: (GameResp) -> Unit,
    onOpen: (GameResp) -> Unit,
    onPause: (GameResp) -> Unit,
    onDelete: (GameResp) -> Unit,
    onCaptcha: (GameResp) -> Unit,
    onConfig: (GameResp) -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable {
                onClick(gameResp)
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
                account = gameResp.config.account,
                platform = gameResp.config.platform,
                accountColor = MaterialTheme.colorScheme.secondary,
            )
            DoubleText(
                startText = stringResource(id = R.string.status),
                endText = gameResp.status.text
            )

            val battle: StageController by Injekt.injectLazy()
            val map = battle.map.collectAsState()
            val stage = map.value.getOrDefault(gameResp.gameConfig.battleMaps?.get(0) ?: "", null)
            val text = if (stage == null) {
                gameResp.gameConfig.battleMaps?.get(0) ?: ""
            } else {
                "${stage.code} ${stage.name}"
            }
            DoubleText(
                startText = stringResource(id = R.string.map),
                endText = text
            )
            DoubleText(
                startText = stringResource(id = R.string.keep_ap),
                endText = gameResp.gameConfig.keepingAP.toString()
            )
            DoubleText(
                startText = stringResource(id = R.string.building_arrange),
                endText = if (gameResp.gameConfig.enableBuildingArrange) stringResource(id = R.string.enable) else stringResource(
                    id = R.string.unable
                ),
            )
            InstanceAction(
                gameResp = gameResp,
                onCaptcha = { onCaptcha(gameResp) },
                onLogin = { onOpen(gameResp) },
                onPause = { onPause(gameResp) },
                onDelete = {
                    onDelete(gameResp)
                })

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onConfig(gameResp)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
            ) {
                Text(text = stringResource(id = R.string.instance_config))
            }

        }

        // 游戏启动中蒙层
        if (gameResp.status.code == 1) {
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
                    LoadingIcon()
                    Spacer(modifier = Modifier.size(16.dp))
                    Column() {
                        Text(text = stringResource(id = R.string.game_logging))
                        Text(text = stringResource(id = R.string.it_take_five_minute))
                        Text(text = stringResource(id = R.string.please_refresh))
                    }
                }
            }

            TextButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp, 0.dp, 16.dp, 16.dp)
                    .fillMaxWidth(),
                onClick = {
                    onConfig(gameResp)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
            ) {
                Text(text = stringResource(id = R.string.instance_config))
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
    ) {
        val start = 0.coerceAtLeast(3.coerceAtMost(account.length - 1))
        val end = 7.coerceAtMost(account.length)
        val sb = StringBuilder()
        for (i in 1..end - start) {
            sb.append("*")
        }

        Text(
            fontWeight = FontWeight.W900,
            color = accountColor,
            text = "${stringResource(id = R.string.account)}: ${
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
                .padding(8.dp, 0.dp)
        ) {
            Text(
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.W900,
                text =
                if (platform < 2)
                    stringResource(id = R.string.official_server)
                else
                    stringResource(id = R.string.bilibili_server),
                fontSize = 12.sp,
            )
        }


    }
}

@Composable
fun InstanceAction(
    gameResp: GameResp,
    onCaptcha: () -> Unit,
    onLogin: () -> Unit,
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
                Text(text = stringResource(id = R.string.delete_instance))
            }
            Spacer(modifier = Modifier.size(16.dp))
            TextButton(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.weight(1f),
                onClick = {
                    when (gameResp.status.code) {
                        999 -> onCaptcha()
                        2 -> onPause()
                        else -> onLogin()
                    }
                },
            ) {
                val text = when (gameResp.status.code) {
                    999 -> stringResource(id = R.string.captcha)
                    2 -> stringResource(id = R.string.pause_game)
                    else -> stringResource(id = R.string.login_game)
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