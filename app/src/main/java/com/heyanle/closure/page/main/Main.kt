package com.heyanle.closure.page.main

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.More
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.heyanle.closure.R
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.page.game_instance.Instance
import com.heyanle.closure.page.home.Home
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.HomePagerLayout
import com.heyanle.closure.ui.HomeTab
import com.heyanle.closure.utils.stringRes
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2022/12/23 20:29.
 * https://github.com/heyanLE
 */
@OptIn(ExperimentalPagerApi::class)
val mainPagerInfo: List<PageInfo> by lazy {
    listOf<PageInfo>(
        PageInfo(
            stringRes(R.string.instance),
        ){ it, vm ->
            Instance(it, vm)
        },
        PageInfo(
            stringRes(R.string.home)
        ){ it, vm ->
            Home(it, vm)
        }
    )
}
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Main(){
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val viewModel = viewModel<MainViewModel>()
    val currentGameResp by viewModel.currentGameInstance.observeAsState()
    val isGameInstancePageShow by viewModel.isGameInstancePageShow.observeAsState(false)

    val avatarImage by viewModel.avatarImage.observeAsState(R.drawable.logo)
    val topBarTitle by viewModel.topBarTitle.observeAsState(stringRes(R.string.please_choose_instance))

    LaunchedEffect(isGameInstancePageShow, currentGameResp){
        if((isGameInstancePageShow || currentGameResp == null)){
            if(pagerState.currentPage != 0){
                viewModel.topBarTitle.value = stringRes(R.string.please_choose_instance)
                pagerState.animateScrollToPage(0)
            }

        }else if(pagerState.currentPage != 1){
            pagerState.animateScrollToPage(1)
        }
    }
    Box(
        modifier = Modifier
            .background(ColorScheme.background)
    ) {
        Column {
            TopAppBar(
                title = {
                    Text(text = topBarTitle)
                },
                navigationIcon = {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(avatarImage).build(),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier.height(40.dp)
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = ColorScheme.primary
                ),
                actions = {
                    Button(onClick = {
                        viewModel.isGameInstancePageShow.value = true
                    }) {
                        Text(text = stringResource(id = R.string.change_instance))
                    }
                }
            )

           
            VerticalPager(
                count = mainPagerInfo.size,
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                userScrollEnabled = false,
            ) {
                mainPagerInfo[it].content(pagerState, viewModel)
            }

        }
    }
}

class PageInfo @OptIn(ExperimentalPagerApi::class) constructor(
    val label: String,
    val content: @Composable (PagerState, MainViewModel)->Unit,
)