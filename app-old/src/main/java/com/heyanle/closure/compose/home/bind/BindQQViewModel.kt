package com.heyanle.closure.compose.home.bind

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.heyanle.closure.R
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.net.Net
import com.heyanle.closure.net.api.AuthAPI
import com.heyanle.closure.net.model.BindQQResponseWait
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.stringRes
import com.heyanle.injekt.core.Injekt

import kotlinx.coroutines.launch

/**
 * Created by HeYanLe on 2023/3/12 15:47.
 * https://github.com/heyanLE
 */
class BindQQViewModel : ViewModel() {


    sealed class BindQQState {
        object None : BindQQState()

        object Loading : BindQQState()

        class Sus(val qqCode: String) : BindQQState()

        class Error(val errorMsg: String) : BindQQState()

        class Wait(val bindQQResponseWait: BindQQResponseWait) : BindQQState()
    }

    var state by mutableStateOf<BindQQState>(BindQQState.None)
    private val authApi: AuthAPI by Injekt.injectLazy()
    private val closureController: ClosureController by Injekt.injectLazy()
    fun refresh() {
        val token = closureController.token.value.ifEmpty { return }
        viewModelScope.launch {
            state = BindQQState.Loading
            val resp = authApi.bindQQ(token).awaitResponseOK()
                .response?.body()?.string()
            if (resp == null) {
                state = BindQQState.Error(stringRes(R.string.net_error))
            } else {

                val parser = JsonParser.parseString(resp).asJsonObject
                val code = parser.get("code").asInt
                when (code) {
                    2 -> {
                        state = BindQQState.Sus(parser.get("data").asString)
                    }

                    1 -> {
                        state = BindQQState.Wait(
                            BindQQResponseWait.fromJsonObject(
                                parser.getAsJsonObject("data")
                            )
                        )
                    }

                    0 -> {
                        state = BindQQState.Error(parser.get("message").asString)
                    }
                }
            }


        }
    }

}