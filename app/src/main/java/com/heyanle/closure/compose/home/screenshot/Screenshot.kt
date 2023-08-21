package com.heyanle.closure.compose.home.screenshot

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.R
import com.heyanle.closure.net.model.GameResp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.heyanle.closure.compose.common.ErrorPage
import com.heyanle.closure.compose.common.LoadingPage
import com.heyanle.closure.compose.common.OKImage
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2023/1/1 14:06.
 * https://github.com/heyanLE
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenshotDialog(
    enable: Boolean,
    onDismissRequest: () -> Unit,
    select: GameResp,
) {

    val vm = viewModel<ScreenshotViewModel>()

    val scope = rememberCoroutineScope()
    LaunchedEffect(select, enable) {
        if (enable) {
            vm.refresh(select.config.account, select.config.platform)
        }
    }

    if (enable) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth(),
            onDismissRequest = {
                onDismissRequest()
            },
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    onClick = {
                        onDismissRequest()
                    }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            title = {
                Text(text = stringResource(id = R.string.screenshot))
            },
            text = {
                if (vm.isLoading.value) {
                    LoadingPage(
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (vm.errorMsg.value.isNotEmpty()) {
                    ErrorPage(
                        modifier = Modifier.fillMaxWidth(),
                        errorMsg = vm.errorMsg.value,
                        clickEnable = true,
                        onClick = {
                            scope.launch {
                                vm.refresh(select.config.account, select.config.platform)
                            }
                        }
                    )
                } else {
                    val pagerState = rememberPagerState {
                        vm.screenshot.size
                    }
                    HorizontalPager(state = pagerState) { i ->
                        OKImage(
                            modifier = Modifier.fillMaxWidth(),
                            image = vm.screenshot[i],
                            contentDescription = stringResource(id = R.string.screenshot)
                        )
                    }
                }

            }
        )
    }


}