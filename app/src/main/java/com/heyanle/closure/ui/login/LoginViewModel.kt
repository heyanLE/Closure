package com.heyanle.closure.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.heyanle.closure.R
import com.heyanle.closure.closure.auth.AuthController
import com.heyanle.closure.ui.common.MoeDialog
import com.heyanle.closure.ui.common.MoeDialogData
import com.heyanle.closure.utils.koin
import com.heyanle.closure.utils.stringRes

/**
 * Created by heyanlin on 2023/12/31.
 */
class LoginViewModel: ViewModel() {

    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val isPasswordShow = mutableStateOf(false)
    val showRegisterDialog = mutableStateOf(false)

    private val authController: AuthController by koin.inject()
    val authState = authController.status

    fun login(username: String, password: String){
        authController.login(username, password)
    }

    fun register(username: String, password: String){
        authController.register(username, password)
    }

}