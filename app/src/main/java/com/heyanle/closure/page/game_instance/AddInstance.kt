package com.heyanle.closure.page.game_instance

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heyanle.closure.R
import com.heyanle.closure.net.model.CreateGameReq
import com.heyanle.closure.theme.ColorScheme

/**
 * Created by HeYanLe on 2023/1/2 15:46.
 * https://github.com/heyanLE
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInstanceDialog(
    enable: Boolean,
    onDismissRequest: ()->Unit,
    onAdd: (CreateGameReq)->Unit,
){
    var account by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var showPassword by remember {
        mutableStateOf(false)
    }

    var server by remember {
        mutableStateOf(1)
    }

    var serverExpanded by remember {
        mutableStateOf(false)
    }

    if(enable){
        AlertDialog(
            containerColor = ColorScheme.background,
            onDismissRequest = onDismissRequest,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(id = R.drawable.specter),
                        contentDescription = stringResource(id = R.string.add_instance))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = stringResource(id = R.string.add_instance))
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                serverExpanded = true
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(id = R.string.server))
                            Icon(Icons.Filled.ExpandMore, contentDescription = stringResource(id = R.string.server) )
                        }

                        Box(){
                            Box(modifier = Modifier
                                .clip(
                                    CircleShape
                                )
                                .background(ColorScheme.secondary)
                                .padding(8.dp, 4.dp)){
                                Text(
                                    color = ColorScheme.onSecondary,
                                    fontWeight = FontWeight.W900,
                                    text =
                                    if(server < 2)
                                        stringResource(id = R.string.official_server)
                                    else
                                        stringResource(id = R.string.bilibili_server),
                                    fontSize = 12.sp,
                                )
                            }
                            DropdownMenu(
                                expanded = serverExpanded,
                                onDismissRequest = { serverExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        server = 1
                                        serverExpanded = false
                                    },
                                    text = {
                                        Text(text = stringResource(id = R.string.official_server))
                                    }
                                )

                                DropdownMenuItem(
                                    onClick = {
                                        server = 2
                                        serverExpanded = false
                                    },
                                    text = {
                                        Text(text = stringResource(id = R.string.bilibili_server))
                                    }
                                )
                            }
                        }


                    }

                    // ??????
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = account,
                        onValueChange = { account = it },
                        placeholder = {Text(text = stringResource(id = R.string.input_account))},
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Person,
                                stringResource(id = R.string.input_email)
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = ColorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            textColor = ColorScheme.secondary,
                            cursorColor = ColorScheme.secondary,
                        )
                    )

                    // ??????
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = { password = it },
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
                            containerColor = ColorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            textColor = ColorScheme.secondary,
                            cursorColor = ColorScheme.secondary,
                            focusedLabelColor = ColorScheme.secondary,
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorScheme.secondary,
                        contentColor = ColorScheme.onSecondary
                    ),
                    onClick = {
                        onAdd(CreateGameReq(account, password, platform = server))
                    }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorScheme.background,
                        contentColor = ColorScheme.onSurface
                    ),
                    onClick = {
                        onDismissRequest()
                    }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}