package com.heyanle.closure.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.utils.koin

/**
 * Created by heyanlin on 2023/12/31.
 */
class LoginViewModel: ViewModel() {

    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val isPasswordShow = mutableStateOf(false)
    val showRegisterDialog = mutableStateOf(false)


    private val closureController: ClosureController by koin.inject()

}