package com.heyanle.closure.page.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.heyanle.closure.R
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.page.main.MainViewModel
import com.heyanle.closure.utils.stringRes

/**
 * Created by HeYanLe on 2022/12/28 20:34.
 * https://github.com/heyanLE
 */

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Home(
  gameResp: GameResp
){


    Box(modifier = Modifier.fillMaxSize()){
        Text(text = "home")
    }
}