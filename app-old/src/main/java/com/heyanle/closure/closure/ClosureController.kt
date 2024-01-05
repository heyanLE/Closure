package com.heyanle.closure.closure

import android.content.Context
import com.heyanle.closure.base.DataResult
import com.heyanle.closure.base.preferences.PreferenceStore
import com.heyanle.closure.compose.common.moeSnackBar
import com.heyanle.closure.net.api.AuthAPI
import com.heyanle.closure.net.api.CommonAPI
import com.heyanle.closure.net.api.GameAPI
import com.heyanle.closure.net.model.Announcement
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.net.model.LoginReq
import com.heyanle.closure.net.model.WebsiteUser
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.onSuccessfulNullable
import com.heyanle.closure.utils.toDataResult
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLEncoder
import kotlin.coroutines.suspendCoroutine

/**
 * Created by HeYanLe on 2023/8/19 20:58.
 * https://github.com/heyanLE
 */
class ClosureController(
    private val context: Context,
    private val gameAPI: GameAPI,
    private val authAPI: AuthAPI,
    private val commonAPI: CommonAPI,
    private val preferenceStore: PreferenceStore,
) {

    private val scope = MainScope()

    init {
        updateAnnouncement()
    }
    data class GameDataState(
        val loading: Boolean = false,
        val gameList: List<GameResp> = emptyList(),
        val errorMsg: String = ""
    )

    private val tokenPre = preferenceStore.getString("token", "")
    val token = tokenPre.flow().stateIn(scope, SharingStarted.Lazily, tokenPre.get())

    private val _gameData = MutableStateFlow<GameDataState>(GameDataState(true, emptyList()))
    val gameData = _gameData.asStateFlow()

    private val _websiteUser = MutableStateFlow<WebsiteUser?>(null)
    val websiteUser = _websiteUser.asStateFlow()

    data class AnnouncementState(
        val loading: Boolean = false,
        val anno: Announcement? = null,
        val errorMsg: String = ""
    )

    private val _announcement = MutableStateFlow<AnnouncementState>(AnnouncementState(true, null))
    val announcement = _announcement.asStateFlow()

    suspend fun login(email: String, password: String): DataResult<WebsiteUser> {
//        val passStr = URLEncoder.encode(password, "utf-8")
        return authAPI.login(email, password).awaitResponseOK()
            .onSuccessful { user ->
                _websiteUser.update {
                    user
                }
                tokenPre.set(user.token)
                updateGameList()
            }
            .onFailed { b, s ->
                tokenPre.set("")
                s.moeSnackBar()
            }.toDataResult()
    }

    suspend fun register(email: String, password: String): DataResult<WebsiteUser> {
        val passStr = URLEncoder.encode(password, "utf-8")
        return authAPI.register(LoginReq(email, passStr)).awaitResponseOK()
            .onSuccessful { user ->
                _websiteUser.update {
                    user
                }
                tokenPre.set(user.token)
                updateGameList()
            }
            .onFailed { b, s ->
                tokenPre.set("")
                s.moeSnackBar()
            }.toDataResult()
    }

    fun clearToken(){
        tokenPre.set("")
    }

    fun updateGameList() {
        scope.launch {
            if (token.value.isNotEmpty()) {
                _gameData.update {
                    it.copy(loading = true)
                }
                gameAPI.get(token.value).awaitResponseOK()
                    .onSuccessfulNullable { list ->
                        _gameData.update {
                            it.copy(loading = false, list?: emptyList(), "")
                        }
                    }
                    .onFailed { b, s ->
                        s.moeSnackBar()
                        _gameData.update {
                            it.copy(loading = false, emptyList(), s)
                        }
                    }
            }
        }
    }

    fun updateWebsiteUser() {
        scope.launch {
            if (token.value.isNotEmpty()) {
                authAPI.auth(token.value).awaitResponseOK()
                    .onSuccessful { we ->
                        _websiteUser.update {
                            we
                        }
                    }
                    .onFailed { b, s ->
                        s.moeSnackBar()
                        _websiteUser.update { null }
                    }
            }
        }
    }

    fun updateAnnouncement() {
        scope.launch {
            _announcement.update {
                it.copy(loading = true)
            }
            commonAPI.getAnnouncement().awaitResponseOK()
                .onSuccessful { an ->
                    _announcement.update {
                        it.copy(false, an, "")
                    }
                }
                .onFailed { b, s ->
                    s.moeSnackBar()
                    _announcement.update {
                        it.copy(false, null, s)
                    }
                }
        }
    }


}