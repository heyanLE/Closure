package com.heyanle.closure.compose.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.R
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.compose.common.moeSnackBar
import com.heyanle.closure.utils.stringRes
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2023/8/20 13:27.
 * https://github.com/heyanLE
 */
class LoginViewModel : ViewModel() {

    val email = mutableStateOf("")
    val password = mutableStateOf("")

    val progressDialog = mutableStateOf(false)

    val errorMsg = mutableStateOf("")
    val errorDialog = mutableStateOf(false)

    val registerDialog = mutableStateOf(false)

    private val controller: ClosureController by Injekt.injectLazy()

    fun login() {

        val e = email.value.ifEmpty {
            stringRes(R.string.email_password_empty).moeSnackBar()
            return
        }
        val p = password.value.ifEmpty {
            stringRes(R.string.email_password_empty).moeSnackBar()
            return
        }
        progressDialog.value = true
        viewModelScope.launch {
            controller.login(e, p)
                .onOK {
                    progressDialog.value = false
                }
                .onError {
                    showError(it.errorMsg)
                }
        }

    }

    fun register() {

        val e = email.value.ifEmpty {
            stringRes(R.string.email_password_empty).moeSnackBar()
            return
        }
        val p = password.value.ifEmpty {
            stringRes(R.string.email_password_empty).moeSnackBar()
            return
        }
        progressDialog.value = true
        viewModelScope.launch {
            controller.register(e, p)
                .onOK {
                    progressDialog.value = false
                }
                .onError {
                    showError(it.errorMsg)
                }
        }
    }

    private fun showError(msg: String) {
        progressDialog.value = false
        errorMsg.value = msg
        errorDialog.value = true
    }

}