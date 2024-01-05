package com.heyanle.closure.compose.home.screenshot

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.compose.common.moeSnackBar
import com.heyanle.closure.net.api.GameAPI
import com.heyanle.closure.net.model.ScreenshotRsp
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.injekt.core.Injekt

/**
 * Created by HeYanLe on 2023/1/1 14:17.
 * https://github.com/heyanLE
 */
class ScreenshotViewModel: ViewModel() {

    val isLoading = mutableStateOf(false)
    val errorMsg = mutableStateOf("")
    val screenshot = mutableStateListOf<String>()

    private val gameAPI: GameAPI by Injekt.injectLazy()
    private val closureController: ClosureController by Injekt.injectLazy()


    suspend fun refresh(account: String, platform: Long) {
        val token = closureController.token.value.ifEmpty { return }
        isLoading.value = true
        gameAPI.screenshots(token, platform, account).awaitResponseOK()
            .onSuccessful {
                screenshot.clear()
                screenshot.addAll(getUrl(it))
                isLoading.value = false
            }.onFailed { b, s ->
                isLoading.value = false
                errorMsg.value = s
                s.moeSnackBar()
            }
    }

    fun getUrl(resp: List<ScreenshotRsp>): List<String>{
        val res = arrayListOf<String>()
        resp.forEach { re ->
            re.fileName?.forEach {
                res.add(re.url + it)
            }
        }
        return res
    }

}