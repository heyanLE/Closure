package com.heyanle.closure.compose.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heyanle.closure.R

/**
 * Created by HeYanLe on 2023/8/20 13:28.
 * https://github.com/heyanLE
 */
@Composable
fun RegisterDialog(
    show: MutableState<Boolean>,
    email: String,
    password: String,
    onCancel: ()->Unit,
    onConfirm: ()->Unit,
){
    if(show.value){
        AlertDialog(
            title = {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){

                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(id = R.drawable.fiammetta),
                        contentDescription = stringResource(id = R.string.register)
                    )
                    Spacer(modifier = Modifier.size(4.dp))

                    Text(stringResource(id = R.string.register))
                }
            },
            text = {
                val text = "${stringResource(id = R.string.email)} ${email}\n${stringResource(id = R.string.password)} ${password}\n${
                    stringResource(
                    id = R.string.ask_register
                )
                }"
                Text(text) },
            onDismissRequest = {show.value = false},
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    onClick = {
                        onConfirm()
                    }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onCancel()
                    }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun ErrorDialog(
    show: MutableState<Boolean>,
    msg: MutableState<String>,
){
    if(show.value){
        AlertDialog(
            title = {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    ErrorIcon(Modifier.size(32.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(stringResource(id = R.string.error))
                }
            },
            text = {  Text(msg.value) },
            onDismissRequest = {show.value = false},
            confirmButton = {})
    }

}

@Composable
fun ProgressDialog(
    show: MutableState<Boolean> = mutableStateOf(true)
){
    if(show.value){
       ProgressDialog()
    }
}

@Composable
fun ProgressDialog(){
    AlertDialog(
        title = { Text(stringResource(id = R.string.loading)) },
        icon = { LoadingIcon() },
        onDismissRequest = {},
        confirmButton = {})
}