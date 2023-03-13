package com.heyanle.closure

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import com.heyanle.closure.crash.CrashHandler
import com.heyanle.closure.model.ItemModel
import com.heyanle.closure.model.StageModel
import com.heyanle.closure.utils.AppCenterManager
import com.heyanle.okkv2.MMKVStore
import com.heyanle.okkv2.core.Okkv
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.DistributeListener
import com.microsoft.appcenter.distribute.ReleaseDetails

/**
 * Created by HeYanLe on 2022/12/23 15:50.
 * https://github.com/heyanLE
 */

lateinit var app: ClosureApp

class ClosureApp: Application() {



    override fun onCreate() {
        super.onCreate()
        initOkkv()
        app = this
    }

    private fun initOkkv(){
        Okkv.Builder(MMKVStore(this)).cache().build().init().default()
        // 如果不使用缓存，请手动指定 key
        Okkv.Builder(MMKVStore(this)).build().init().default("no_cache")

        StageModel.refresh()
        ItemModel.refresh()

        initCrasher()

        initAppCenter()

    }

    private fun initCrasher(){
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
    }

    private fun initAppCenter(){
        kotlin.runCatching {
            // https://appcenter.ms
            Distribute.disableAutomaticCheckForUpdate();
            AppCenter.start(
                this, "2fb2410f-d255-41b1-8560-360dc66ee30c",
                Analytics::class.java, Crashes::class.java, Distribute::class.java
            )
            Distribute.setListener(object: DistributeListener{
                override fun onReleaseAvailable(
                    activity: Activity?,
                    releaseDetails: ReleaseDetails?
                ): Boolean {
                    releaseDetails?.let {
                        AppCenterManager.releaseDetail.value = it
                        AppCenterManager.showReleaseDialog.value = true
                    }
                    return true
                }

                override fun onNoReleaseAvailable(activity: Activity?) {

                }
            })
            //Distribute.checkForUpdate()
        }.onFailure {
            it.printStackTrace()
        }

    }
}