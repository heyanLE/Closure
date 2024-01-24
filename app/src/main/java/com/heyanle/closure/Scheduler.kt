package com.heyanle.closure

import android.app.Application
import com.heyanle.closure.closure.ClosureModule
import com.heyanle.closure.preferences.PreferenceModule
import com.heyanle.closure.setting.SettingModule
import com.heyanle.closure.theme.ThemeModule
import com.heyanle.closure.utils.MoshiArrayListJsonAdapter
import com.heyanle.closure.utils.ssl.CropUtil
import com.heyanle.easy_crasher.CrashHandler
import com.heyanle.easybangumi4.utils.exo_ssl.TrustAllHostnameVerifier
import com.heyanle.injekt.api.addSingletonFactory
import com.heyanle.injekt.core.Injekt
import com.heyanle.okkv2.MMKVStore
import com.heyanle.okkv2.core.Okkv
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

        initInjekt(application)
    }


    /**
     * MainActivity#onCreate
     */
    fun runOnMainActivityCreate(activity: MainActivity, isFirst: Boolean) {
        Migrate.update(activity)
    }

    private fun initInjekt(application: Application){
        Injekt.addSingletonFactory {
            application
        }

        Injekt.addSingletonFactory {
            Moshi.Builder()
                .add(MoshiArrayListJsonAdapter.FACTORY)
                .addLast(KotlinJsonAdapterFactory())
                .build()
        }

        PreferenceModule(application).registerWith(Injekt)
        ClosureModule(application).registerWith(Injekt)
        SettingModule(application).registerWith(Injekt)
        ThemeModule(application).registerWith(Injekt)
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