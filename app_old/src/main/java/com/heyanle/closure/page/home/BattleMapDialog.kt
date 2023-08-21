package com.heyanle.closure.page.home

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.heyanle.closure.model.ItemModel
import com.heyanle.closure.model.StageModel
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage
import com.heyanle.closure.ui.LoadingPage
import com.heyanle.closure.ui.OKImage
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toast
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
    rootVM: AutoSettingViewModel,
    onDismissRequest: ()->Unit,
) {
    val scope = rememberCoroutineScope()
    val vm = viewModel<BattleMapViewModel>()


    if(enable) {
        AlertDialog(
            modifier = Modifier,
            containerColor = ColorScheme.background,
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorScheme.secondary,
                        contentColor = ColorScheme.onSecondary
                    ),
                    onClick = {
                        onDismissRequest()
                    },
                    enabled = !rootVM.isLoading.value && !rootVM.isError.value,
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            text = {
                Box(modifier = Modifier
                    .height(500.dp)
                    .fillMaxWidth()){
                    val isLoading by StageModel.isLoading.observeAsState(initial = false)
                    val isError by StageModel.isLoading.observeAsState(initial = false)
                    val errorMsg by StageModel.errorMsg.observeAsState(initial = stringResource(id = R.string.net_error))
                    if(isLoading){
                        LoadingPage()
                    }else if(isError){
                        ErrorPage(
                            modifier = Modifier.fillMaxSize(),
                            errorMsg = errorMsg,
                            clickEnable = true,
                            onClick = {
                                StageModel.refresh()
                            },
                            other = {
                                Text(text = stringResource(id = R.string.click_to_retry))
                            }
                        )
                    }else{
                        val allList by StageModel.mapLiveData.observeAsState(initial = mapOf())
                        LaunchedEffect(key1 = Unit){
                            val re = arrayListOf<String>()
                            re.addAll(rootVM.battleMap)
                            vm.init(allList)
                        }

                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // 搜索框
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = vm.keyword.value,
                                onValueChange = { vm.keyword.value = it },
                                placeholder = { Text(text = stringResource(id = R.string.search)) },
                                singleLine = true,
                                leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    stringResource(id = R.string.search)
                                )
                                },
                                trailingIcon = {
                                    TextButton(onClick = {
                                        if(vm.keyword.value.isEmpty()){
                                            stringRes(R.string.please_input_keyword).toast()
                                        }else{
                                            scope.launch {
                                                vm.refresh(allList)
                                            }
                                        }
                                    }) {
                                        Text(text = stringResource(id = R.string.search), color = ColorScheme.onSurface)
                                    }
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = ColorScheme.surface,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    errorIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    textColor = ColorScheme.secondary,
                                    cursorColor = ColorScheme.secondary,
                                    focusedLabelColor = ColorScheme.secondary,
                                )
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            // 关卡配置
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                item { 
                                    Text(text = stringResource(id = R.string.battle_list))
                                }
                                val list = rootVM.battleMap
                                items(list.size){i ->
                                    val it = list[i]
                                    Box(modifier = Modifier
                                        .clip(
                                            CircleShape
                                        )
                                        .background(ColorScheme.secondary)
                                        .clickable {
                                            rootVM.battleMap.removeAt(i)
                                        }
                                        .padding(8.dp, 4.dp)
                                    ){
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            color = ColorScheme.onSecondary,
                                            fontWeight = FontWeight.W900,
                                            text = allList[it]?.code?:it,
                                            fontSize = 12.sp,
                                        )
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
                            ){
                                items(res){

                                    StageCard(stage = it){ stage ->
                                        val index = rootVM.battleMap.indexOf(stage.id)
                                        if(index == -1){
                                            rootVM.battleMap.add(stage.id)
                                        }else{
                                            rootVM.battleMap.removeAt(index)
                                        }
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
    stage: StageModel.Stage,
    onClick: (StageModel.Stage)->Unit,
){
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(8.dp)
            )
            .padding(0.dp, 4.dp)
            .clickable {
                onClick(stage)
            }
            .background(ColorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Row() {

                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stage.name)
                        Spacer(modifier = Modifier.size(16.dp))
                        OKImage( modifier = Modifier
                            .size(24.dp),image = ItemModel.AP_ICON_URL, contentDescription = stringResource(R.string.ap))
                        Text(text = "-${stage.ap}")
                    }
                }


                Box(modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .background(ColorScheme.secondary)
                    .padding(8.dp, 4.dp)){
                    Text(
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        color = ColorScheme.onSecondary,
                        fontWeight = FontWeight.W900,
                        text = stage.code,
                        fontSize = 12.sp,
                    )
                }
            }

            val allItems by ItemModel.mapLiveData.observeAsState(initial = mapOf())
            Log.d("BattleMapDialog", allItems.size.toString())
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ){


                items(stage.items){
                    if(allItems.containsKey(it)){
                        OKImage(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(1.dp, 0.dp),
                            image = allItems[it]?.getIconUrl()?:"", contentDescription = it)
                    }else{
                        Text(modifier = Modifier
                            .size(24.dp)
                            .padding(1.dp, 0.dp),
                            text = it)
                    }
                }
            }
        }
    }
}