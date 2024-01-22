package com.heyanle.closure.closure

import com.heyanle.closure.R
import com.heyanle.closure.closure.auth.AuthRepository
import com.heyanle.closure.closure.auth.model.LoginBody
import com.heyanle.closure.closure.game.GameRepository
import com.heyanle.closure.closure.game.model.GetGameInfo
import com.heyanle.closure.closure.game.model.WebGame
import com.heyanle.closure.closure.quota.QuotaRepository
import com.heyanle.closure.closure.quota.module.Account
import com.heyanle.closure.ui.common.moeSnackBar
import com.heyanle.closure.utils.CoroutineProvider
import com.heyanle.closure.utils.hekv.HeKV
import com.heyanle.closure.utils.jsonTo
import com.heyanle.closure.utils.stringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read

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

    private val scope = CoroutineProvider.mainScope
    private val hekv = HeKV(rootFolderPath, username)

    private val account = MutableStateFlow<LoadableData<Account>>(getCacheData("account"))
    private val webGameList =
        MutableStateFlow<LoadableData<List<WebGame>>>(getCacheData("web_game_list"))


    // 根据 实例 account 缓存对应 stateFlow
    private val getGameMap = hashMapOf<String, MutableStateFlow<LoadableData<GetGameInfo>>>()
    private val readWriteLock = ReentrantReadWriteLock()


    fun refreshAccount() {}

    fun refreshWebGameList() {}

    fun refreshGetGameInfoFlow(gameAccount: String) {}

    fun getGetGameInfoFlow(gameAccount: String): MutableStateFlow<LoadableData<GetGameInfo>> {
        val rl = readWriteLock.readLock()
        val wl = readWriteLock.writeLock()
        try {
            rl.lock()
            if (getGameMap.containsKey(gameAccount)) {
                val cur = getGameMap[gameAccount]
                if (cur != null) {
                    rl.unlock()
                    return cur
                }
            }
            wl.lock()
            rl.unlock()
            val n = innerNewGetGameFlow(gameAccount)
            getGameMap[gameAccount] = n
            wl.unlock()
            return n
        } finally {
            runCatching {
                rl.unlock()
            }
            runCatching {
                wl.unlock()
            }
        }
    }

    init {

        // 持久化数据
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