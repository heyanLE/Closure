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
        // 配置bean文件，也可在oncreate初始化
        val gt3ConfigBean = GT3ConfigBean()
        // 设置验证模式，1：bind，2：unbind
        gt3ConfigBean.pattern = 1
        // 设置点击灰色区域是否消失，默认不消息
        gt3ConfigBean.isCanceledOnTouchOutside = false
        // 设置语言，如果为null则使用系统默认语言
        gt3ConfigBean.lang = null
        // 设置加载webview超时时间，单位毫秒，默认10000，仅且webview加载静态文件超时，不包括之前的http请求
        gt3ConfigBean.timeout = 10000
        // 设置webview请求超时(用户点选或滑动完成，前端请求后端接口)，单位毫秒，默认10000
        gt3ConfigBean.webviewTimeout = 10000
        // 设置回调监听
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
        // 开启验证
        gt3.startCustomFlow()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::gt3.isInitialized){
            gt3.destory()
        }
    }

}