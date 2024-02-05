package com.heyanle.closure.closure

import com.heyanle.closure.closure.auth.AuthRepository
import com.heyanle.closure.closure.game.GameRepository
import com.heyanle.closure.closure.game.model.GetGameInfo
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.closure.quota.QuotaRepository
import com.heyanle.closure.closure.quota.module.Account
import com.heyanle.closure.ui.common.moeSnackBar
import com.heyanle.closure.utils.hekv.HeKV
import com.heyanle.closure.utils.jsonTo
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * 针对某个 username 管理所有数据，数据位置 rootFolder/username
 * 依赖 username 和 token，该对象必须运行在登录态下
 * 使用工厂模式（为了和 controller 区分叫 presenter）
 * Created by heyanlin on 2024/1/22 11:09.
 */
class ClosurePresenter(
    private val username: String,
    private val rootFolderPath: String,
    private val closureController: ClosureController,
    private val authRepository: AuthRepository,
    private val gameRepository: GameRepository,
    private val quotaRepository: QuotaRepository,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val hekv = HeKV("${rootFolderPath}/${username}", "data")


    private val _account = MutableStateFlow<LoadableData<Account>>(getCacheData("account"))
    val account = _account.asStateFlow()

    private val _webGameList =
        MutableStateFlow<LoadableData<List<WebGame>>>(getCacheData("web_game_list"))
    val webGameList = _webGameList.asStateFlow()


    // 根据 实例 account 缓存对应 stateFlow
    private val getGameMap = hashMapOf<String, MutableStateFlow<LoadableData<GetGameInfo>>>()


    fun onSEEStart() {
        scope.launch {
            _webGameList.update {
                it.copy(
                    isLoading = true
                )
            }
        }
    }

    fun onSEEOpen() {
        scope.launch {
            _webGameList.update {
                it.copy(
                    isLoading = true
                )
            }
        }
    }

    fun onSEEPush(webGameList: List<WebGame>) {
        scope.launch {
            _webGameList.update {
                it.copy(
                    isLoading = false,
                    isError = false,
                    data = webGameList.map {
                        if (it.status.nickName.isNotEmpty()) {
                            hekv.put("nick_name_${it.status.account}", it.status.nickName)
                            it
                        } else {
                            it.copy(
                                status = it.status.copy(nickName = hekv.get("nick_name_${it.status.account}", ""))
                            )
                        }
                    },
                    fromCache = false
                )
            }
        }
    }

    fun refreshAccount() {
        scope.launch {
            val token = closureController.tokenIfNull(username)
            if (token == null) {
                stringRes(com.heyanle.i18n.R.string.waiting_for_login)
                return@launch
            }
            _account.update {
                it.copy(
                    isLoading = true
                )
            }
            val accountResp = quotaRepository.awaitAccount(token)
            accountResp.okWithData { acc ->
                save()
                _account.update {
                    it.copy(
                        isLoading = false,
                        isError = false,
                        fromCache = false,
                        data = acc,
                    )
                }
            }.error { resp ->
                resp.snackWhenError()
                _account.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        fromCache = false,
                        errorMsg = resp.message,
                        throwable = resp.throwable
                    )
                }
            }
        }

    }

    fun refreshGetGameInfoFlow(gameAccount: String) {
        scope.launch {
            val token = closureController.tokenIfNull(username)
            if (token == null) {
                stringRes(com.heyanle.i18n.R.string.waiting_for_login)
                return@launch
            }
            val flow = getGetGameInfoFlow(gameAccount)
            flow.update {
                it.copy(
                    isLoading = true
                )
            }
            val getGameResp = gameRepository.awaitGetGameInfo(gameAccount, token)
            getGameResp.okWithData {
                it.okWithData { info ->
                    save()
                    flow.update {
                        it.copy(
                            isLoading = false,
                            isError = false,
                            fromCache = false,
                            data = info
                        )
                    }
                }.error { resp ->
                    "${stringRes(com.heyanle.i18n.R.string.feature_error)} ${resp.code}:${resp.message}".moeSnackBar()
                    flow.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            errorMsg = resp.message,
                            throwable = null
                        )
                    }
                }
            }.error { resp ->
                resp.snackWhenError()
                resp.throwable?.printStackTrace()
                _webGameList.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMsg = resp.message,
                        throwable = resp.throwable
                    )
                }
            }
        }

    }

    suspend fun getGetGameInfoFlow(gameAccount: String): MutableStateFlow<LoadableData<GetGameInfo>> {
        return withContext(Dispatchers.Main) {
            if (getGameMap.containsKey(gameAccount)) {
                val cur = getGameMap[gameAccount]
                if (cur != null) {
                    return@withContext cur
                }
            }
            val n = innerNewGetGameFlow(gameAccount)
            getGameMap[gameAccount] = n
            return@withContext n
        }
    }

    val initJob = scope.launch {
        val localAccount = hekv.get("account", "").jsonTo<Account>()
        val localWebGameList = hekv.get("webGameList", "[]").jsonTo<List<WebGame>>()
        val localGetGameMap = HashMap<String, GetGameInfo>()
        localWebGameList?.forEach {
            val info = hekv.get("getGameInfo_${it.status.account}", "").jsonTo<GetGameInfo>()
            if (info != null) {
                localGetGameMap[it.status.account] = info
            }
        }
        if (localAccount != null) {
            _account.update {
                it.copy(
                    isLoading = false,
                    isError = false,
                    fromCache = true,
                    data = localAccount
                )
            }
        }
        if (!localWebGameList.isNullOrEmpty()) {
            _webGameList.update {
                it.copy(
                    isLoading = false,
                    isError = false,
                    fromCache = true,
                    data = localWebGameList
                )
            }
        }
        if (localGetGameMap.isNotEmpty()) {
            scope.launch(Dispatchers.Main) {
                localGetGameMap.clear()
                localGetGameMap.asIterable().forEach {
                    val loadable = LoadableData<GetGameInfo>(
                        isLoading = false,
                        isError = false,
                        fromCache = true,
                        data = it.value
                    )
                    val flow = MutableStateFlow(loadable)
                    getGameMap[it.key] = flow
                }
            }

        }
    }

    private suspend fun save() {
        initJob.join()
        withContext(Dispatchers.Main) {
            val account = _account.first().data
            val webGameList = _webGameList.first().data
            val getGameInfoList = ArrayList<GetGameInfo>()
            getGameMap.asIterable().forEach {
                val loaded = it.value.first()
                if (loaded.data != null) {
                    getGameInfoList.add(loaded.data)
                }
            }
            if (account != null) {
                hekv.put("account", account.toJson())
            }
            if (webGameList != null) {
                hekv.put("webGameList", webGameList.toJson())
            }
            getGameInfoList.forEach {
                hekv.put("getGameInfo_${it.config.account}", it.toJson())
            }
        }

    }

    private fun innerNewGetGameFlow(gameAccount: String): MutableStateFlow<LoadableData<GetGameInfo>> {
        return MutableStateFlow(getCacheData("GetGameInfo_${gameAccount}"))
    }

    private inline fun <reified T> getCacheData(key: String): LoadableData<T> {
        val cache = hekv.get(key, "")
        if (cache.isEmpty()) {
            return LoadableData()
        }
        if (T::class.java == String::class.java) {
            return LoadableData(data = cache as T, fromCache = true)
        }
        val data = cache.jsonTo<T>()
        return LoadableData(data = data, fromCache = true)
    }


}