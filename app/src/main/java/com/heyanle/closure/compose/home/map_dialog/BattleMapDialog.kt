package com.heyanle.closure.compose.home.map_dialog

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.R
import com.heyanle.closure.base.theme.LocalThemeState
import com.heyanle.closure.closure.items.ItemsController
import com.heyanle.closure.closure.stage.Stage
import com.heyanle.closure.closure.stage.StageController
import com.heyanle.closure.compose.common.OKImage
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toast
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.launch
import java.util.Collections

/**
 * Created by HeYanLe on 2023/1/2 19:50.
 * https://github.com/heyanLE
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleMapDialog(
    enable: Boolean,
    def: List<String>,
    onConfirm: (List<Stage>) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val vm = viewModel<BattleMapViewModel>()
    val stageController: StageController by Injekt.injectLazy()

    val allList = stageController.map.collectAsState()
    val current = remember(def, allList) {
        mutableStateListOf<Stage>().apply {
            addAll(def.flatMap {
                val sta = allList.value[it]
                if(sta == null){
                    emptyList()
                }else{
                    listOf(sta)
                }
            })
        }
    }
    val key = vm.keyword.collectAsState()
    if (enable) {
        AlertDialog(
            modifier = Modifier,
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    onClick = {
                        onConfirm(current.toList())
                        onDismissRequest()
                    },
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    onClick = {
                        onDismissRequest()
                    },
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            text = {
                Box(
                    modifier = Modifier
                        .height(500.dp)
                        .fillMaxWidth()
                ) {


                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 搜索框
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = key.value,
                            onValueChange = { vm.keyword.value = it },
                            placeholder = { Text(text = stringResource(id = R.string.search)) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    stringResource(id = R.string.search)
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = if(LocalThemeState.current.isDark()){MaterialTheme.colorScheme.background} else MaterialTheme.colorScheme.secondaryContainer,
                                focusedContainerColor = if(LocalThemeState.current.isDark()){MaterialTheme.colorScheme.background} else MaterialTheme.colorScheme.secondaryContainer,
                                focusedLabelColor = MaterialTheme.colorScheme.secondary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                                cursorColor = MaterialTheme.colorScheme.secondary,
                                selectionColors = TextSelectionColors(
                                    handleColor = MaterialTheme.colorScheme.secondary,
                                    backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                                ),
                            )
                        )

                        Spacer(modifier = Modifier.size(8.dp))

                        // 关卡配置
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            item {
                                Text(text = stringResource(id = R.string.battle_list))
                            }

                            items(current.size) { i ->
                                val it = current[i]

                                Row(modifier = Modifier
                                    .clip(
                                        CircleShape
                                    )
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .clickable {
                                        current.remove(it)
                                    }
                                    .padding(8.dp, 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontWeight = FontWeight.W900,
                                        text = allList.value[it.id]?.code ?: it.id.toString(),
                                        fontSize = 12.sp,
                                    )
                                    Icon(modifier = Modifier.size(16.dp), imageVector = Icons.Filled.Close, tint = MaterialTheme.colorScheme.onSecondary,contentDescription = "")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.size(8.dp))

                        val res by vm.result.observeAsState(initial = emptyList())
                        // 结果
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(res) {

                                StageCard(stage = it) { stage ->
                                    if (current.contains(stage)) {
                                        current.remove(stage)
                                    } else {
                                        current.add(stage)
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

// 关卡卡片
@Composable
fun StageCard(
    stage: Stage,
    onClick: (Stage) -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(8.dp)
            )
            .padding(0.dp, 4.dp)
            .clickable {
                onClick(stage)
            }
            .background(if(LocalThemeState.current.isDark()){MaterialTheme.colorScheme.background} else MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row() {

                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stage.name)
                        Spacer(modifier = Modifier.size(16.dp))
                        OKImage(
                            modifier = Modifier
                                .size(24.dp),
                            image = ItemsController.AP_ICON_URL,
                            contentDescription = stringResource(R.string.ap)
                        )
                        Text(text = "-${stage.ap}")
                    }
                }


                Box(
                    modifier = Modifier
                        .clip(
                            CircleShape
                        )
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(8.dp, 4.dp)
                ) {
                    Text(
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.W900,
                        text = stage.code,
                        fontSize = 12.sp,
                    )
                }
            }

            val itemsController: ItemsController by Injekt.injectLazy()
            val allItems by itemsController.map.collectAsState()
            Log.d("BattleMapDialog", allItems.size.toString())
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {


                items(stage.items) {
                    if (allItems.containsKey(it)) {
                        OKImage(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(1.dp, 0.dp),
                            image = allItems[it]?.getIconUrl() ?: "", contentDescription = it
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(1.dp, 0.dp),
                            text = it
                        )
                    }
                }
            }
        }
    }
}