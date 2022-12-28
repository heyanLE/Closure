package com.heyanle.closure

import android.app.Application
import com.heyanle.okkv2.MMKVStore
import com.heyanle.okkv2.core.Okkv

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
        Okkv.Builder().store(MMKVStore(this)).cache().build().init().default()
        // 如果不使用缓存，请手动指定 key
        Okkv.Builder().store(MMKVStore(this)).build().init().default("no_cache")
    }
}