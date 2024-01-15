package com.heyanle.closure

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import com.heyanle.closure.closure.auth.authModule
import com.heyanle.closure.closure.net.netModule
import com.heyanle.closure.preferences.preferenceModule
import com.heyanle.closure.utils.ssl.CropUtil
import com.heyanle.easy_crasher.CrashHandler
import com.heyanle.easybangumi4.utils.exo_ssl.TrustAllHostnameVerifier
import com.heyanle.okkv2.MMKVStore
import com.heyanle.okkv2.core.Okkv
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.DistributeListener
import com.microsoft.appcenter.distribute.ReleaseDetails
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import javax.net.ssl.HttpsURLConnection

/**
 * 全局初始化时点分发
 * Created by HeYanLe on 2023/10/29 14:39.
 * https://github.com/heyanLE
 */
object Scheduler {

    /**
     * application#init
     */
    fun runOnAppInit(application: Application) {

    }

    /**
     * application#onCreate
     */
    fun runOnAppCreate(application: Application) {
        initCrasher(application)


        initAppCenter(application)
        initOkkv(application)

        initTrustAllHost()

        initKoin(application)
    }


    /**
     * MainActivity#onCreate
     */
    fun runOnMainActivityCreate(activity: MainActivity, isFirst: Boolean) {
        Migrate.update(activity)
    }


    private fun initKoin(application: Application){
        startKoin {
            androidLogger(Level.INFO)
            androidContext(application)

            modules(
                netModule,
                preferenceModule,
                authModule,

            )
        }
    }


    /**
     * 全局异常捕获 + crash 界面
     */
    private fun initCrasher(application: Application){
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(application))
    }

    /**
     * 允许 http 链接
     */
    private fun initTrustAllHost(){
        HttpsURLConnection.setDefaultSSLSocketFactory(CropUtil.getUnsafeSslSocketFactory())
        HttpsURLConnection.setDefaultHostnameVerifier(TrustAllHostnameVerifier())
    }


    /**
     * 初始化 App Center
     */
    private fun initAppCenter(application: Application){

    }

    /**
     * 初始化 okkv
     */
    private fun initOkkv(application: Application){
        Okkv.Builder(MMKVStore(application)).cache().build().init().default()
        // 如果不使用缓存，请手动指定 key
        Okkv.Builder(MMKVStore(application)).build().init().default("no_cache")
    }
}