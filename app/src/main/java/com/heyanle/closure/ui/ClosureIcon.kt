package com.heyanle.closure.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heyanle.closure.R

/**
 * Created by HeYanLe on 2022/12/28 18:40.
 * https://github.com/heyanLE
 */

@Composable
fun LoadingIcon(
    modifier: Modifier = Modifier
){
    Image(
        modifier = Modifier.size(40.dp).then(modifier),
        painter = painterResource(id = R.drawable.loading),
        contentDescription = stringResource(id = R.string.loading))
}

@Composable
fun ErrorIcon(
    modifier: Modifier = Modifier
){
    Image(
        modifier = Modifier.size(40.dp).then(modifier),
        painter = painterResource(id = R.drawable.kaltsit),
        contentDescription = stringResource(id = R.string.loading))
}