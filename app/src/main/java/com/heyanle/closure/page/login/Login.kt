package com.heyanle.closure.page.login

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.heyanle.closure.R
import com.heyanle.closure.net.model.WebsiteUser
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.theme.MyApplicationTheme
import com.heyanle.closure.ui.ErrorIcon
import com.heyanle.closure.ui.LoadingIcon
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2022/12/23 16:47.
 * https://github.com/heyanLE
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun Login(
    callback: (WebsiteUser)->Unit,
){

    val scope = rememberCoroutineScope()
    val vm = viewModel<LoginViewModel>()

    var showPassword by remember { mutableStateOf(false) }

    ProgressDialog(show = vm.progressDialog)
    ErrorDialog(show = vm.errorDialog, msg = vm.errorMsg)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(ColorScheme.primary)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(id = R.string.app_name)
                )
            }
            // Text+Link
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.login),
                    fontSize = 20.sp,
                    color = ColorScheme.onBackground
                )
                Text(text = stringResource(id = R.string.closure),
                    fontSize = 20.sp,
                    color = ColorScheme.secondary,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .clickable {
//                        openUrl(Uri.parse("$BASE_URL/account/login"))
                        })
            }
            // 邮箱
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = vm.email.value,
                onValueChange = { vm.email.value = it },
                placeholder = {Text(text = stringResource(id = R.string.input_email))},
                leadingIcon = {
                    Icon(
                        Icons.Filled.Person,
                        stringResource(id = R.string.input_email)
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    textColor = ColorScheme.secondary,
                    cursorColor = ColorScheme.secondary,
                )
            )
            // 密码
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = vm.password.value,
                onValueChange = { vm.password.value = it },
                placeholder = { Text(text = stringResource(id = R.string.input_password)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.VpnKey,
                        stringResource(id = R.string.password)
                    )
                },
                trailingIcon = {
                    Crossfade(targetState = showPassword) {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (it) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                null
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.textFieldColors(

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    textColor = ColorScheme.secondary,
                    cursorColor = ColorScheme.secondary,
                    focusedLabelColor = ColorScheme.secondary,
                )
            )

            Button(colors = ButtonDefaults.buttonColors(containerColor = ColorScheme.secondary),
                modifier = Modifier.fillMaxWidth(), onClick = {
                    if (vm.email.value.isBlank() || vm.password.value.isBlank()) {
                        stringRes(R.string.email_password_empty).toast()
                        return@Button
                    }
                    scope.launch {
                        vm.login (callback)
                    }
                }) {
                Text(text = stringResource(id = R.string.login))
            }

            OutlinedButton(modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ColorScheme.onBackground
                ),
                onClick = {
                if (vm.email.value.isBlank() || vm.password.value.isBlank()) {
                    stringRes(R.string.email_password_empty).toast()
                    return@OutlinedButton
                }
            }) {
                Text(text = stringResource(id = R.string.register))
            }

        }
    }
}

@Composable
fun ProgressDialog(
    show: MutableState<Boolean>
){
    if(show.value){
        AlertDialog(
            title = { Text(stringResource(id = R.string.loading)) },
            icon = { LoadingIcon() },
            onDismissRequest = {},
            confirmButton = {})
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