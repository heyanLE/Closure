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

/**
 * Created by HeYanLe on 2022/12/29 17:48.
 * https://github.com/heyanLE
 */
object ItemModel {

    // 理智图标
    const val AP_ICON_URL = "https://ak.dzp.me/dst/items/AP_GAMEPLAY.webp"

    // 源石图标
    const val DIAMOND_ICON_URL = "https://ak.dzp.me/dst/items/DIAMOND.webp"

    // 合成玉图标
    const val DIAMOND_SHD_ICON_URL = "https://ak.dzp.me/dst/items/DIAMOND_SHD.webp"

    // 龙门币图标
    const val GOLD_ICON_URL = "https://ak.dzp.me/dst/items/GOLD.webp"


    private const val URL = "https://arknights.host/data/Items.json"

    private val map: HashMap<String, ItemBean> = hashMapOf()
    val mapLiveData = MutableLiveData<Map<String, ItemBean>>(emptyMap())

    val isLoading = MutableLiveData<Boolean>(false)

    // 一个关卡
    data class ItemBean (
        val id: String,
        val name: String,
        val icon: String,
    ){
        companion object {
            fun parsonFromResp(resp: String): Map<String, ItemBean>{
                val jsonObject = JSONObject(resp)
                val res = HashMap<String, ItemBean>()
                jsonObject.keys().forEach {
                    val o = jsonObject.getJSONObject(it)
                    val name = o.getString("name")
                    val icon = o.getString("icon")

                    val itemBean = ItemBean(
                        id = it,
                        name = name,
                        icon = icon,
                    )
                    res[it] = itemBean
                }
                return res
            }
        }

        fun getIconUrl(): String{
            return "https://ak.dzp.me/dst/items/${icon}.webp"
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
            isLoading.value = true
            GlobalScope.launch {
                Net.okHttpClient.get(URL).onSuccessful {
                    retryCount = 3
                    map.clear()
                    kotlin.runCatching {
                        map.putAll(ItemBean.parsonFromResp(it))
                    }.onFailure {
                        it.printStackTrace()
                        map.clear()
                    }
                    isRefresh.set(false)
                    isLoading.postValue(false)
                    mapLiveData.postValue(map)
                }.onFailed { _, _ ->
                    retryCount --
                    refresh()
                }
            }
        }else{
            isLoading.postValue(false)
            map.clear()
            isRefresh.set(false)
        }
    }






}