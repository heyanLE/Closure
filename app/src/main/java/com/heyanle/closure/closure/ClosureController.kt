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
import com.heyanle.closure.utils.hekv.HeKV
import com.heyanle.closure.utils.koin
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.waitPageFinished
import io.ktor.http.parameters
import io.ktor.http.parametersOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parameterArrayOf
import org.koin.core.parameter.parametersOf
import java.util.concurrent.locks.ReentrantReadWriteLock

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

    private val scope = CoroutineProvider.mainScope
    private val presenterMap = hashMapOf<String, ClosurePresenter>()
    private val readWriteLock = ReentrantReadWriteLock()

    private val usernamePref = androidPreferenceStore.getString("username", "")
    private val passwordPref = androidPreferenceStore.getString("password", "")

    data class ClosureState(
        val isLogging: Boolean = false,
        val isRegistering: Boolean = false,
        val isShowPage: Boolean = false,
        val username: String = "",
        val token: String = "",
    )

    private val _state = MutableStateFlow<ClosureState>(ClosureState())
    val state = _state.asStateFlow()


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
                "${stringRes(com.heyanle.i18n.R.string.net_error)} ${resp.code}:${resp.message ?: resp.throwable?.message ?: ""}".moeSnackBar()
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
            "${stringRes(com.heyanle.i18n.R.string.net_error)} ${resp.code}:${resp.message ?: resp.throwable?.message ?: ""}".moeSnackBar()
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

    fun getPresenter(username: String): ClosurePresenter {
        val rl = readWriteLock.readLock()
        val wl = readWriteLock.writeLock()
        try {
            rl.lock()
            if (presenterMap.containsKey(username)) {
                val cur = presenterMap[username]
                if (cur != null) {
                    rl.unlock()
                    return cur
                }
            }
            wl.lock()
            rl.unlock()
            val closurePresenter: ClosurePresenter = koin.get { parameterArrayOf(username, rootFolder) }
            presenterMap[username] = closurePresenter
            wl.unlock()
            return closurePresenter
        } finally {
            runCatching {
                rl.unlock()
            }
            runCatching {
                wl.unlock()
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