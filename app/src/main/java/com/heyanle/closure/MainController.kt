package com.heyanle.closure

import androidx.lifecycle.MutableLiveData
import com.heyanle.closure.net.Net
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.net.model.WebsiteUser
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.toast
import com.heyanle.okkv2.core.okkv
import retrofit2.awaitResponse

/**
 * Created by HeYanLe on 2022/12/28 17:28.
 * https://github.com/heyanLE
 */
object MainController {

    private var okkvToken by okkv("token", "")
    val token = MutableLiveData<String>(okkvToken)

    val user = MutableLiveData<WebsiteUser?>(null)

    // 游戏实例
    val gameInstance = MutableLiveData<List<GameResp>?>(null)

    // 选择的游戏实例
    val currentGameInstance = MutableLiveData<GameResp?>(null)

    init {
        token.observeForever {
            okkvToken = it
        }
    }

    suspend fun loginWithToken(
        callback: (Boolean) -> Unit
    ){
        val token = this.token.value ?: return
        Net.auth.login(token).awaitResponseOK().onSuccessful {
            user.postValue(it)
            callback(true)
        }.onFailed { b, s ->
            s.toast()
            callback(false)
        }
    }

}