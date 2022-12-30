package com.heyanle.closure.page.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.R
import com.heyanle.closure.net.Net
import com.heyanle.closure.net.model.GameLogItem
import com.heyanle.closure.page.MainController
import com.heyanle.closure.page.data
import com.heyanle.closure.page.error
import com.heyanle.closure.page.loading
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toast
import com.heyanle.closure.utils.todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by HeYanLe on 2022/12/23 21:54.
 * https://github.com/heyanLE
 */
class HomeViewModel: ViewModel() {

    companion object {

    }

    val avatarImage = MutableLiveData<Any>(R.drawable.logo)
    val topBarTitle = MutableLiveData<String>(stringRes(R.string.app_name))



    val observer = Observer<MainController.InstanceSelect> {
        viewModelScope.launch {
            loadGetGameResp()
            loadLog()
        }
    }

    val log = MutableLiveData<MainController.StatusData<List<GameLogItem>>>(MainController.StatusData.None())

    suspend fun loadLog(){
        log.loading()
        val current = MainController.current.value
        val account = current?.account?:""
        val platform = current?.platform?:-1L
        val token = MainController.token.value?:""
        Net.game.getLog(token, platform, account, 0).awaitResponseOK()
            .onSuccessful {
                val list = (it?: emptyList()).asReversed()

                log.data(list)
            }.onFailed { b, s ->
                log.error(s)
            }
    }

    init {
        MainController.current.observeForever(observer)
        if(MainController.currentGetGame.value?.isNone() == true){
            val current = MainController.current.value
            val account = current?.account?:""
            val platform = current?.platform?:-1L
            if(account.isNotEmpty() && platform >= 0){
                viewModelScope.launch {
                    loadGetGameResp()
                    loadLog()
                }
            }
        }
    }

    fun def(){
        avatarImage.value = R.drawable.logo
        topBarTitle.value = stringRes(R.string.app_name)
    }


    suspend fun loadGetGameResp(){
        val current = MainController.current.value
        val account = current?.account?:""
        val platform = current?.platform?:-1L
        val token = MainController.token.value?:""
        MainController.currentGetGame.loading()
        Net.game.game(token, platform, account).awaitResponseOK()
            .onSuccessful {
                withContext(Dispatchers.Main){
                    if(it == null){
                        def()
                        MainController.currentGetGame.error(stringRes(R.string.load_error))
                    }else{
                        avatarImage.value = it.status.getSecretaryIconUrl()
                        topBarTitle.value = it.status.nickName
                        MainController.currentGetGame.data(it)
                    }

                }
            }.onFailed { b, s ->
                def()
                MainController.currentGetGame.error(s)
            }
    }

    fun onWarehouse(){
        todo("详情页仓库")
    }

    fun onInstanceConfig(){
        todo("详情页托管配置")
    }

    fun onScreenshot(){
        todo("详情页截图")
    }


    override fun onCleared() {
        super.onCleared()
        MainController.current.removeObserver(observer)
    }

}