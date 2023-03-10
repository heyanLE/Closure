package com.heyanle.closure

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.geetest.sdk.GT3ConfigBean
import com.geetest.sdk.GT3ErrorBean
import com.geetest.sdk.GT3GeetestUtils
import com.geetest.sdk.GT3Listener
import com.google.gson.Gson
import com.heyanle.closure.net.model.CaptchaInfo
import com.heyanle.closure.theme.MyApplicationTheme
import com.heyanle.closure.utils.GsonUtil
import com.heyanle.closure.utils.ReleaseDialog
import org.json.JSONObject


/**
 * Created by HeYanLe on 2022/12/23 16:42.
 * https://github.com/heyanLE
 */

val LocalAct = staticCompositionLocalOf<MainActivity> {
    error("MainActivity Not Provide")
}

class MainActivity: ComponentActivity() {

    lateinit var gt3: GT3GeetestUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gt3 = GT3GeetestUtils(this)
        setContent {

            CompositionLocalProvider(LocalAct provides this) {
                MyApplicationTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Nav()
                        ReleaseDialog()
                    }
                }
            }

        }
    }



    fun onCaptcha(captchaInfo: CaptchaInfo, listener: (String)->Unit){
        val jsonObject = JSONObject()
        jsonObject.put("gt", captchaInfo.gt)
        jsonObject.put("challenge", captchaInfo.challenge)
        jsonObject.put("success", 1)
        jsonObject.put("new_captcha", true)
        // ??????bean??????????????????oncreate?????????
        val gt3ConfigBean = GT3ConfigBean()
        // ?????????????????????1???bind???2???unbind
        gt3ConfigBean.pattern = 1
        // ??????????????????????????????????????????????????????
        gt3ConfigBean.isCanceledOnTouchOutside = false
        // ????????????????????????null???????????????????????????
        gt3ConfigBean.lang = null
        // ????????????webview????????????????????????????????????10000?????????webview?????????????????????????????????????????????http??????
        gt3ConfigBean.timeout = 10000
        // ??????webview????????????(??????????????????????????????????????????????????????)????????????????????????10000
        gt3ConfigBean.webviewTimeout = 10000
        // ??????????????????
        gt3ConfigBean.listener = object: GT3Listener() {
            override fun onReceiveCaptchaCode(p0: Int) {
                Log.d("MainActivity", "onReceiveCaptchaCode $p0")
            }

            override fun onStatistics(p0: String?) {
                Log.d("MainActivity", "onStatistics $p0")
            }

            override fun onClosed(p0: Int) {
                Log.d("MainActivity", "onClosed $p0")
            }

            override fun onSuccess(p0: String?) {
                Log.d("MainActivity", "onSuccess $p0")
            }

            override fun onFailed(p0: GT3ErrorBean?) {
                Log.d("MainActivity", "onFailed $p0")
            }

            override fun onButtonClick() {
                Log.d("MainActivity", "onButtonClick")
                gt3ConfigBean.api1Json = jsonObject
                gt3.getGeetest()
            }

            override fun onDialogResult(result: String?) {
                super.onDialogResult(result)
                Log.d("MainActivity", "onDialogResult $result")
                listener(result?:"")
                gt3.dismissGeetestDialog()
            }
        }
        gt3ConfigBean.api1Json = JSONObject(GsonUtil.gson.toJson(captchaInfo))
        gt3.init(gt3ConfigBean)
        // ????????????
        gt3.startCustomFlow()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::gt3.isInitialized){
            gt3.destory()
        }
    }

}