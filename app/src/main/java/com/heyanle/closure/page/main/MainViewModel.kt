package com.heyanle.closure.page.main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heyanle.closure.R
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.utils.stringRes

/**
 * Created by HeYanLe on 2022/12/23 21:54.
 * https://github.com/heyanLE
 */
class MainViewModel: ViewModel() {

    val currentGameInstance = MutableLiveData<GameResp?>()
    val isGameInstancePageShow = MutableLiveData<Boolean>(false)

    val avatarImage = MutableLiveData<Any>(R.drawable.logo)
    val topBarTitle = MutableLiveData<String>(stringRes(R.string.please_choose_instance))


    fun init(){

    }

}