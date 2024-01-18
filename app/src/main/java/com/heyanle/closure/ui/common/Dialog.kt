package com.heyanle.closure.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import com.heyanle.closure.R

/**
 * Created by heyanlin on 2024/1/18 15:39.
 */



@Composable
fun ProgressDialog(){
    AlertDialog(
        title = { Text(stringResource(id = com.heyanle.i18n.R.string.loading)) },
        icon = { LoadingImage() },
        onDismissRequest = {},
        confirmButton = {})
}