package com.heyanle.closure.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset

/**
 * Created by HeYanLe on 2022/12/28 20:32.
 * https://github.com/heyanLE
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeTab(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    count: Int,
    label: @Composable (Int, Boolean)->Unit,
){
    ScrollableTabRow(
        divider = {},
        modifier = modifier.height(60.dp),
        selectedTabIndex = pagerState.currentPage) {
        repeat(count){
            label(it, pagerState.currentPage == it)
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomePagerLayout(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    count: Int,
    pager: @Composable PagerScope.(Int) -> Unit
){
    HorizontalPager(
        count = count,
        state = pagerState,
        modifier = modifier,
        content = pager
    )
}