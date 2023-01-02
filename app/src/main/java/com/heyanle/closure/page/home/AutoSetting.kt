package com.heyanle.closure.page.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.R
import com.heyanle.closure.model.StageModel
import com.heyanle.closure.net.model.CreateGameReq
import com.heyanle.closure.net.model.GameConfig
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage
import com.heyanle.closure.ui.LoadingPage
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2023/1/2 17:05.
 * https://github.com/heyanLE
 */

// 脱管配置 dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoSettingDialog(
    enable: Boolean,
    onDismissRequest: ()->Unit,
    onSave: (GameConfig)->Unit,
){

    var enableBattleMapDialog by remember {
        mutableStateOf(false)
    }

    val vm = viewModel<AutoSettingViewModel>()
    BattleMapDialog(
        enableBattleMapDialog,
        onDismissRequest = {
            enableBattleMapDialog = false
        },
        rootVM = vm
    )




    val scope = rememberCoroutineScope()
    if(enable){


        AlertDialog(
            modifier = Modifier,
            containerColor = ColorScheme.background,
            onDismissRequest = onDismissRequest,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = stringResource(id = R.string.auto_setting)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = stringResource(id = R.string.auto_setting))
                }
            },
            confirmButton = {
                TextButton(

                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorScheme.secondary,
                        contentColor = ColorScheme.onSecondary
                    ),
                    onClick = {
                        vm.newConfig()?.let(onSave)
                    },
                    enabled = !vm.isLoading.value && !vm.isError.value,
                ) {
                    Text(text = stringResource(id = R.string.save))
                }
            },
            dismissButton = {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorScheme.background,
                        contentColor = ColorScheme.onSurface
                    ),
                    onClick = {
                        onDismissRequest()
                    }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            text = {
                Box(modifier = Modifier.height(300.dp)){
                    if(vm.isLoading.value){
                        LoadingPage(modifier = Modifier.fillMaxSize())
                    }else if(vm.isError.value){
                        ErrorPage(
                            modifier = Modifier.fillMaxSize(),
                            errorMsg = vm.errorMsg.value,
                            clickEnable = true,
                            onClick = {
                                scope.launch {
                                    vm.refresh()
                                }
                            },
                            other = {
                                Text(text = stringResource(id = R.string.click_to_retry))
                            }
                        )
                    }else{
                        var keepingAPString by remember {
                            mutableStateOf(vm.keepingAP.value.toString())
                        }
                        var recruitReserveString by remember {
                            mutableStateOf(vm.recruitReserve.value.toString())
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ){

                            // 理智保留
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                value = keepingAPString,
                                onValueChange = {
                                    keepingAPString =  it
                                    kotlin.runCatching {
                                        vm.keepingAP.value = it.toInt()
                                    }.onFailure {
                                        vm.keepingAP.value = 0
                                    }
                                },
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        kotlin.runCatching {
                                            vm.keepingAP.value = keepingAPString.toInt()
                                            keepingAPString = vm.keepingAP.value.toString()
                                        }.onFailure {
                                            vm.keepingAP.value = 0
                                            keepingAPString = vm.keepingAP.value.toString()

                                        }
                                    },
                                    onDone = {
                                        kotlin.runCatching {
                                            vm.recruitReserve.value = recruitReserveString.toInt()
                                            recruitReserveString = vm.recruitReserve.value.toString()
                                        }.onFailure {
                                            vm.recruitReserve.value = 0
                                            recruitReserveString = vm.recruitReserve.value.toString()

                                        }
                                    }
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = {},
                                singleLine = true,
                                label = {
                                    Text(
                                        text = stringResource(id = R.string.keep_ap),
                                    )
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    errorIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    containerColor = ColorScheme.surface,
                                    textColor = ColorScheme.onSurface,
                                    cursorColor = ColorScheme.secondary,
                                    focusedLabelColor = ColorScheme.secondary
                                )
                            )

                            // 公招券保留
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                value = recruitReserveString,
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        kotlin.runCatching {
                                            vm.recruitReserve.value = recruitReserveString.toInt()
                                            recruitReserveString = vm.recruitReserve.value.toString()
                                        }.onFailure {
                                            vm.recruitReserve.value = 0
                                            recruitReserveString = vm.recruitReserve.value.toString()

                                        }
                                    },
                                    onDone = {
                                        kotlin.runCatching {
                                            vm.recruitReserve.value = recruitReserveString.toInt()
                                            recruitReserveString = vm.recruitReserve.value.toString()
                                        }.onFailure {
                                            vm.recruitReserve.value = 0
                                            recruitReserveString = vm.recruitReserve.value.toString()
                                        }
                                    }
                                ),
                                onValueChange = {
                                    recruitReserveString =  it
                                    kotlin.runCatching {
                                        vm.recruitReserve.value = it.toInt()
                                    }.onFailure {
                                        vm.recruitReserve.value = 0
                                    }

                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = {},
                                singleLine = true,
                                label = {
                                    Text(modifier = Modifier,text = stringResource(id = R.string.recruit_reserve))
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = ColorScheme.surface,
                                    textColor = ColorScheme.onSurface,
                                    cursorColor = ColorScheme.secondary,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    errorIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    focusedLabelColor = ColorScheme.secondary
                                )
                            )


                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 基建排班
                                Row (
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Text(text = stringResource(id = R.string.building_arrange))
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Switch(checked = vm.enableBuildingArrange.value, onCheckedChange = {vm.enableBuildingArrange.value = it},
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = ColorScheme.secondary,
                                            uncheckedTrackColor = ColorScheme.primary,
                                            uncheckedBorderColor = Color.Transparent
                                        )
                                    )
                                }

                                // 招募机械
                                Row (
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Text(text = stringResource(id = R.string.recruit_ignore_robot))
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Switch(checked = vm.recruitIgnoreRobot.value, onCheckedChange = {vm.recruitIgnoreRobot.value = it},
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = ColorScheme.secondary,
                                            uncheckedTrackColor = ColorScheme.primary,
                                            uncheckedBorderColor = Color.Transparent
                                        )
                                    )
                                }
                            }



                            val map by StageModel.mapLiveData.observeAsState(initial = mapOf())

                            Row (
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(modifier = Modifier.padding(0.dp, 4.dp),text = stringResource(id = R.string.battle_list),fontWeight = FontWeight.W900,)
                                TextButton(
                                    onClick = {
                                        enableBattleMapDialog = true
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = ColorScheme.onSecondary,
                                        containerColor = ColorScheme.secondary
                                    ),
                                ) {
                                    Text(text = stringResource(id = R.string.click_to_config))
                                }


                            }
                            // 关卡配置
                            LazyVerticalGrid(
                                modifier = Modifier.fillMaxWidth(),
                                columns = GridCells.Adaptive(48.dp)
                            ){
                                items(vm.battleMap){
                                    Box(modifier = Modifier
                                        .clip(
                                            CircleShape
                                        )
                                        .background(ColorScheme.secondary)
                                        .padding(8.dp, 4.dp)){
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            color = ColorScheme.onSecondary,
                                            fontWeight = FontWeight.W900,
                                            text = map[it]?.code?:it,
                                            fontSize = 12.sp,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

