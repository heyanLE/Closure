package com.heyanle.closure

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import com.heyanle.closure.appcenter.AppCenterUpdateController
import com.heyanle.closure.crash.CrashHandler
import com.heyanle.injekt.core.Injekt
import com.heyanle.okkv2.MMKVStore
import com.heyanle.okkv2.core.Okkv
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.DistributeListener
import com.microsoft.appcenter.distribute.ReleaseDetails

/**
 * Created by HeYanLe on 2023/8/12 11:17.
 * https://github.com/heyanLE
 */
lateinit var APP: ClosureApp

class ClosureApp : Application() {

    init {
        RootModule.registerWith(Injekt)
    }

    override fun onCreate() {
        super.onCreate()
        APP = this
        if (isMainProcess()) {
            PreferencesModule(this).registerWith(Injekt)
            ControllerModule(this).registerWith(Injekt)
            initCrasher()
            initOkkv()
            initAppCenter()
        }
    }

    private fun initAppCenter() {
        if (!BuildConfig.DEBUG) {
            kotlin.runCatching {
                // https://appcenter.ms
                Distribute.disableAutomaticCheckForUpdate();
                AppCenter.start(
                    this, "2fb2410f-d255-41b1-8560-360dc66ee30c",
                    Analytics::class.java, Crashes::class.java, Distribute::class.java
                )
                Distribute.setListener(object : DistributeListener {
                    override fun onReleaseAvailable(
                        activity: Activity?,
                        releaseDetails: ReleaseDetails?
                    ): Boolean {
                        releaseDetails?.let {
                            val updateController: AppCenterUpdateController by Injekt.injectLazy()
                            updateController.releaseDetail.value = it
                            updateController.showReleaseDialog.value = true
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

    private fun initCrasher() {
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
    }

    private fun initOkkv() {
        Okkv.Builder(MMKVStore(this)).cache().build().init().default()
        // 如果不使用缓存，请手动指定 key
        Okkv.Builder(MMKVStore(this)).build().init().default("no_cache")
    }

    private fun isMainProcess(): Boolean {
        return packageName == if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getProcessName()
        } else {
            getProcessName(this) ?: packageName
        }

    }

    private fun getProcessName(cxt: Context): String? {
        val pid = Process.myPid()
        val am = cxt.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName
            }
        }
        return null
    }
}