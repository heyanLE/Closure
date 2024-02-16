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
import com.heyanle.closure.LocalClosureStatePresenter
import com.heyanle.closure.R
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.ui.common.ProgressDialog
import com.heyanle.closure.ui.common.moeSnackBar
import com.heyanle.closure.utils.openUrl
import com.heyanle.closure.utils.stringRes
import kotlinx.coroutines.launch

/**
 * Created by heyanlin on 2023/12/31.
 */
@Composable
fun Login(
    closureController: ClosureController,
) {
    val loginViewModel = viewModel<LoginViewModel>()
    val sta = LocalClosureStatePresenter.current
    val scope = rememberCoroutineScope()

    var username by loginViewModel.username
    var password by loginViewModel.password
    var isPasswordShow by loginViewModel.isPasswordShow

    LoginPage(
        username = username,
        password = password,
        showPassword = isPasswordShow,
        onUsernameChange = {
            username = it
        },
        onPasswordChange = {
            password = it
        },
        onShowPasswordChange = {
            isPasswordShow = it
        },
        onLogin = { loginViewModel.login() },
        onRegister = { loginViewModel.register() },
        onJumpToClosure = {
            "https://closure.ltsc.vip/".openUrl()
        }
    )

    LoginPage(loginViewModel = loginViewModel, onLogin = {
        scope.launch {
            closureController.login(
                loginViewModel.username.value,
                loginViewModel.password.value,
                true
            )
        }
    })

    if (sta.isLogging) {
        ProgressDialog(stringResource(id = com.heyanle.i18n.R.string.logging))
    } else if (sta.isRegistering) {
        ProgressDialog(stringResource(id = com.heyanle.i18n.R.string.registering))
    }




    RegisterDialog(
        show = loginViewModel.showRegisterDialog,
        email = username,
        password = password,
        onCancel = {
            loginViewModel.showRegisterDialog.value = false
        },
        onConfirm = {
            loginViewModel.realRegister()
        }
    )


}

@Composable
fun LoginPage(
    username: String,
    password: String,
    showPassword: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onShowPasswordChange: (Boolean) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onJumpToClosure: () -> Unit,
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
                        onJumpToClosure()
                    }
            )
        }


        // 用户名
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = { onUsernameChange(it) },
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

        // 密码
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = { onPasswordChange(it) },
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
                    IconButton(onClick = { onShowPasswordChange(!it) }) {
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
                    if (username.isEmpty() || password.isEmpty()) {
                        stringRes(com.heyanle.i18n.R.string.email_password_empty).moeSnackBar()
                    } else {
                        onLogin()
                    }
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
                    if (username.isEmpty() || password.isEmpty()) {
                        stringRes(com.heyanle.i18n.R.string.email_password_empty).moeSnackBar()
                    } else {
                        onRegister()
                    }
                }) {
                Text(text = stringResource(id = com.heyanle.i18n.R.string.register))
            }
        }


    }
}

@Composable
fun LoginPage(
    loginViewModel: LoginViewModel,
    onLogin: () -> Unit,
) {


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


