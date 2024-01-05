package com.heyanle.closure

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Created by heyanlin on 2023/12/31.
 */
class ClosureApp: Application() {

    init {
        Scheduler.runOnAppInit(this)
    }

    override fun onCreate() {
        super.onCreate()
        Scheduler.runOnAppInit(this)

    }

}