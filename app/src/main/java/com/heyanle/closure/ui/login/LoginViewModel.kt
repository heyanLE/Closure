package com.heyanle.closure.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.R
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.ui.common.moeSnackBar
import com.heyanle.closure.utils.stringRes
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.launch

/**
 * Created by heyanlin on 2023/12/31.
 */
class LoginViewModel: ViewModel() {


    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val isPasswordShow = mutableStateOf(false)
    val showRegisterDialog = mutableStateOf(false)

    private val closureController: ClosureController by Injekt.injectLazy()

    fun login(){
        val username = username.value
        val password = password.value
        if(username.isEmpty() || password.isEmpty()){
            stringRes(com.heyanle.i18n.R.string.email_password_empty).moeSnackBar()
            return
        }
        viewModelScope.launch {
            closureController.login(username, password, true)
        }

    }

    fun register(){
        val username = username.value
        val password = password.value
        if(username.isEmpty() || password.isEmpty()){
            stringRes(com.heyanle.i18n.R.string.email_password_empty).moeSnackBar()
            return
        }
        showRegisterDialog.value = true
    }

    fun realRegister(){
        val username = username.value
        val password = password.value
        if(username.isEmpty() || password.isEmpty()){
            stringRes(com.heyanle.i18n.R.string.email_password_empty).moeSnackBar()
            return
        }
        viewModelScope.launch {
            closureController.register(username, password)
        }
    }

}