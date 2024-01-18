package com.heyanle.closure.closure.auth

import android.content.Context
import android.webkit.WebView
import com.heyanle.closure.R
import com.heyanle.closure.closure.auth.model.AuthResp
import com.heyanle.closure.closure.auth.model.LoginBody
import com.heyanle.closure.closure.auth.model.LoginResp
import com.heyanle.closure.closure.auth.model.RegisterBody
import com.heyanle.closure.closure.net.NetResponse
import com.heyanle.closure.preferences.android.AndroidPreferenceStore
import com.heyanle.closure.ui.common.moeDialog
import com.heyanle.closure.ui.common.moeSnackBar
import com.heyanle.closure.utils.CoroutineProvider
import com.heyanle.closure.utils.evaluateJavascript
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.waitPageFinished
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.context.startKoin
import org.koin.core.module._scopedInstanceFactory

/**
 * Created by heyanlin on 2023/12/31.
 */
class AuthController(
    private val context: Context,
    private val preferenceStore: AndroidPreferenceStore,
    private val authRepository: AuthRepository,
) {

    private val scope = CoroutineProvider.mainScope

    private val usernamePref = preferenceStore.getString("username", "")
    val username = usernamePref.stateIn(scope)

    private val passwordPref = preferenceStore.getString("password", "")
    val password = passwordPref.stateIn(scope)

    private val tokenPref = preferenceStore.getString("token", "")
    val token = tokenPref.stateIn(scope)


    // 0 -> idle 1 -> logging 2 -> registering
    private val _status = MutableStateFlow(0)
    val status = _status.asStateFlow()

    private val webView: WebView by lazy {
        WebView(context)
    }

    init {

    }

    fun login(email: String, password: String) {
        scope.launch {
            _status.update { 1 }
            authRepository.awaitLogin(LoginBody(email, password))
                .okWithData {
                    it.okWithData {
                        stringRes(com.heyanle.i18n.R.string.login_complete)
                        tokenPref.set(it.token)
                    }.error {
                        // 业务错误
                        "${stringRes(com.heyanle.i18n.R.string.feature_error)} ${it.code} ${it.message}".moeSnackBar()
                        tokenPref.set("")
                    }
                }
                .error {
                    // 网络错误
                    "${stringRes(com.heyanle.i18n.R.string.net_error)} ${it.code} ${it.message}".moeSnackBar()
                    tokenPref.set("")
                }
            _status.update { 0 }
        }

    }

    fun register(email: String, password: String) {
        scope.launch {
            _status.update { 2 }
            authRepository.awaitRegister(getRegister(email, password))
                .okWithData {
                    it.okWithData {
                        stringRes(com.heyanle.i18n.R.string.register_complete)
                        tokenPref.set(it.token)
                    }.error {
                        // 业务错误
                        "${stringRes(com.heyanle.i18n.R.string.feature_error)} ${it.code} ${it.message}".moeSnackBar()
                        tokenPref.set("")
                    }
                }
                .error {
                    // 网络错误
                    "${stringRes(com.heyanle.i18n.R.string.net_error)} ${it.code} ${it.message}".moeSnackBar()
                    tokenPref.set("")
                }
            _status.update { 0 }
        }
    }

    private suspend fun getRegister(email: String, password: String): RegisterBody {
        return withContext(Dispatchers.Main) {
            webView.loadUrl("file:///android_assets/register/Skadi.html")
            webView.waitPageFinished()
            val noise = webView.evaluateJavascript("noise()")
            val sign = webView.evaluateJavascript("sign(\"${email}\",\"${password}\",\"${noise}\")")
            RegisterBody(email, password, sign, noise)
        }

    }

}