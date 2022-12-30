package com.heyanle.closure.page

import androidx.compose.runtime.Composable
import androidx.lifecycle.MutableLiveData
import com.heyanle.closure.R
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.net.model.GetGameResp
import com.heyanle.closure.utils.stringRes
import com.heyanle.okkv2.core.okkv

/**
 * Created by HeYanLe on 2022/12/28 17:28.
 * https://github.com/heyanLE
 */
object MainController {

    sealed class StatusData<T>{
        class None<T>: StatusData<T>()
        data class Loading<T>(
            val loadingText: String = stringRes(R.string.loading)
        ): StatusData<T>()

        data class Error<T>(
            val errorMsg: String = "",
            val throwable: Throwable? = null,
        ): StatusData<T>()

        data class Data<T>(
            val data: T
        ): StatusData<T>()

        fun isNone(): Boolean {
            return this is None
        }

        fun isLoading(): Boolean {
            return this is Loading
        }

        fun isError(): Boolean {
            return this is Error
        }

        fun isData(): Boolean {
            return this is Data
        }

        @Composable
        fun onLoading(content: @Composable (Loading<T>)->Unit): StatusData<T> {
            (this as? Loading)?.let {
                content(it)
            }
            return this
        }

        @Composable
        fun onError(content: @Composable (Error<T>)->Unit): StatusData<T> {
            (this as? Error)?.let {
                content(it)
            }
            return this
        }

        @Composable
        fun onData(content: @Composable (Data<T>)->Unit): StatusData<T> {
            (this as? Data)?.let {
                content(it)
            }
            return this
        }

    }

    data class InstanceSelect(
        val account: String,
        val platform: Long,
    )

    var okkvToken by okkv("token", "")
    var okkvCurrentAccount by okkv("currentAccount", "")
    var okkvCurrentPlatform by okkv("currentPlatform", -1L)


    val token = MutableLiveData<String>(okkvToken)

    // 当前选择
    val current = MutableLiveData<InstanceSelect>(InstanceSelect(okkvCurrentAccount, okkvCurrentPlatform))

    val instance = MutableLiveData<StatusData<List<GameResp>>>(StatusData.None())
    val currentInstance = MutableLiveData<StatusData<GameResp>>(StatusData.None())
    val currentGetGame = MutableLiveData<StatusData<GetGameResp>>(StatusData.None())


    init {
        // livedata 持久化
        token.observeForever {
            okkvToken = it
        }
        current.observeForever {
            okkvCurrentAccount = it.account
            okkvCurrentPlatform = it.platform
        }
    }


}

fun <T> MutableLiveData<MainController.StatusData<T>>.loading(loadingText: String = stringRes(R.string.loading)){
    value = MainController.StatusData.Loading(loadingText)
}

fun <T> MutableLiveData<MainController.StatusData<T>>.error(errorMsg: String = "", throwable: Throwable? = null,){
    value = MainController.StatusData.Error(errorMsg, throwable)
}

fun <T> MutableLiveData<MainController.StatusData<T>>.data(data: T){
    value = MainController.StatusData.Data(data)
}
