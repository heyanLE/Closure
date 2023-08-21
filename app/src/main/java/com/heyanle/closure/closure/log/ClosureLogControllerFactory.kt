package com.heyanle.closure.closure.log

import android.content.Context
import com.heyanle.closure.net.api.GameAPI
import com.heyanle.closure.utils.getCachePath
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by HeYanLe on 2023/8/19 19:50.
 * https://github.com/heyanLE
 */
class ClosureLogControllerFactory(
    private val context: Context,
    private val gameAPI: GameAPI,
) {

    private val map = ConcurrentHashMap<Pair<Long, String>, ClosureLogController>()

    fun clear(){
        map.clear()
    }

    fun getRepository(platform: Long, account: String): ClosureLogController {
        val key = platform to account
        return map.getOrPut(key) {
            ClosureLogControllerImpl(getFile(platform, account), account, platform, gameAPI)
        }
    }


    private fun getFile(platform: Long, account: String): File {
        return File(context.getCachePath("${platform}-${account}"), "${platform}-${account}.log")
    }
}