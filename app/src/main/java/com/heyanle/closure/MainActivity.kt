package com.heyanle.closure

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.geetest.sdk.GT3ConfigBean
import com.geetest.sdk.GT3ErrorBean
import com.geetest.sdk.GT3GeetestUtils
import com.geetest.sdk.GT3Listener
import com.heyanle.closure.appcenter.ReleaseDialog
import com.heyanle.closure.base.theme.ClosureTheme
import com.heyanle.closure.compose.common.MoeSnackBar
import com.heyanle.closure.net.model.CaptchaInfo
import com.heyanle.closure.utils.toJson
import com.heyanle.closure.compose.Nav
import com.heyanle.closure.utils.MediaUtils
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
        MediaUtils.setIsDecorFitsSystemWindows(this, false)
        setContent {

            CompositionLocalProvider(LocalAct provides this) {
                ClosureTheme {
                    val focusManager = LocalFocusManager.current
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { focusManager.clearFocus() })
                    ) {
                        Surface(
                            color= MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ) {
                            Nav()
                            MoeSnackBar(Modifier.statusBarsPadding())
                        }


                    }
                    ReleaseDialog()
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
        gt3ConfigBean.api1Json = JSONObject(captchaInfo.toJson())
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