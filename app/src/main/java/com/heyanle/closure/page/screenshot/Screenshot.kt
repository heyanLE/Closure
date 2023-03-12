package com.heyanle.closure.page.screenshot

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.R
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.net.model.GetGameResp
import com.heyanle.closure.page.MainController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorPage
import com.heyanle.closure.ui.LoadingPage
import com.heyanle.closure.ui.OKImage
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.days

/**
 * Created by HeYanLe on 2023/1/1 14:06.
 * https://github.com/heyanLE
 */

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScreenshotDialog(
    enable: Boolean,
    onDismissRequest: ()->Unit,
    select: MainController.InstanceSelect,
) {

    val vm = viewModel<ScreenshotViewModel>()

    val scope = rememberCoroutineScope()
    val token = MainController.token.value?:""
    LaunchedEffect(select, enable){
        if(enable){
            vm.refresh(token, select.account, select.platform)
        }
    }

    if(enable){
        AlertDialog(
            modifier = Modifier.fillMaxWidth().height(320.dp),

            containerColor = ColorScheme.primary,
            titleContentColor = ColorScheme.onPrimary,
            onDismissRequest = {
                onDismissRequest()
            },
            confirmButton = {
                Button(
                    onClick = { onDismissRequest()
                    }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            title = {
                Text(text = stringResource(id = R.string.screenshot))
            },
            text = {
                val status by vm.screenshot.observeAsState(MainController.StatusData.None())
                status.onLoading {
                    LoadingPage(
                        modifier = Modifier.fillMaxWidth()
                    )
                }.onError {
                    ErrorPage(
                        modifier = Modifier.fillMaxWidth(),
                        errorMsg = it.errorMsg,
                        clickEnable = true,
                        onClick = {
                            scope.launch {
                                vm.refresh(token, select.account, select.platform)
                            }
                        }
                    )
                }.onData {

                    val list = vm.getUrl(it.data)
                    HorizontalPager(count = list.size) {i ->
                        OKImage(
                            modifier = Modifier.fillMaxWidth(),
                            image = list[i],
                            contentDescription = stringResource(id = R.string.screenshot))
                    }
                }
            }
        )
    }



}