package com.heyanle.closure.closure.items

import org.json.JSONObject

/**
 * Created by HeYanLe on 2023/8/12 14:14.
 * https://github.com/heyanLE
 */
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