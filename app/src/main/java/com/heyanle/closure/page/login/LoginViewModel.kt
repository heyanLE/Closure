package com.heyanle.closure.page.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.heyanle.closure.net.Net
import com.heyanle.closure.net.model.LoginReq
import com.heyanle.closure.net.model.WebsiteUser
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.toast
import retrofit2.awaitResponse
import java.net.URLEncoder

/**
 * Created by HeYanLe on 2022/12/23 18:05.
 * https://github.com/heyanLE
 */
class LoginViewModel: ViewModel() {

    val email = mutableStateOf("")
    val password = mutableStateOf("")

    val progressDialog = mutableStateOf(false)

    val errorMsg = mutableStateOf("")
    val errorDialog = mutableStateOf(false)

    val registerDialog = mutableStateOf(false)

    suspend fun login(callback: (WebsiteUser)->Unit){
        progressDialog.value = true
        "eamil: ${email.value}".toast()
        password.value.toast()
        val passStr =  URLEncoder.encode(password.value, "utf-8")
        val res = Net.auth.login(email.value, passStr) .awaitResponseOK()
        res.onSuccessful {
            it?.let { data ->
                progressDialog.value = false
                callback(data)
            }
        }.onFailed { _, msg ->

            showError(msg)
        }
    }

    suspend fun register(callback: (WebsiteUser)->Unit) {
        progressDialog.value = true
        val res = Net.auth.register(LoginReq(email.value, password.value)).awaitResponseOK()
        res.onSuccessful {
            it?.let { data ->
                progressDialog.value = false
                callback(data)
            }
        }.onFailed { _, msg ->
            showError(msg)
        }
    }

    private fun showError(msg: String) {
        progressDialog.value = false
        errorMsg.value = msg
        errorDialog.value = true
    }

}