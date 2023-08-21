package com.heyanle.closure.compose.login

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
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.LocalAct
import com.heyanle.closure.R
import com.heyanle.closure.compose.common.ErrorDialog
import com.heyanle.closure.compose.common.ProgressDialog
import com.heyanle.closure.compose.common.moeSnackBar
import com.heyanle.closure.utils.openUrl
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toast
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2023/8/20 13:27.
 * https://github.com/heyanLE
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login() {
    val scope = rememberCoroutineScope()
    val vm = viewModel<LoginViewModel>()
    var showPassword by remember { mutableStateOf(false) }

    ProgressDialog(show = vm.progressDialog)
    ErrorDialog(show = vm.errorDialog, msg = vm.errorMsg)
    RegisterDialog(
        show = vm.registerDialog,
        email = vm.email.value,
        password = vm.password.value,
        onCancel = { vm.registerDialog.value = false },
        onConfirm = {
            vm.register()
        }
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(id = R.string.app_name)
            )

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
                    color = MaterialTheme.colorScheme.onBackground
                )
                val act = LocalAct.current
                Text(text = stringResource(id = R.string.closure),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .clickable {
                            kotlin
                                .runCatching {
                                    openUrl(act, "https://arknights.host/")
                                }
                                .onFailure {
                                    it.moeSnackBar()
                                    it.printStackTrace()
                                }

                        })
            }
            // 邮箱
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = vm.email.value,
                onValueChange = { vm.email.value = it },
                placeholder = { Text(text = stringResource(id = R.string.input_email)) },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Person,
                        stringResource(id = R.string.input_email)
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.secondary,
                    selectionColors = TextSelectionColors(
                        handleColor = MaterialTheme.colorScheme.secondary,
                        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                    ),

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
                    Crossfade(targetState = showPassword, label = "") {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (it) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                null
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.secondary,
                    selectionColors = TextSelectionColors(
                        handleColor = MaterialTheme.colorScheme.secondary,
                        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                    ),
                )
            )

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                onClick = {
                    vm.login()
                }) {
                Text(text = stringResource(id = R.string.login))
            }

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = {
                    if(vm.email.value.isEmpty() || vm.password.value.isEmpty()){
                        stringRes(R.string.email_password_empty).moeSnackBar()
                    }else{
                        vm.registerDialog.value = true
                    }
                }) {
                Text(text = stringResource(id = R.string.register))
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterDialog(
    show: MutableState<Boolean>,
    email: String,
    password: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (show.value) {
        AlertDialog(
            onDismissRequest = {
                show.value = false
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

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
                val text =
                    "${stringResource(id = R.string.email)} ${email}\n${stringResource(id = R.string.password)} ${password}\n${
                        stringResource(
                            id = R.string.ask_register
                        )
                    }"
                Text(text)
            },
            confirmButton = {
                TextButton(
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
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    onClick = {
                        onCancel()
                    }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}