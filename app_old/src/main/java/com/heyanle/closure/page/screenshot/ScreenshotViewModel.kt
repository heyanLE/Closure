package com.heyanle.closure.page.screenshot

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heyanle.closure.R
import com.heyanle.closure.net.Net
import com.heyanle.closure.net.model.ScreenshotRsp
import com.heyanle.closure.page.MainController
import com.heyanle.closure.page.data
import com.heyanle.closure.page.error
import com.heyanle.closure.page.loading
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.stringRes

/**
 * Created by HeYanLe on 2023/1/1 14:17.
 * https://github.com/heyanLE
 */
class ScreenshotViewModel: ViewModel() {

    val screenshot = MutableLiveData<MainController.StatusData<List<ScreenshotRsp>>>(MainController.StatusData.None())

    suspend fun refresh(token: String, account: String, platform: Long) {
        screenshot.loading()
        Net.game.screenshots(token, platform, account).awaitResponseOK()
            .onSuccessful {
                if(it != null){
                    screenshot.data(it)
                }else{
                    screenshot.error(stringRes(R.string.net_error))
                }
            }.onFailed { b, s ->
                screenshot.error(s)
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