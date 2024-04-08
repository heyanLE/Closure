package com.heyanle.closure.geetest

import android.app.Activity
import android.content.res.Configuration
import com.geetest.captcha.GTCaptcha4Client
import com.geetest.captcha.GTCaptcha4Config
import com.geetest.sdk.GT3ConfigBean
import com.geetest.sdk.GT3ErrorBean
import com.geetest.sdk.GT3GeetestUtils
import com.geetest.sdk.GT3Listener
import com.heyanle.closure.BuildConfig
import com.heyanle.closure.closure.game.model.CaptchaInfo
import com.heyanle.closure.utils.logi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject


/**
 * Created by heyanlin on 2024/2/4 16:25.
 */
class GeetestHelper(
    private val activity: Activity
) {

    companion object {
        const val TAG = "GeetestHelper"
        const val CAPTCHA_ID = "3d50c20b712aaf5c4390a663f1912941"
    }

    private var gt3: GT3GeetestUtils = GT3GeetestUtils(activity)
    private val config = GTCaptcha4Config.Builder()
        //.setDebug(BuildConfig.DEBUG) // TODO 线上务必关闭
        .setLanguage("zh")
        .setTimeOut(10000)
        .setCanceledOnTouchOutside(true)
        .build()
    private var gt4: GTCaptcha4Client = GTCaptcha4Client.getClient(activity).apply {
        init(CAPTCHA_ID, config)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun onGT3(
        captchaInfo: CaptchaInfo,
    ): String? {
        gt3 ?: return null
        return innerCaptcha(captchaInfo.gt, captchaInfo.challenge)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun onGT4(): String? {
        gt4 ?: null
        return withTimeoutOrNull<String?>(10000) {
            suspendCancellableCoroutine { con ->
                gt4.addOnSuccessListener { b, s ->
                    if (b) {
                        s.logi(TAG)
                        con.resume(s) {
                            it.printStackTrace()
                        }
                    } else {
                        con.resume(null) {
                            it.printStackTrace()
                        }
                    }
                }?.addOnFailureListener {
                    "gt4 onFailureListener".logi(TAG)
                    runCatching {
                        con.cancel()
                    }.onFailure {
                        it.printStackTrace()
                    }

                }?.verifyWithCaptcha()
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun innerCaptcha(gt: String, challenge: String): String? {
        return withTimeoutOrNull<String?>(10000) {
            suspendCancellableCoroutine<String?> { con ->
                val jsonObject = JSONObject()
                jsonObject.put("gt", gt)
                jsonObject.put("challenge", challenge)
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
                gt3ConfigBean.listener = object : GT3Listener() {
                    override fun onReceiveCaptchaCode(p0: Int) {
                        "onReceieCaptchaCode $p0".logi(TAG)
                    }

                    override fun onStatistics(p0: String?) {
                        "onStatistics $p0".logi(TAG)
                    }

                    override fun onClosed(p0: Int) {
                        "onClosed $p0".logi(TAG)
                    }

                    override fun onSuccess(p0: String?) {
                        "onSuccess $p0".logi(TAG)
                    }

                    override fun onFailed(p0: GT3ErrorBean?) {
                        "onFailed $p0".logi(TAG)
                        try {
                            con.cancel()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onButtonClick() {
                        "onButtonClick".logi()
                        gt3ConfigBean.api1Json = jsonObject
                        gt3.getGeetest()
                    }

                    override fun onDialogResult(result: String?) {
                        super.onDialogResult(result)
                        "onDialogResult $result".logi(TAG)
                        con.resume(result) {
                            it.printStackTrace()
                        }
                        gt3.dismissGeetestDialog()
                    }
                }
                // gt3ConfigBean.api1Json = JSONObject(captchaInfo.toJson())
                gt3.init(gt3ConfigBean)
                // 开启验证
                gt3.startCustomFlow()
            }
        }
    }

    fun onConfigurationChanged(config: Configuration) {
        gt4.configurationChanged(config)
    }

    fun onDestroy() {
        gt3.destory()
        gt4.destroy()
    }

}