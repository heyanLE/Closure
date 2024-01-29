package com.heyanle.closure.closure

import android.content.Context
import android.webkit.WebView
import com.heyanle.closure.closure.auth.AuthRepository
import com.heyanle.closure.closure.auth.model.LoginBody
import com.heyanle.closure.closure.auth.model.RegisterBody
import com.heyanle.closure.preferences.android.AndroidPreferenceStore
import com.heyanle.closure.ui.common.moeSnackBar
import com.heyanle.closure.utils.CoroutineProvider
import com.heyanle.closure.utils.evaluateJavascript
import com.heyanle.closure.utils.logi
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.waitPageFinished
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 登录态管理
 * 有缓存用户密码                   没有缓存密码
 *    ↓                              ↓ 空白数据
 * 登录中（Toast） 登录失败→         登录页
 *      ↘                           ↙
 *              登陆成功
 * Created by heyanlin on 2024/1/22 13:01.
 */
class ClosureController(
    private val rootFolder: String,
    private val context: Context,
    private val androidPreferenceStore: AndroidPreferenceStore,
    private val authRepository: AuthRepository,
) {

    companion object{
        const val TAG = "ClosureController"
    }

    private val scope = CoroutineProvider.mainScope

    private val usernamePref = androidPreferenceStore.getString("username", "")
    private val passwordPref = androidPreferenceStore.getString("password", "")

    data class ClosureState(
        val isLogging: Boolean = false,
        val isRegistering: Boolean = false,
        val isShowPage: Boolean = false,
        val username: String = "",
        val token: String = "",
    )

    private val _state = MutableStateFlow<ClosureState>(ClosureState(username = usernamePref.get()))
    val state = _state.asStateFlow()

    suspend fun awaitToken(username: String): String {
        return state.filter { !it.isRegistering && !it.isLogging && it.token.isNotEmpty() && it.username == username }.map { it.token }.first()
    }

    fun tokenIfNull(username: String): String? {
        val sta = state.value
        if(sta.isRegistering || sta.isLogging || sta.username != username){
            return null
        }
        sta.token.logi(TAG)
        return sta.token
    }

    init {
        scope.launch {
            val username = usernamePref.get()
            val password = passwordPref.get()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                login(username, password, false)
            } else {
                _state.update {
                    it.copy(
                        isLogging = false,
                        isRegistering = false,
                        isShowPage = true,
                        token = ""
                    )
                }
            }
        }
    }

    suspend fun register(username: String, password: String) {
        _state.update {
            it.copy(
                isRegistering = true,
                isLogging = false,
                isShowPage = true,
                token = "",
            )
        }
        authRepository.awaitRegister(getRegister(username, password))
            .okWithData {
                it.okWithData { resp ->
                    usernamePref.set(username)
                    passwordPref.set(password)
                    _state.update {
                        it.copy(
                            isLogging = false,
                            isRegistering = false,
                            isShowPage = false,
                            token = resp.token,
                            username = username,
                        )
                    }
                }.error { resp ->
                    "${stringRes(com.heyanle.i18n.R.string.feature_error)} ${resp.code}:${resp.message}".moeSnackBar()
                    _state.update {
                        it.copy(
                            isLogging = false,
                            isRegistering = false,
                            token = "",
                            isShowPage = true,
                        )
                    }
                }
            }
            .error { resp ->
                resp.snackWhenError()
               resp.throwable?.printStackTrace()
                _state.update {
                    it.copy(
                        isLogging = false,
                        isRegistering = false,
                        token = "",
                        isShowPage = true,
                    )
                }
            }
    }

    suspend fun login(username: String, password: String, fromPage: Boolean) {
        Exception().printStackTrace()
        _state.update {
            it.copy(
                isLogging = true,
                isRegistering = false,
                isShowPage = fromPage,
                token = "",
            )
        }
        authRepository.awaitLogin(LoginBody(username, password)).okWithData {
            it.okWithData { resp ->
                usernamePref.set(username)
                passwordPref.set(password)

                _state.update {
                    username.logi(TAG)
                    it.copy(
                        isLogging = false,
                        isRegistering = false,
                        isShowPage = false,
                        token = resp.token,
                        username = username,
                    )
                }
            }.error { resp ->
                "${stringRes(com.heyanle.i18n.R.string.feature_error)} ${resp.code}:${resp.message}".moeSnackBar()
                _state.update {
                    it.copy(
                        isLogging = false,
                        isRegistering = false,
                        token = "",
                        isShowPage = true,
                    )
                }
            }
        }.error { resp ->
            resp.snackWhenError()
            resp.throwable?.printStackTrace()
            _state.update {
                it.copy(
                    isLogging = false,
                    isRegistering = false,
                    token = "",
                    isShowPage = true,
                )
            }

        }
    }

    private val webView: WebView by lazy {
        WebView(context)
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