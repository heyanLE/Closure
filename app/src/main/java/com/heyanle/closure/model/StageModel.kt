package com.heyanle.closure.model

import androidx.lifecycle.MutableLiveData
import com.heyanle.closure.net.Net
import com.heyanle.closure.utils.get
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * Created by HeYanLe on 2022/12/29 17:05.
 * https://github.com/heyanLE
 */
object StageModel {

    private const val URL = "https://arknights.host/data/Stage.json"

    private val map: HashMap<String, Stage> = hashMapOf()
    val mapLiveData = MutableLiveData<Map<String, Stage>>(emptyMap())

    // 一个关卡
    data class Stage (
        val id: String,
        val name: String,
        val code: String,
        val ap: Int,
        val items: List<String>,
    ){
        companion object {
            fun parsonFromResp(resp: String): Map<String, Stage>{
                val jsonObject = JSONObject(resp)
                val res = HashMap<String, Stage>()
                jsonObject.keys().forEach {
                    val o = jsonObject.getJSONObject(it)
                    val name = o.getString("name")
                    val code = o.getString("code")
                    val ap = o.getInt("ap")
                    val items = o.getJSONArray("items")
                    val item = arrayListOf<String>()
                    for(i in 0 until items.length()){
                        item.add(items.getString(i))
                    }
                    val stage = Stage(
                        id = it,
                        name = name,
                        code = code,
                        ap = ap,
                        items = item,
                    )
                    res[it] = stage
                }
                return res
            }
        }
    }


    @Volatile
    private var retryCount = 3

    private val isRefresh = AtomicBoolean(false)

    fun refresh(){
        if(isRefresh.compareAndSet(false, true)){
            retryCount = 3
            tryRefresh()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun tryRefresh(){
        if(retryCount > 0){
            GlobalScope.launch {
                Net.okHttpClient.get(URL).onSuccessful {
                    retryCount = 3
                    map.clear()
                    kotlin.runCatching {
                        map.putAll(Stage.parsonFromResp(it))
                    }.onFailure {
                        it.printStackTrace()
                        map.clear()
                    }
                    mapLiveData.postValue(map)
                    isRefresh.set(false)
                }.onFailed { _, _ ->
                    retryCount --
                    refresh()
                }
            }
        }else{
            map.clear()
            isRefresh.set(false)
        }
    }






}