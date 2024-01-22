package com.heyanle.closure.ui.login

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.heyanle.closure.R
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.ui.common.ProgressDialog
import com.heyanle.closure.ui.common.moeSnackBar
import com.heyanle.closure.utils.openUrl
import com.heyanle.closure.utils.stringRes

/**
 * Created by heyanlin on 2023/12/31.
 */
@Composable
fun Login(
    closureController: ClosureController,
) {
    val loginViewModel = viewModel<LoginViewModel>()
    val status = loginViewModel.authState.collectAsState()

    LoginPage(loginViewModel = loginViewModel)

    if (status.value == 1 || status.value == 2) {
        ProgressDialog()
    }

    RegisterDialog(
        show = loginViewModel.showRegisterDialog,
        email = loginViewModel.username.value,
        password = loginViewModel.password.value,
        onCancel = {
            loginViewModel.showRegisterDialog.value = false
        },
        onConfirm = {
            loginViewModel.register(loginViewModel.username.value, loginViewModel.password.value)
        }
    )


}

@Composable
fun LoginPage(
    loginViewModel: LoginViewModel,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
    ) {
        Spacer(modifier = Modifier.size(100.dp))
        Image(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(id = com.heyanle.i18n.R.string.app_name)
        )
        Row(
            Modifier
                .padding(top = 5.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = com.heyanle.i18n.R.string.login),
                fontSize = 20.sp,
            )
            Text(text = stringResource(id = com.heyanle.i18n.R.string.closure),
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .clickable {
                        kotlin
                            .runCatching {
                                "https://arknights.host/".openUrl()
                            }
                            .onFailure {
                                it.moeSnackBar()
                                it.printStackTrace()
                            }

                    })
        }


        // 用户名
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = loginViewModel.username.value,
            onValueChange = { loginViewModel.username.value = it },
            placeholder = { Text(text = stringResource(id = com.heyanle.i18n.R.string.input_email)) },
            leadingIcon = {
                Icon(
                    Icons.Filled.Person,
                    stringResource(id = com.heyanle.i18n.R.string.input_email)
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

        var showPassword by loginViewModel.isPasswordShow

        // 密码
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = loginViewModel.password.value,
            onValueChange = { loginViewModel.password.value = it },
            placeholder = { Text(text = stringResource(id = com.heyanle.i18n.R.string.input_password)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.VpnKey,
                    stringResource(id = com.heyanle.i18n.R.string.password)
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

        Row {
            TextButton(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(),
                onClick = {
                    loginViewModel.login(loginViewModel.username.value, loginViewModel.password.value)
                }) {
                Text(text = stringResource(id = com.heyanle.i18n.R.string.login))
            }

            Spacer(modifier = Modifier.size(16.dp))

            TextButton(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                onClick = {
                    if (loginViewModel.username.value.isEmpty() || loginViewModel.password.value.isEmpty()) {
                        stringRes(com.heyanle.i18n.R.string.email_password_empty).moeSnackBar()
                    } else {
                        loginViewModel.showRegisterDialog.value = true
                        //onRegister(loginViewModel.username.value, loginViewModel.password.value)
                    }
                }) {
                Text(text = stringResource(id = com.heyanle.i18n.R.string.register))
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
                        contentDescription = stringResource(id = com.heyanle.i18n.R.string.register)
                    )
                    Spacer(modifier = Modifier.size(4.dp))

                    Text(stringResource(id = com.heyanle.i18n.R.string.register))
                }
            },
            text = {
                val text =
                    "${stringResource(id = com.heyanle.i18n.R.string.email)} ${email}\n${
                        stringResource(
                            id = com.heyanle.i18n.R.string.password
                        )
                    } ${password}\n${
                        stringResource(
                            id = com.heyanle.i18n.R.string.ask_register
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
                    Text(text = stringResource(id = com.heyanle.i18n.R.string.confirm))
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
                    Text(text = stringResource(id = com.heyanle.i18n.R.string.cancel))
                }
            }
        )
    }
}


